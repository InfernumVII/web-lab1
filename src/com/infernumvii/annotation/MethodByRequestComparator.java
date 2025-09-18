package com.infernumvii.annotation;


import java.util.Comparator;

import com.infernumvii.annotation.model.Method;

public class MethodByRequestComparator implements Comparator<java.lang.reflect.Method> {

    @Override
    public int compare(java.lang.reflect.Method o1, java.lang.reflect.Method o2) {
        Request request1 = o1.getAnnotation(Request.class);
        Request request2 = o2.getAnnotation(Request.class);
        //TODO uris not implemented
        if (request1.method().equals(Method.ANY) && !request2.method().equals(Method.ANY)) {
            return 1;
        } else if (!request1.method().equals(Method.ANY) && request2.method().equals(Method.ANY)) {
            return -1;
        }
        return 0;
    }

}