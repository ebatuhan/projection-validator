package com.batu.visitor;

import java.util.Optional;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementScanner14;

import com.batu.api.JPAProjection;
import com.batu.model.ValidationContext;
import com.batu.util.ValueExtractor;
import com.batu.validator.MethodNameValidator;

public class ProjectionVisitor extends ElementScanner14<Void, ValidationContext> {

    @Override
    public Void visitExecutable(ExecutableElement method, ValidationContext validationContext) {
        if (method.getModifiers().contains(Modifier.DEFAULT)
                || method.getModifiers().contains(Modifier.STATIC)) {
            return null;
        }

        String methodName = method.getSimpleName().toString();

        Optional<? extends Element> entityFieldElement = validationContext.entity()
                .getEnclosedElements()
                .stream()
                .filter(el -> MethodNameValidator.accessorMatchesField(
                        methodName,
                        el.getSimpleName().toString()))
                .findFirst();

        if (entityFieldElement.isEmpty()) {
            validationContext.reporter().reportGetterNotFound(
                    method,
                    method.getEnclosingElement().asType(),
                    validationContext.entity().asType()
            );
            return null;
        }

        TypeMirror methodType = method.getReturnType();
        TypeMirror fieldType = entityFieldElement.get().asType();

        if (!compatible(methodType, fieldType, validationContext)) {
            validationContext.reporter().reportTypeMismatch(
                    method,
                    fieldType,
                    methodType,
                    method.getEnclosingElement().asType(),
                    validationContext.entity().asType()
            );
        }

        return super.visitExecutable(method, validationContext);
    }

    private boolean compatible(
            TypeMirror methodType,
            TypeMirror fieldType,
            ValidationContext validationContext
    ) {
        boolean methodIsCollection = isCollection(methodType, validationContext);
        boolean fieldIsCollection = isCollection(fieldType, validationContext);

        if (methodIsCollection != fieldIsCollection) {
            return false;
        }

        if (methodIsCollection) {
            TypeMirror methodArgument = getLastTypeArgument(methodType);
            TypeMirror fieldArgument = getLastTypeArgument(fieldType);

            if (methodArgument == null || fieldArgument == null) {
                return false;
            }

            return compatible(methodArgument, fieldArgument, validationContext);
        }

        Element methodTypeElement = validationContext.env()
                .getTypeUtils()
                .asElement(methodType);

        if (methodTypeElement != null
                && methodTypeElement.getAnnotation(JPAProjection.class) != null) {
            methodType = ValueExtractor.asTypeMirror(methodTypeElement);
        }

        return validationContext.env()
                .getTypeUtils()
                .isSameType(methodType, fieldType);
    }

    private boolean isCollection(TypeMirror type, ValidationContext validationContext) {
        TypeMirror collectionType = validationContext.env()
                .getElementUtils()
                .getTypeElement("java.util.Collection")
                .asType();

        return validationContext.env().getTypeUtils().isAssignable(
                validationContext.env().getTypeUtils().erasure(type),
                validationContext.env().getTypeUtils().erasure(collectionType)
        );
    }

    private TypeMirror getLastTypeArgument(TypeMirror type) {
        if (!(type instanceof DeclaredType declaredType)) {
            return null;
        }

        var arguments = declaredType.getTypeArguments();

        if (arguments.isEmpty()) {
            return null;
        }

        return arguments.getLast();
    }
}