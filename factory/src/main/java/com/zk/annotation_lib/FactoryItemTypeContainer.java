package com.zk.annotation_lib;

import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

/**
 * author: ZK.
 * date:   On 2018-10-24.
 * description: 被注解包含相同type()的类的FactoryAnnotationClass容器。如 Audi，Benz，BMW的FactoryAnnotationClass容器
 */
public class FactoryItemTypeContainer {


    private String itemTypeName;

    private final String FACTORY = "Factory";

    private Map<String, FactoryAnnotationClass> mAnnotationClassMap = new HashMap<>();


    public FactoryItemTypeContainer(String itemTypeName) {
        this.itemTypeName = itemTypeName;
    }


    //添加
    public void add(FactoryAnnotationClass toInsert) throws ProcessingException {
        FactoryAnnotationClass exist = mAnnotationClassMap.get(toInsert.getId());
        if (exist != null) {
            throw new ProcessingException(toInsert.getAnnotatedClassElement(), "Conflit : The class %1$s is annotated " +
                    "with  %2$s with id = %3$s but %4$s already use the same id", toInsert.getAnnotatedClassElement()
                    .getQualifiedName().toString(), Factory.class.getName(), toInsert.getId(), exist
                    .getAnnotatedClassElement().getQualifiedName().toString());
        }
        mAnnotationClassMap.put(toInsert.getId(), toInsert);
    }


    //拼接字符串编写Java代码 ，使用Square的开源框架 javapoet ： https://github.com/square/javapoet
    /**
     * public class CarFactory {
     * <p>
     * public Car create(String id) {
     * if (id == null) {
     * throw new IllegalArgumentException("id is Null");
     * }
     * if (id.equals("Audi")) {
     * return new Audi();
     * }
     * <p>
     * if (id.equals("BMW")) {
     * return new BMW();
     * }
     * <p>
     * if (id.equals("Benz")) {
     * return new Benz();
     * }
     * throw new IllegalArgumentException("unKnow id = " + id);
     * }
     * <p>
     * }
     */
    public void generateCode(Elements elementUtils, Filer filer) throws IOException {

        TypeElement superTypeElement = elementUtils.getTypeElement(itemTypeName);
        String factoryClassName = superTypeElement.getSimpleName() + FACTORY; // 将要生成的工厂类名

        MethodSpec.Builder methodBuilder = MethodSpec
                .methodBuilder("create")
                .addModifiers(Modifier.PUBLIC)
                .returns(TypeName.get(superTypeElement.asType()))
                .addParameter(String.class, "id")
                .beginControlFlow("if (id == null ||\"\".equals(id))")
                .addStatement(" throw  new IllegalArgumentException(\"id is Null\")")
                .endControlFlow();
        for (FactoryAnnotationClass factoryAnnotationClass : mAnnotationClassMap.values()) {
            methodBuilder.beginControlFlow("if ($S.equals(id))", factoryAnnotationClass.getId())
                    .addStatement("return new $L()", factoryAnnotationClass.getAnnotatedClassElement().getQualifiedName().toString())
                    .endControlFlow();
        }
        methodBuilder.addStatement("throw new IllegalArgumentException($S + id)", "Unknown id = ");

        TypeSpec typeSpec = TypeSpec.classBuilder(factoryClassName)
                .addModifiers(Modifier.PUBLIC)
                .addMethod(methodBuilder.build())
                .build();
        JavaFile.builder(elementUtils.getPackageOf(superTypeElement)
                .getQualifiedName().toString(), typeSpec)
                .build()
                .writeTo(filer);


    }


}
