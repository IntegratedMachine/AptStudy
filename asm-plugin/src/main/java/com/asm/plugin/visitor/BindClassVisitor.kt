package com.asm.plugin.visitor

import org.objectweb.asm.AnnotationVisitor
import org.objectweb.asm.Attribute
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.FieldVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.TypePath

class BindClassVisitor(cv: ClassVisitor) : ClassVisitor(Opcodes.ASM7, cv) {
    private val TAG = "BindClassVisitor"
    private var mClassName: String? = null
    private var fieldVisitor: BindFieldVisitor? = null
    override fun visit(
        version: Int,
        access: Int,
        name: String?,
        signature: String?,
        superName: String?,
        interfaces: Array<out String>?
    ) {
        println("$TAG : visit -----> started:$name")
        this.mClassName = name
        super.visit(version, access, name, signature, superName, interfaces)
    }

    override fun visitField(
        access: Int,
        name: String?,
        descriptor: String?,
        signature: String?,
        value: Any?
    ): FieldVisitor {
        val fv = cv.visitField(access, name, descriptor, signature, value)
//        if (fieldVisitor == null) {
            fieldVisitor = BindFieldVisitor(fv)
//        }
        println("$TAG : visitField name: $name, $descriptor, $signature, $value")

        return fieldVisitor!!
    }

    override fun visitAttribute(attribute: Attribute?) {
        println("$TAG : visitAttribute attribute: $attribute")
        super.visitAttribute(attribute)
    }

    override fun visitAnnotation(descriptor: String?, visible: Boolean): AnnotationVisitor {
        println("$TAG : visitAnnotation descriptor: $descriptor")
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

    override fun visitEnd() {
        println("$TAG : visit -----> end")
        super.visitEnd()
    }
}