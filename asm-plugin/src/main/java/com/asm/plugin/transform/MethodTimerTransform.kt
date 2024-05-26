package com.asm.plugin.transform

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import com.asm.plugin.visitor.BindClassVisitor
import jdk.internal.org.objectweb.asm.Opcodes.ACC_PUBLIC
import jdk.internal.org.objectweb.asm.Opcodes.ACC_STATIC
import jdk.internal.org.objectweb.asm.Opcodes.GETSTATIC
import jdk.internal.org.objectweb.asm.Opcodes.INVOKEVIRTUAL
import jdk.internal.org.objectweb.asm.Opcodes.RETURN
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.FileUtils
import org.apache.commons.io.IOUtils
import org.gradle.api.Project
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassReader.EXPAND_FRAMES
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.MethodVisitor
import java.io.File
import java.io.FileOutputStream
import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream
import java.util.zip.ZipEntry


class MethodTimerTransform(var project: Project) : Transform() {


    override fun getName(): String {
        return "MethodTimerTransform"
    }

    override fun getInputTypes(): MutableSet<QualifiedContent.ContentType> {
        return TransformManager.CONTENT_CLASS
    }

    override fun isIncremental(): Boolean {
        return true
    }

    override fun getScopes(): MutableSet<in QualifiedContent.Scope> {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    override fun transform(transformInvocation: TransformInvocation?) {
        super.transform(transformInvocation)
        var startTime = System.currentTimeMillis()
        println("----------transform start:${startTime}-----------")
        var inputs = transformInvocation?.inputs
        val outputProvider = transformInvocation?.outputProvider
        outputProvider?.deleteAll()

        inputs?.forEach {
            it.directoryInputs.forEach { dInput ->
                try {
                    handleDirectoryInput(dInput, outputProvider!!)
                }catch (e:Exception){
                    e.printStackTrace()
                }


            }
            it.jarInputs.forEach { jInpt ->
                try {
                    handleJarInputs(jInpt, outputProvider!!)
                }catch (e:Exception){
                    e.printStackTrace()
                }



            }
        }
        var endTime = System.currentTimeMillis()
        println("-----------transform end${endTime}-------------")
        println("-----------transform cost: ${endTime - startTime}-------------")
    }

    /**
     * 处理文件目录下的class文件
     */
    private fun handleDirectoryInput(
        directoryInput: DirectoryInput,
        outputProvider: TransformOutputProvider
    ) {
        //是否是目录
        if (directoryInput.file.isDirectory()) {
            //列出目录所有文件（包含子文件夹，子文件夹内文件）
            directoryInput.file.walk().maxDepth(Int.MAX_VALUE).filter { it.isFile }
                .forEach { file ->
                    var name = file.name
                    if (name.endsWith(".class")) {
                        println("-----------handleDirectoryInput ${name}-----true------path ${file.parentFile.absolutePath}")
                        val classReader = ClassReader(file.readBytes())
                        val classWriter = ClassWriter(classReader, ClassWriter.COMPUTE_MAXS)
                        val cv = BindClassVisitor(classWriter)
                        classReader.accept(cv, EXPAND_FRAMES)
                        //新增加一个方法
                        val mw: MethodVisitor = classWriter.visitMethod(
                            ACC_PUBLIC + ACC_STATIC,
                            "add",
                            "([Ljava/lang/String;)V",
                            null,
                            null
                        )
                        // pushes the 'out' field (of type PrintStream) of the System class
                        mw.visitFieldInsn(
                            GETSTATIC,
                            "java/lang/System",
                            "out",
                            "Ljava/io/PrintStream;"
                        )
                        // pushes the "Hello World!" String constant
                        mw.visitLdcInsn("this is add method print!")
                        // invokes the 'println' method (defined in the PrintStream class)
                        mw.visitMethodInsn(
                            INVOKEVIRTUAL,
                            "java/io/PrintStream",
                            "println",
                            "(Ljava/lang/String;)V"
                        )
                        mw.visitInsn(RETURN)
                        // this code uses a maximum of two stack elements and two local
                        // variables
                        mw.visitMaxs(0, 0)
                        mw.visitEnd()

                        val code = classWriter.toByteArray()
                        val fos = FileOutputStream(
                            file.parentFile.absolutePath + File.separator + name
                        )
                        println("handleDirectoryInput fos: ${file.parentFile.absolutePath}")
                        fos.write(code)
                        fos.close()

                        val file1 = File("build/temp")
                        if (!file1.exists()) {
                            file1.mkdirs()
                        }
                        val fos1 = FileOutputStream(
                            "build/temp/$name"
                        )

                        fos1.write(code)
                        fos1.close()
                    }
                }

        }
        //处理完输入文件之后，要把输出给下一个任务
        val dest = outputProvider.getContentLocation(
            directoryInput.name,
            directoryInput.contentTypes,
            directoryInput.scopes,
            Format.DIRECTORY
        )
        FileUtils.copyDirectory(directoryInput.file, dest)
    }

    private fun handleJarInputs(jarInput: JarInput, outputProvider: TransformOutputProvider) {
        if (jarInput.file.absolutePath.endsWith(".jar")) {
            //重名名输出文件,因为可能同名,会覆盖
            var jarName = jarInput.name
            val md5Name = DigestUtils.md5Hex(jarInput.file.absolutePath)
            if (jarName.endsWith(".jar")) {
                jarName = jarName.substring(0, jarName.length - 4)
            }
            val jarFile = JarFile(jarInput.file)
            val enumeration = jarFile.entries()
            val tmpFile = File(jarInput.file.getParent() + File.separator + "classes_temp.jar")
            //避免上次的缓存被重复插入
            if (tmpFile.exists()) {
                tmpFile.delete()
            }
            val jarOutputStream = JarOutputStream(FileOutputStream(tmpFile))
            //用于保存
            while (enumeration.hasMoreElements()) {
                val jarEntry = (enumeration.nextElement()) as JarEntry
                val inputStream = jarFile.getInputStream(jarEntry)
                var entryName = jarEntry.name
                val zipEntry = ZipEntry(entryName)
                jarOutputStream.putNextEntry(zipEntry)
                //插桩class
                //class文件处理
                if (entryName.endsWith("Activity.class")) {
                    println("----------- handleJarInputs $entryName -------true----")
                    val classReader = ClassReader(IOUtils.toByteArray(inputStream))
                    val classWriter = ClassWriter(classReader, ClassWriter.COMPUTE_MAXS)
                    val cv = BindClassVisitor(classWriter)
                    classReader.accept(cv, EXPAND_FRAMES)
                    val code = classWriter.toByteArray()
                    jarOutputStream.write(code)
                } else {
                    jarOutputStream.write(IOUtils.toByteArray(inputStream))
                }
                jarOutputStream.closeEntry()
            }
            //结束
            jarOutputStream.close()
            jarFile.close()
            val dest = outputProvider.getContentLocation(
                jarName + md5Name,
                jarInput.contentTypes, jarInput.scopes, Format.JAR
            )
            FileUtils.copyFile(tmpFile, dest)
            tmpFile.delete()
        }
    }
}

/**
 * 处理Jar中的class文件
 */
