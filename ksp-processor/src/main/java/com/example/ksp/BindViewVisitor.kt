package com.example.ksp

import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSVisitorVoid
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.ksp.KotlinPoetKspPreview
import com.squareup.kotlinpoet.ksp.toClassName

@KotlinPoetKspPreview
class BindViewVisitor(private val environment: SymbolProcessorEnvironment) : KSVisitorVoid() {

    override fun visitClassDeclaration(declaration: KSClassDeclaration, data: Unit) {
        val className = declaration.toClassName()

        val fileSpec: FileSpec = buildFileSpec(declaration)

        environment.codeGenerator.createNewFile(
            dependencies = Dependencies(
                aggregating = false,
                sources = arrayOf(declaration.containingFile!!)
            ),
            packageName = className.packageName,
            fileName = className.simpleName + "_ViewBinding"
        ).use { outputStream ->
            outputStream.writer()
                .use {
                    fileSpec.writeTo(it)
                }
        }
    }

    @KotlinPoetKspPreview
    fun buildFileSpec(declaration: KSClassDeclaration): FileSpec {
        val className = declaration.toClassName()
        val fileName = className.simpleName + "_ViewBinding"
        val typeSpec = TypeSpec.classBuilder(fileName)
            .addFunction(
                FunSpec.builder("bind")
                    .addParameter("host", ClassName(className.packageName, className.simpleName))
                    .build()
            )
            .build()
        return FileSpec.builder(className.packageName, fileName)
            .addType(typeSpec)
            .build()
    }

}
