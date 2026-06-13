package com.batu.util;

import javax.lang.model.element.Element;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeMirror;

import com.batu.api.JPAProjection;

public class ValueExtractor {
    public static TypeMirror asTypeMirror(Element projectionElement) {
        JPAProjection annotation = projectionElement.getAnnotation(JPAProjection.class);

        if (annotation == null) {
            return null;
        }

        try {
            annotation.entity();
            throw new IllegalStateException("Expected MirroredTypeException");
        } catch (MirroredTypeException exception) {
            return exception.getTypeMirror();
        }

    }
}
