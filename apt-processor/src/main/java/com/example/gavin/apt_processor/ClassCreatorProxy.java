package com.example.gavin.apt_processor;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;

/**
 * Created by gavin
 * date 2018/4/22
 * 创建Java文件代理类
 */
public class ClassCreatorProxy {
    private String mBindingClassName;
    private String mPackageName;
    private TypeElement mTypeElement;
    private Map<Integer, VariableElement> mVariableElementMap = new HashMap<>();

    public ClassCreatorProxy(Elements elementUtils, TypeElement classElement) {
        this.mTypeElement = classElement;
        PackageElement packageElement = elementUtils.getPackageOf(mTypeElement);
        String packageName = packageElement.getQualifiedName().toString();
        String className = mTypeElement.getSimpleName().toString();
        this.mPackageName = packageName;
        this.mBindingClassName = className + "_ViewBinding";
    }

    public void putElement(int id, VariableElement element) {
        mVariableElementMap.put(id, element);
    }

    /**
     * 创建Java代码
     *
     * @return
     */
    public String generateJavaCode() {
        StringBuilder builder = new StringBuilder();
        builder.append("package ").append(mPackageName).append(";\n\n");
        builder.append("import com.example.gavin.apt_library.*;\n");
        builder.append('\n');
        builder.append("public class ").append(mBindingClassName);
        builder.append(" {\n");

        generateMethods(builder);
        builder.append('\n');
        builder.append("}\n");
        return builder.toString();
    }

    /**
     * 加入Method
     *
     * @param builder
     */
    private void generateMethods(StringBuilder builder) {
        builder.append("public void bind(" + mTypeElement.getQualifiedName() + " host ) {\n");
        for (int id : mVariableElementMap.keySet()) {
            VariableElement element = mVariableElementMap.get(id);
            String name = element.getSimpleName().toString();
            String type = element.asType().toString();
            builder.append("host." + name).append(" = ");
            builder.append("(" + type + ")(((android.app.Activity)host).findViewById( " + id + "));\n");
        }
        builder.append("  }\n");
    }

    public String getProxyClassFullName() {
        return mPackageName + "." + mBindingClassName;
    }

    public TypeElement getTypeElement() {
        return mTypeElement;
    }

    //======================

    /**
     * 创建Java代码
     * javapoet
     *
     * @return
     */
    public TypeSpec generateJavaCode2(Messager mMessager) {
        TypeSpec bindingClass = TypeSpec.classBuilder(mBindingClassName)
                .addModifiers(Modifier.PUBLIC)
                .addMethod(generateMethods2(mMessager))
                .build();
        return bindingClass;

    }

    /**
     * 加入Method
     * javapoet
     */
    private MethodSpec generateMethods2(Messager mMessager) {
        ClassName host = ClassName.bestGuess(mTypeElement.getQualifiedName().toString());
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("bind")
                .addModifiers(Modifier.PUBLIC)
                .returns(void.class)
                .addParameter(host, "host");

        boolean isKtClass = false;
        List<? extends TypeParameterElement> typeParameters = mTypeElement.getTypeParameters();
        for (TypeParameterElement typeParameter : typeParameters) {
            mMessager.printMessage(Diagnostic.Kind.NOTE, "typeParameter: " + typeParameter);
        }
        List<? extends Element> enclosedElements = mTypeElement.getEnclosedElements();
        for (Element enclosedElement : enclosedElements) {
            mMessager.printMessage(Diagnostic.Kind.NOTE, "enclosedElement: " + enclosedElement);
            String enclosedElementString = enclosedElement.toString();
            if (enclosedElementString.startsWith("get")) {
                for (Element enclosedElementIn : enclosedElements) {
                    if (enclosedElementString.substring(3).replace("()", "").equals(capitalize(enclosedElementIn.toString()))) {
                        isKtClass = true;
                        break;
                    }
                }
                if (isKtClass) {
                    break;
                }
            }
        }
        mMessager.printMessage(Diagnostic.Kind.NOTE, "mTypeElement: " + mTypeElement
                + ", host: " + host.packageName()
                + ", isKtClass: " + isKtClass
        );
        for (int id : mVariableElementMap.keySet()) {
            VariableElement element = mVariableElementMap.get(id);
            String name = element.getSimpleName().toString();
            String type = element.asType().toString();
//            methodBuilder.addCode("host." + name + " = " + "(" + type + ")(((android.app.Activity)host).findViewById( " + id + "));");
            if (isKtClass) {
                methodBuilder.addStatement(
                        "host.set$N(host.findViewById($L))",
                        capitalize(element.getSimpleName().toString()), id
                );
            } else {
                methodBuilder.addStatement(
                        "host.$N = host.findViewById($L)",
                        element.getSimpleName().toString(), id
                );
            }
        }
        return methodBuilder.build();
    }

    public String capitalize(String inputString) {

        // get the first character of the inputString
        char firstLetter = inputString.charAt(0);

        // convert it to an UpperCase letter
        char capitalFirstLetter = Character.toUpperCase(firstLetter);

        // return the output string by updating
        //the first char of the input string
        return capitalFirstLetter + inputString.substring(1);
    }

    public boolean isKtClass(Class clazz) {
        Annotation[] annotations = clazz.getAnnotations();
        for (Annotation annotation : annotations) {
            if (annotation.toString().contains("kotlin")) {
                return true;
            }
        }
        return false;
    }


    public String getPackageName() {
        return mPackageName;
    }
}
