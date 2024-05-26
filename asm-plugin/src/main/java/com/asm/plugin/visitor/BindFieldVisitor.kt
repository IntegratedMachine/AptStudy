package com.asm.plugin.visitor

import com.asm.plugin.bean.FieldData
import org.objectweb.asm.AnnotationVisitor
import org.objectweb.asm.FieldVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.TypePath

class BindFieldVisitor(
    fv: FieldVisitor,
    val fieldAnnoMap: ArrayList<FieldData>,
    val fieldName: String?
) : FieldVisitor(Opcodes.ASM7, fv) {
    private val TAG = "BindFieldVisitor"

    override fun visitAnnotation(descriptor: String?, visible: Boolean): AnnotationVisitor {
        val containsTag = descriptor?.contains("BindView")
        println("$TAG : visitAnnotation descriptor: $descriptor, containsTag: $containsTag")
        //Lcom/example/gavin/apt_annotation/BindView;
        if (containsTag == true) {
            println("$TAG : visitAnnotation new AnnotationVisitor")
            val av = fv.visitAnnotation(descriptor, visible)
            return BindAnnotationVisitor(av, fieldAnnoMap, fieldName, descriptor)
        }
        return super.visitAnnotation(descriptor, visible)
    }

    override fun visitTypeAnnotation(
        typeRef: Int,
        typePath: TypePath?,
        descriptor: String?,
        visible: Boolean
    ): AnnotationVisitor {
        println("$TAG : visitTypeAnnotation descriptor: $descriptor")
        return super.visitTypeAnnotation(typeRef, typePath, descriptor, visible)
    }
}