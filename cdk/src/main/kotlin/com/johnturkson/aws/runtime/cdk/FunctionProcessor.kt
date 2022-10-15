package com.johnturkson.aws.runtime.cdk

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.getAnnotationsByType
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.johnturkson.aws.runtime.annotations.Architecture
import com.johnturkson.aws.runtime.annotations.Function
import com.johnturkson.aws.runtime.annotations.Memory
import com.johnturkson.aws.runtime.annotations.Timeout

class FunctionProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger,
    private val options: Map<String, String>,
) : SymbolProcessor {
    override fun process(resolver: Resolver): List<KSAnnotated> {
        val handlers = resolver.getSymbolsWithAnnotation(Function::class.qualifiedName!!)
        val handlerClasses = handlers.filterIsInstance<KSClassDeclaration>().toList()
        val handlerFunctions = handlers.filterIsInstance<KSFunctionDeclaration>().toList()
        
        handlerClasses.forEach { resourceClass -> generateFunctionClass(resourceClass) }
        handlerFunctions.forEach { resourceClass -> generateFunctionClass(resourceClass) }
        if (handlerClasses.toList().isNotEmpty()) generateFunctionsClass(handlerClasses, handlerFunctions)
        
        return emptyList()
    }
    
    @OptIn(KspExperimental::class)
    private fun generateFunctionClass(handlerClass: KSDeclaration) {
        val resourceClassName = handlerClass.simpleName.asString()
        val generatedPackageName = requireNotNull(options["OUTPUT_LOCATION"]) + ".infrastructure"
        val handlerName = handlerClass.qualifiedName?.asString()
        val handlerLocation = requireNotNull(options["HANDLER_LOCATION"])
        
        val timeout = handlerClass.getAnnotationsByType(Timeout::class).firstOrNull()
        val memory = handlerClass.getAnnotationsByType(Memory::class).firstOrNull()
        val architecture = handlerClass.getAnnotationsByType(Architecture::class).firstOrNull()
        
        val imports = """
            import software.amazon.awscdk.Duration
            import software.amazon.awscdk.services.lambda.Architecture
            import software.amazon.awscdk.services.lambda.Code
            import software.amazon.awscdk.services.lambda.Function
            import software.amazon.awscdk.services.lambda.Runtime
            import software.constructs.Construct
        """.trimIndent()
        
        val generatedClass = """
            package $generatedPackageName
            
            $imports
            
            object $resourceClassName {
                private val instances = mutableMapOf<Construct, Function>()
                
                fun builder(construct: Construct, configuration: Function.Builder.() -> Unit = {}): Function.Builder {
                    return Function.Builder.create(construct, "$resourceClassName")
                        .handler("$handlerName")
                        .code(Code.fromAsset("$handlerLocation"))
                        .runtime(Runtime.PROVIDED_AL2)
                        ${architecture?.let { ".architecture(Architecture.${architecture.value})" } ?: ""}
                        ${timeout?.let { ".timeout(Duration.seconds(${timeout.value}))" } ?: ""}
                        ${memory?.let { ".memorySize(${memory.value})" } ?: ""}
                        .apply(configuration)
                }
                
                fun build(construct: Construct, onRebuildBehavior: (Function) -> Unit = {}, configuration: Function.Builder.() -> Unit = {}): Function {
                    val instance = instances[construct]
                    return if (instance != null) {
                        onRebuildBehavior(instance)
                        instance
                    } else {
                        val function = builder(construct, configuration).build()
                        instances[construct] = function
                        function
                    }
                }
            }
        """.trimIndent()
        
        val generatedResourceBuilderFile = codeGenerator.createNewFile(
            Dependencies(false, handlerClass.containingFile!!),
            generatedPackageName,
            resourceClassName,
            "kt"
        )
        
        generatedResourceBuilderFile.bufferedWriter().use { writer -> writer.write(generatedClass) }
    }
    
    private fun generateFunctionsClass(
        handlerClasses: List<KSClassDeclaration>,
        handlerFunctions: List<KSFunctionDeclaration>,
    ) {
        val generatedPackageName = requireNotNull(options["OUTPUT_LOCATION"]) + ".infrastructure"
        val generatedClassName = "Functions"
        
        val imports = """
            import software.amazon.awscdk.services.lambda.Function
            import software.constructs.Construct
        """.trimIndent()
        
        val builders = listOf(handlerClasses, handlerFunctions)
            .flatten()
            .map { handler -> handler.simpleName.asString() }
            .distinct()
            .joinToString(separator = "\n") { function ->
                "add($function.builder(construct, configuration))"
            }
        
        val functions = listOf(handlerClasses, handlerFunctions)
            .flatten()
            .map { handler -> handler.simpleName.asString() }
            .distinct()
            .joinToString(separator = "\n") { function ->
                "add($function.build(construct, onRebuildBehavior, configuration))"
            }
        
        val generatedClass = """
            package $generatedPackageName
            
            $imports
            
            object Functions {
                fun builders(construct: Construct, configuration: Function.Builder.() -> Unit = {}): List<Function.Builder> {
                    return buildList {
                        $builders
                    }
                }
            
                fun build(construct: Construct, onRebuildBehavior: (Function) -> Unit = {}, configuration: Function.Builder.() -> Unit = {}): List<Function> {
                    return buildList {
                        $functions
                    }
                }
            }
        """.trimIndent()
        
        val files = listOf(handlerClasses, handlerFunctions)
            .flatten()
            .mapNotNull { resource -> resource.containingFile }
        val dependencies = Dependencies(true, *files.toTypedArray())
        val generatedResourceBuilderFile = codeGenerator.createNewFile(
            dependencies,
            generatedPackageName,
            generatedClassName,
            "kt"
        )
        
        generatedResourceBuilderFile.bufferedWriter().use { writer -> writer.write(generatedClass) }
    }
}
