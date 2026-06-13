package com.batu.util;

import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;

public final class Reporter {
    private final Messager messager;
    private final Diagnostic.Kind kind;

    public Reporter(ProcessingEnvironment processingEnv) {
        this.messager = processingEnv.getMessager();

        String severity = processingEnv
            .getOptions()
            .getOrDefault("batu.reporter.severity", "error");

        this.kind = "warning".equalsIgnoreCase(severity)
            ? Diagnostic.Kind.WARNING
            : Diagnostic.Kind.ERROR;
    }


    public void reportTypeMismatch(TypeMirror expected, TypeMirror found, TypeMirror source, TypeMirror entity) {
    messager.printMessage(kind, String.format(
        "Type mismatch in entity %s at %s:%n  expected: %s%n  found:    %s",
        entity,
        source,
        expected,
        found
    ));
}

    public void reportGetterNotFound(ExecutableElement found, TypeMirror source, TypeMirror entity) {
    messager.printMessage(kind, String.format(
        "Getter not found in entity %s at %s:%n  missing getter for: %s",
        entity,
        source,
        found
    ));
}

}
