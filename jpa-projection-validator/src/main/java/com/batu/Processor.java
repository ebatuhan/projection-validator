package com.batu;

import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;

import com.batu.api.JPAProjection;
import com.batu.model.ValidationContext;
import com.batu.util.Reporter;
import com.batu.util.ValueExtractor;
import com.batu.visitor.ProjectionVisitor;

@SupportedAnnotationTypes("com.batu.JPAProjection")
@SupportedSourceVersion(SourceVersion.RELEASE_21)
public class Processor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment re) {
        for (Element element : re.getElementsAnnotatedWith(JPAProjection.class)) {
            TypeElement entity = getEntityElement(ValueExtractor.asTypeMirror(element));

            ValidationContext p = new ValidationContext(
                    entity,
                    processingEnv,
                    new Reporter(processingEnv));

            element.accept(new ProjectionVisitor(), p);
        }

        return true;
    }

    private TypeElement getEntityElement(TypeMirror entityMirror) {
        return (TypeElement) processingEnv.getTypeUtils()
                .asElement(entityMirror);
    }

    public void tp(String msg) {
        processingEnv.getMessager().printWarning(msg);
    }

}
