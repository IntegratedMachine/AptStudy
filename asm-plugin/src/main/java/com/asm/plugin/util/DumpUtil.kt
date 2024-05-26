package com.asm.plugin.util

import com.asm.plugin.bean.FieldData
import jdk.internal.org.objectweb.asm.Opcodes.ACC_FINAL
import jdk.internal.org.objectweb.asm.Opcodes.ACC_PUBLIC
import jdk.internal.org.objectweb.asm.Opcodes.ACC_SUPER
import jdk.internal.org.objectweb.asm.Opcodes.ALOAD
import jdk.internal.org.objectweb.asm.Opcodes.CHECKCAST
import jdk.internal.org.objectweb.asm.Opcodes.INVOKESPECIAL
import jdk.internal.org.objectweb.asm.Opcodes.INVOKEVIRTUAL
import jdk.internal.org.objectweb.asm.Opcodes.PUTFIELD
import jdk.internal.org.objectweb.asm.Opcodes.RETURN
import jdk.internal.org.objectweb.asm.Opcodes.V1_8
import org.objectweb.asm.AnnotationVisitor
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.FieldVisitor
import org.objectweb.asm.MethodVisitor
import java.io.FileOutputStream


object DumpUtil {
    @Throws(Exception::class)
    fun dump(): ByteArray {
        val classWriter = ClassWriter(0)
        var fieldVisitor: FieldVisitor
        var methodVisitor: MethodVisitor
        var annotationVisitor0: AnnotationVisitor
        classWriter.visit(
            V1_8,
            ACC_PUBLIC or ACC_FINAL or ACC_SUPER,
            "com/example/gavin/apttest/HelloASM",
            null,
            "java/lang/Object",
            null
        )
        run {
            annotationVisitor0 = classWriter.visitAnnotation("Lkotlin/Metadata;", true)
            annotationVisitor0.visit("mv", intArrayOf(1, 9, 0))
            annotationVisitor0.visit("k", 1)
            annotationVisitor0.visit("xi", 48)
            run {
                val annotationVisitor1: AnnotationVisitor = annotationVisitor0.visitArray("d1")
                annotationVisitor1.visit(
                    null,
                    "\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\u0008\u0002\n\u0002\u0010\u0002\n\u0000\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\u0006\u0010\u0003\u001a\u00020\u0004\u00a8\u0006\u0005"
                )
                annotationVisitor1.visitEnd()
            }
            run {
                val annotationVisitor1: AnnotationVisitor = annotationVisitor0.visitArray("d2")
                annotationVisitor1.visit(null, "Lcom/example/gavin/apttest/HelloASM;")
                annotationVisitor1.visit(null, "")
                annotationVisitor1.visit(null, "()V")
                annotationVisitor1.visit(null, "testAsm")
                annotationVisitor1.visit(null, "")
                annotationVisitor1.visit(null, "app_debug")
                annotationVisitor1.visitEnd()
            }
            annotationVisitor0.visitEnd()
        }
        run {
            methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null)
            methodVisitor.visitCode()
            methodVisitor.visitVarInsn(ALOAD, 0)
            methodVisitor.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false)
            methodVisitor.visitInsn(RETURN)
            methodVisitor.visitMaxs(1, 1)
            methodVisitor.visitEnd()
        }
        run {
            methodVisitor =
                classWriter.visitMethod(ACC_PUBLIC or ACC_FINAL, "testAsm", "()V", null, null)
            methodVisitor.visitCode()
            methodVisitor.visitInsn(RETURN)
            methodVisitor.visitMaxs(0, 1)
            methodVisitor.visitEnd()
        }
        classWriter.visitEnd()

        val fos = FileOutputStream("HelloASM.class")
        fos.write(classWriter.toByteArray())
        fos.close()
        return classWriter.toByteArray()
    }

    fun dumpBindViewMethod(fieldDataList: ArrayList<FieldData>, cv: ClassVisitor) {
        val classWriter = ClassWriter(0)
        var fieldVisitor: FieldVisitor
        val methodVisitor: MethodVisitor
        var annotationVisitor0: AnnotationVisitor

        methodVisitor =
            cv.visitMethod(ACC_PUBLIC or ACC_FINAL, "bindView", "()V", null, null)

        methodVisitor.visitCode()
        methodVisitor.visitVarInsn(ALOAD, 0)
        methodVisitor.visitVarInsn(ALOAD, 0)
        methodVisitor.visitLdcInsn(2131231016)
        methodVisitor.visitMethodInsn(
            INVOKEVIRTUAL,
            "com/example/gavin/apttest/CustomView",
            "findViewById",
            "(I)Landroid/view/View;",
            false
        )
        methodVisitor.visitTypeInsn(CHECKCAST, "android/widget/TextView")
        methodVisitor.visitFieldInsn(
            PUTFIELD,
            "com/example/gavin/apttest/CustomView",
            "textView",
            "Landroid/widget/TextView;"
        )
        methodVisitor.visitVarInsn(ALOAD, 0)
        methodVisitor.visitVarInsn(ALOAD, 0)
        methodVisitor.visitLdcInsn(2131230812)
        methodVisitor.visitMethodInsn(
            INVOKEVIRTUAL,
            "com/example/gavin/apttest/CustomView",
            "findViewById",
            "(I)Landroid/view/View;",
            false
        )
        methodVisitor.visitTypeInsn(CHECKCAST, "android/widget/Button")
        methodVisitor.visitFieldInsn(
            PUTFIELD,
            "com/example/gavin/apttest/CustomView",
            "mButton",
            "Landroid/widget/Button;"
        )
        methodVisitor.visitInsn(RETURN)
        methodVisitor.visitMaxs(3, 1)
        methodVisitor.visitEnd()
    }
}