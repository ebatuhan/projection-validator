package com.batu.util;

import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;

public final class Reporter {
    private final Messager messager;

    public Reporter(ProcessingEnvironment re){
        this.messager = re.getMessager();
    }

    public void reportTypeMismatch(TypeMirror expected, TypeMirror found, TypeMirror source, TypeMirror entity) {
    messager.printError(String.format(
        "Type mismatch in entity %s at %s:%n  expected: %s%n  found:    %s",
        entity,
        source,
        expected,
        found
    ));
}

    public void reportGetterNotFound(ExecutableElement found, TypeMirror source, TypeMirror entity) {
    messager.printError(String.format(
        "Getter not found in entity %s at %s:%n  missing getter for: %s",
        entity,
        source,
        found
    ));
}

}
