package com.asm.plugin.visitor

import com.asm.plugin.bean.FieldData
import org.objectweb.asm.AnnotationVisitor
import org.objectweb.asm.Opcodes

class BindAnnotationVisitor(
    av: AnnotationVisitor,
    val fieldAnnoMap: ArrayList<FieldData>,
    val fieldName: String?,
    val descriptor: String?
) : AnnotationVisitor(Opcodes.ASM7, av) {
    private val TAG = "BindAnnotationVisitor"
    override fun visit(name: String?, value: Any?) {
        println("$TAG : visitAnnotation name: $name, value: $value")
        fieldName?.let {
            fieldAnnoMap.add(FieldData(it, descriptor ?: "", value as? Int ?: 0))
        }
        super.visit(name, value)
    }
}