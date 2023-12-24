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
        
        return emptyList()
    }
    
    @OptIn(KspExperimental::class)
    private fun generateFunctionClass(handlerClass: KSDeclaration) {
        val resourceClassName = handlerClass.simpleName.asString()
        val generatedPackageName = requireNotNull(options["OUTPUT_PACKAGE"]) + ".functions"
        val handlerName = handlerClass.qualifiedName!!.asString()
        val handlerPath = requireNotNull(options["HANDLER_PATH"])
        
        val architecture = handlerClass.getAnnotationsByType(Architecture::class).firstOrNull()
        val timeout = handlerClass.getAnnotationsByType(Timeout::class).firstOrNull()
        val memory = handlerClass.getAnnotationsByType(Memory::class).firstOrNull()
        
        val imports = """
            import software.amazon.awscdk.Duration
            import software.amazon.awscdk.services.lambda.Architecture
            import software.amazon.awscdk.services.lambda.Code
            import software.amazon.awscdk.services.lambda.Function
            import software.amazon.awscdk.services.lambda.Runtime
            import software.constructs.Construct
            import javax.annotation.processing.Generated
        """.trimIndent()
        
        val generatedClass = """
            package $generatedPackageName
            
            $imports
            
            /**
            * @see $handlerName
            */
            @Generated
            class $resourceClassName {
                companion object Builder {
                    fun create(scope: Construct, id: String = "$resourceClassName"): Function.Builder {
                        return Function.Builder.create(scope, id)
                            .handler("$handlerName")
                            .code(Code.fromAsset("$handlerPath"))
                            .runtime(Runtime.PROVIDED_AL2)
                            ${architecture?.let { ".architecture(Architecture.${architecture.value})" } ?: ""}
                            ${timeout?.let { ".timeout(Duration.seconds(${timeout.value}))" } ?: ""}
                            ${memory?.let { ".memorySize(${memory.value})" } ?: ""}
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
}
