package com.asm.plugin.visitor

import org.objectweb.asm.AnnotationVisitor
import org.objectweb.asm.Opcodes

class BindAnnotationVisitor(av: AnnotationVisitor) : AnnotationVisitor(Opcodes.ASM7, av) {
    private val TAG = "BindAnnotationVisitor"
    override fun visit(name: String?, value: Any?) {
        println("$TAG : visitAnnotation name: $name, value: $value")
        super.visit(name, value)
    }
}