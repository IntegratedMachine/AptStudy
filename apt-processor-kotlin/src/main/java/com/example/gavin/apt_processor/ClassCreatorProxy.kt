package com.example.gavin.apt_processor

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import java.io.File
import javax.annotation.processing.Messager
import javax.lang.model.element.TypeElement
import javax.lang.model.element.VariableElement
import javax.lang.model.util.Elements
import javax.tools.Diagnostic

/**
 * Created by gavin
 * date 2018/4/22
 * 创建Java文件代理类
 */
class ClassCreatorProxy(elementUtils: Elements, val typeElement: TypeElement, val kaptKotlinGeneratedDir: String) {
    private val mBindingClassName: String
    val packageName: String
    private val mVariableElementMap: MutableMap<Int, VariableElement> = HashMap()

    init {
        val packageElement = elementUtils.getPackageOf(typeElement)
        val packageName = packageElement.qualifiedName.toString()
        val className = typeElement.simpleName.toString()
        this.packageName = packageName
        mBindingClassName = className + "ViewBind"
    }

    fun putElement(id: Int, element: VariableElement) {
        mVariableElementMap[id] = element
    }


    fun generateCode(messager: Messager) {
        messager.printMessage(Diagnostic.Kind.NOTE, "generateCode mVariableElementMap: $mVariableElementMap")
        val fileSpecBuilder = FileSpec.builder(
            packageName,
            mBindingClassName
        )

        val functionBuilder = FunSpec.builder("bindView")
            .receiver(ClassName(packageName, typeElement.simpleName.toString()))
        for (id in mVariableElementMap.keys) {
            val element = mVariableElementMap[id]
            functionBuilder.addStatement("${element?.simpleName} = findViewById($id)")
        }

        fileSpecBuilder.addFunction(functionBuilder.build())
            .build()
            .writeTo(File(kaptKotlinGeneratedDir))
    }
}
