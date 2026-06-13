package com.batu.util;

import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
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

    public void reportTypeMismatch(
            ExecutableElement method,
            TypeMirror expected,
            TypeMirror found,
            TypeMirror source,
            TypeMirror entity
    ) {
        messager.printMessage(
                kind,
                String.format(
                        "Type mismatch in entity %s at %s:%n  expected: %s%n  found:    %s",
                        entity,
                        source,
                        expected,
                        found
                ),
                method
        );
    }

    public void reportGetterNotFound(
            ExecutableElement method,
            TypeMirror source,
            TypeMirror entity
    ) {
        messager.printMessage(
                kind,
                String.format(
                        "Getter not found in entity %s at %s:%n  missing getter for: %s",
                        entity,
                        source,
                        method
                ),
                method
        );
    }

    public void reportWrongAnnotation(Element element) {
        messager.printMessage(
                kind,
                String.format(
                        "Only interfaces should be annotated with @JPAProjection, but found %s: %s",
                        element.getKind().name().toLowerCase(),
                        element
                ),
                element
        );
    }
}