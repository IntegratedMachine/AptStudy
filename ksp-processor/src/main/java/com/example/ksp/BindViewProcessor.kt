package com.example.ksp

import com.example.gavin.apt_annotation.BindView
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.google.devtools.ksp.validate
import com.squareup.kotlinpoet.ksp.KotlinPoetKspPreview

@KotlinPoetKspPreview
class BindViewProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger
) : SymbolProcessor {

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val startTimeMs = System.currentTimeMillis()
        val symbols = resolver.getSymbolsWithAnnotation(BindView::class.qualifiedName!!)
        val ret = symbols.filter {
            logger.warn("process symbol: $it, validate: ${it.validate()}")
            !it.validate()
        }.toList()
        val butterKnifeList = symbols
            .filter { it is KSPropertyDeclaration && it.validate() }
            .map {
                it as KSPropertyDeclaration
            }.toList()
        ButterKnifeGenerator().generate(codeGenerator, logger, butterKnifeList)
        val totalTime = System.currentTimeMillis() - startTimeMs

        logger.warn("process finish totalTime: $totalTime")
        return ret
    }
}

@KotlinPoetKspPreview
class BindViewProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment) =
        BindViewProcessor(environment.codeGenerator, environment.logger)
}