package com.zk.annotation_lib;

import javax.lang.model.element.Element;

/**
 * author: ZK.
 * date:   On 2018-10-24.
 */
public class ProcessingException extends Exception {


    private final Element mElement;

    public ProcessingException(Element element, String msg, Object... args) {

        super(String.format(msg, args));
        mElement = element;
    }

    public Element getElement() {
        return mElement;
    }
}
