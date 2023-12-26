package com.johnturkson.aws.runtime.bootstrap

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.ClassKind.CLASS
import com.google.devtools.ksp.symbol.ClassKind.OBJECT
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.johnturkson.aws.runtime.annotations.Function

class BootstrapProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger,
    private val options: Map<String, String>,
) : SymbolProcessor {
    override fun process(resolver: Resolver): List<KSAnnotated> {
        val handlers = resolver.getSymbolsWithAnnotation(Function::class.qualifiedName!!)
        val handlerClasses = handlers.filterIsInstance<KSClassDeclaration>().toList()
        val handlerFunctions = handlers.filterIsInstance<KSFunctionDeclaration>().toList()
        val generatedPackageName = "com.johnturkson.aws.lambda.runtime.generated.bootstrap"
        
        val imports = mutableSetOf(
            "import com.johnturkson.aws.runtime.client.Handler",
            "import com.johnturkson.aws.runtime.client.Runtime",
        )
        
        handlerClasses.forEach { function -> imports += "import ${function.qualifiedName!!.asString()}" }
        handlerFunctions.forEach { function -> imports += "import ${function.qualifiedName!!.asString()}" }
        imports += "import javax.annotation.processing.Generated"
        
        val classReferences = handlerClasses.joinToString(separator = "\n") { function ->
            when (function.classKind) {
                OBJECT -> "\"${function.qualifiedName!!.asString()}\" -> ${function.simpleName.asString()}"
                CLASS -> "\"${function.qualifiedName!!.asString()}\" -> ${function.simpleName.asString()}()"
                else -> error("Unsupported handler declaration type: ${function.classKind}")
            }
        }
        
        val functionReferences = handlerFunctions.joinToString(separator = "\n") { function ->
            "\"${function.qualifiedName!!.asString()}\" -> Handler { request -> ${function.simpleName.asString()}(request) }"
        }
        
        val packageDeclarations = "package $generatedPackageName"
        val importDeclarations = imports.joinToString(separator = "\n")
        val functionDeclarations = """
            @Generated
            suspend fun main() {
                val runtime = Runtime()
                val handler = when (runtime.handlerName) {
                    $classReferences
                    $functionReferences
                    else -> error("Missing _HANDLER")
                }
                runtime.listen(handler)
            }
        """.trimIndent()
        
        val contents = listOf(
            packageDeclarations,
            importDeclarations,
            functionDeclarations
        ).joinToString(separator = "\n\n", postfix = "\n")
        
        if (handlerClasses.isNotEmpty() || handlerFunctions.isNotEmpty()) {
            val files = listOf(handlerClasses, handlerFunctions)
                .flatten()
                .mapNotNull { resource -> resource.containingFile }
            val dependencies = Dependencies(true, *files.toTypedArray())
            val bootstrap = codeGenerator.createNewFile(
                dependencies,
                generatedPackageName,
                "Bootstrap",
                "kt"
            )
            bootstrap.bufferedWriter().use { writer -> writer.write(contents) }
        }
        
        return emptyList()
    }
}
