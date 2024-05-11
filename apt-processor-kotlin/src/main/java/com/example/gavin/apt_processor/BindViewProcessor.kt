package com.example.gavin.apt_processor

import com.example.gavin.apt_annotation.BindView
import com.google.auto.service.AutoService
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.Messager
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement
import javax.lang.model.element.VariableElement
import javax.lang.model.util.Elements
import javax.tools.Diagnostic

@AutoService(Processor::class)
class BindViewProcessor : AbstractProcessor() {
    private var mMessager: Messager? = null
    private var mElementUtils: Elements? = null
    private val mProxyMap: MutableMap<String, ClassCreatorProxy> = HashMap()
    @Synchronized
    override fun init(processingEnv: ProcessingEnvironment) {
        super.init(processingEnv)
        mMessager = processingEnv.messager
        mElementUtils = processingEnv.elementUtils
    }

    override fun getSupportedAnnotationTypes(): Set<String> {
        val supportTypes: HashSet<String> = LinkedHashSet()
        supportTypes.add(BindView::class.java.getCanonicalName())
        return supportTypes
    }

    override fun getSupportedSourceVersion(): SourceVersion {
        return SourceVersion.latestSupported()
    }

    override fun process(set: Set<TypeElement>, roundEnvironment: RoundEnvironment): Boolean {
        val startTimeMs = System.currentTimeMillis()
        val kaptKotlinGeneratedDir = processingEnv.options["kapt.kotlin.generated"] ?: return false
        mMessager!!.printMessage(Diagnostic.Kind.NOTE, "processing...")
        mProxyMap.clear()
        //得到所有的注解
        val elements = roundEnvironment.getElementsAnnotatedWith(
            BindView::class.java
        )
        for (element in elements) {
            val variableElement = element as VariableElement
            val classElement = variableElement.enclosingElement as TypeElement
            val fullClassName = classElement.qualifiedName.toString()
            //elements的信息保存到mProxyMap中
            var proxy = mProxyMap[fullClassName]
            if (proxy == null) {
                proxy = ClassCreatorProxy(mElementUtils!!, classElement, kaptKotlinGeneratedDir)
                mProxyMap[fullClassName] = proxy
            }
            val bindAnnotation = variableElement.getAnnotation(BindView::class.java)
            val id = bindAnnotation.value
            proxy.putElement(id, variableElement)
        }
        //通过javapoet生成
        for (key in mProxyMap.keys) {
            val proxyInfo = mProxyMap[key]
            proxyInfo!!.generateCode(mMessager!!)
        }
        val totalTime = System.currentTimeMillis() - startTimeMs
        mMessager!!.printMessage(Diagnostic.Kind.NOTE, "process finish totalTime: $totalTime")
        return true
    }
}
