package com.zk.annotation_lib;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.MirroredTypeException;

/**
 * author: ZK.
 * date:   On 2018-10-23.
 */
public class FactoryAnnotationClass {


    private final TypeElement mAnnotatedClassElement;
    private String mAnnotateTypeName;
    private String mAnnotatedTypeSimpleName;
    private final String mId;

    public FactoryAnnotationClass(TypeElement typeElement) throws IllegalArgumentException {

        mAnnotatedClassElement = typeElement;
        Factory factory = mAnnotatedClassElement.getAnnotation(Factory.class);
        mId = factory.id(); //获取 {@link Factory#id()}中指定的id()
        if (mId == null || "".equals(mId)) {
            throw new IllegalArgumentException(/*String.format("id() in %1$s for class %2$s is Null,that's not allow", Factory.class
                    .getSimpleName(), mAnnotatedClassElement.getQualifiedName().toString())*/"id()  is Null,that's " +
                    "not allow");
        }

        try {
            /**
             * 类已被编译的情况，如果第三方.jar包包含已编译的被@Factory注解的.class文件。这时可以通过try{}中代码块获取
             */
            Class<?> clz = factory.type();
            mAnnotateTypeName = clz.getCanonicalName();// 获取Factory#type()中指定的type()的类型合法全名
            mAnnotatedTypeSimpleName = clz.getSimpleName(); // 获取Factory#type()中指定的type()的类型简单名字

        } catch (MirroredTypeException mte) {

            /**
             * 这种情况是我们尝试编译被@Factory注解的源代码。这种情况会直接抛出MirroredTypeException。幸运的是我们可以通过该Exception去获取所需变量。
             */
            DeclaredType declaredType = (DeclaredType) mte.getTypeMirror(); //获取未编译类
            TypeElement element = (TypeElement) declaredType.asElement();
            mAnnotateTypeName = element.getQualifiedName().toString();
            mAnnotatedTypeSimpleName = element.getSimpleName().toString();
        }

    }


    //获取被@Factory注解的原始元素
    public TypeElement getAnnotatedClassElement() {
        return mAnnotatedClassElement;
    }

    //// 获取Factory#type()中指定的type()的类型合法全名
    public String getAnnotateTypeName() {
        return mAnnotateTypeName;
    }

    // 获取Factory#type()中指定的type()的类型简单名字
    public String getAnnotatedTypeSimpleName() {
        return mAnnotatedTypeSimpleName;
    }

    //获取 {@link Factory#id()}中指定的id()
    public String getId() {
        return mId;
    }
}
