package com.batu.util;

import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.TypeMirror;

public final class Reporter {
    private final Messager messager;

    public Reporter(ProcessingEnvironment re){
        this.messager = re.getMessager();
    }

    public void reportTypeMismatch(TypeMirror expected, TypeMirror found){
        messager.printError("Type missmatch: expected " + expected.toString() + " found: " + found.toString());
    }

    public void reportGetterMismatch(ExecutableElement found){
        messager.printError("No getter found for" + found.getSimpleName());
    }
}
