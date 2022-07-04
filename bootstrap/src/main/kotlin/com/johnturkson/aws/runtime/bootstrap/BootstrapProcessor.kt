package com.johnturkson.aws.runtime.bootstrap

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration

class BootstrapProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger,
    private val options: Map<String, String>,
) : SymbolProcessor {
    override fun process(resolver: Resolver): List<KSAnnotated> {
        val functions = resolver.getSymbolsWithAnnotation(Function::class.qualifiedName!!)
        val functionClasses = functions.filterIsInstance<KSClassDeclaration>().toList()
        
        val imports = mutableSetOf(
            "import com.johnturkson.aws.runtime.client.env",
            "import com.johnturkson.aws.runtime.client.listen",
        )
        
        functionClasses.forEach { function -> imports += "import ${function.qualifiedName?.asString()}" }
        
        val handlers = functionClasses.joinToString(separator = "\n") { function ->
            "\"${function.qualifiedName?.asString()}\" -> ${function.simpleName.asString()}"
        }
        
        val contents = """
            package com.johnturkson.aws.runtime.bootstrap.generated
            
            ${imports.joinToString(separator = "\n")}
            
            suspend fun main() {
                val handler = when (env("_HANDLER")) {
                    $handlers
                    else -> error("Missing _HANDLER")
                }
                listen(handler)
            }
        """.trimIndent()
        
        if (functionClasses.isNotEmpty()) {
            val bootstrap = codeGenerator.createNewFile(
                Dependencies.ALL_FILES,
                "com.johnturkson.aws.runtime.bootstrap.generated",
                "Bootstrap"
            )
            bootstrap.bufferedWriter().use { writer -> writer.write(contents) }
        }
        
        return emptyList()
    }
}
