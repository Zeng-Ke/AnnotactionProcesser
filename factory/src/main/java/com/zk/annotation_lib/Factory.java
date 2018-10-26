package com.zk.annotation_lib;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * author: ZK.
 * date:   On 2018-10-15.
 */


@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface Factory {

    String id();

    Class type();

}
