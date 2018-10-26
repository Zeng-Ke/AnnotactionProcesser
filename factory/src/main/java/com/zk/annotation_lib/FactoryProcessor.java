package com.zk.annotation_lib;

import com.google.auto.service.AutoService;
import com.zk.annotation_lib.Factory;
import com.zk.annotation_lib.FactoryAnnotationClass;
import com.zk.annotation_lib.FactoryItemTypeContainer;
import com.zk.annotation_lib.ProcessingException;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

/**
 * author: ZK.
 * date:   On 2018-10-23.
 */
@AutoService(Processor.class)
public class FactoryProcessor extends AbstractProcessor {

    private Elements mElementUtils;
    private Types mTypeUtils;
    private Messager mMessager;
    private Filer mFiler;

    private Map<String, FactoryItemTypeContainer> mFactoryItemTypeContainers = new HashMap<>();

    //初始化方法，会被注解处理工具调用，并提供ProcessingEnvironment，可通过其获取各种需要的工具类
    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        mElementUtils = processingEnv.getElementUtils();
        mTypeUtils = processingEnv.getTypeUtils();
        mMessager = processingEnv.getMessager();
        mFiler = processingEnv.getFiler();

    }

    //指定使用的Java版本。一般使用SourceVersion.latestSupported()
    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    //指定这个注解处理器对哪个注解进行处理(此处对@Factory进行处理)
    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> annotations = new LinkedHashSet<>();
        annotations.add(Factory.class.getCanonicalName()); // Factory.class.getCanonicalName() = "com.zk.annotation_lib.Factory"
        return annotations;
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        try {
            for (Element element : roundEnv.getElementsAnnotatedWith(Factory.class)) {
                TypeElement typeElement = (TypeElement) element;
          /*  mMessager.printMessage(Diagnostic.Kind.NOTE, "=============NOTE======", element);
            mMessager.printMessage(Diagnostic.Kind.WARNING, "==========WARNING=========", element);
            mMessager.printMessage(Diagnostic.Kind.ERROR, "==========ERROR=========", element);*/

                FactoryAnnotationClass factoryAnnotationClass = new FactoryAnnotationClass(typeElement);
                if (!idValidClass(factoryAnnotationClass)) {
                    return true; //已经打印了错误信息，退出处理
                }
                FactoryItemTypeContainer factoryItemTypeContainer = mFactoryItemTypeContainers.get(factoryAnnotationClass
                        .getAnnotateTypeName());
                if (factoryItemTypeContainer == null) {
                    factoryItemTypeContainer = new FactoryItemTypeContainer(factoryAnnotationClass.getAnnotateTypeName());
                    mFactoryItemTypeContainers.put(factoryAnnotationClass.getAnnotateTypeName(), factoryItemTypeContainer);
                }
                factoryItemTypeContainer.add(factoryAnnotationClass);

            }
            for (FactoryItemTypeContainer itemTypeContainer : mFactoryItemTypeContainers.values()) {
                itemTypeContainer.generateCode(mElementUtils, mFiler);
            }
            mFactoryItemTypeContainers.clear();
        } catch (IllegalArgumentException exception) {
            mMessager.printMessage(Diagnostic.Kind.ERROR, exception.getMessage(), null);
            error(null, exception.getMessage());

        } catch (ProcessingException e) {
            error(e.getElement(), e.getMessage());

        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }


    private boolean idValidClass(FactoryAnnotationClass factoryAnnotationClass) throws ProcessingException {

        TypeElement currentClassElement = factoryAnnotationClass.getAnnotatedClassElement();
        //判断当前类是否为public
        if (!currentClassElement.getModifiers().contains(Modifier.PUBLIC)) {
            error(currentClassElement, "the class %$s is not public", currentClassElement.getQualifiedName().toString());
            return false;
        }

        //获取@Factory的type()的TypeElement。也就是被注解元素的父类。如类Car
        TypeElement superClassElement = mElementUtils.getTypeElement(factoryAnnotationClass.getAnnotateTypeName());
        if (superClassElement.getKind() == ElementKind.INTERFACE) {
            if (!currentClassElement.getInterfaces().contains(superClassElement.asType())) {
                throw new ProcessingException(currentClassElement, "The class %1$s annoated with %2$s must implement the interface %3$s",
                        currentClassElement.getQualifiedName().toString(), Factory.class.toString(), factoryAnnotationClass
                        .getAnnotateTypeName());
            }

        } else {
            TypeMirror superclass = superClassElement.getSuperclass();
            while (true) {
                TypeKind superclassKind = superclass.getKind();
                //到达了基本类型（java.lang.Object）,所以退出
                if (superclassKind == TypeKind.NONE) {
                    throw new ProcessingException(currentClassElement, "The class %1$s annotated with %2$s must interit from %3$s",
                            currentClassElement.getQualifiedName(), Factory.class.toString(), factoryAnnotationClass.getAnnotateTypeName());
                }
                if (superclassKind.toString().equals(factoryAnnotationClass.getAnnotateTypeName())) {
                    //找到了要的父类为注解的type()类型
                    break;
                }
                //在继承树上继续查找
                currentClassElement = (TypeElement) mTypeUtils.asElement(superclass);
            }
        }
        //是否提供空参数构造函数
        for (Element enClosed : currentClassElement.getEnclosedElements()) {
            if (enClosed.getKind() == ElementKind.CONSTRUCTOR) {
                ExecutableElement constructorElement = (ExecutableElement) enClosed;
                if (constructorElement.getParameters().size() == 0 && constructorElement.getModifiers().contains(Modifier.PUBLIC)) {
                    //找到了默认函数
                    return true;
                }
            }

        }
        //没有找到默认空参数构造函数
        throw new ProcessingException(currentClassElement, "The class of %1$s must provide a public empty default constructor",
                currentClassElement.getQualifiedName().toString());

    }


    //打印错误信息
    private void error(Element element, String msg, Object... args) {
        mMessager.printMessage(Diagnostic.Kind.ERROR, String.format(msg, args), element);
    }
}
