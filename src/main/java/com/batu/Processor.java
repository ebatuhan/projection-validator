package com.batu;

import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
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

@SupportedAnnotationTypes("com.batu.api.JPAProjection")
@SupportedSourceVersion(SourceVersion.RELEASE_21)
public class Processor extends AbstractProcessor {

    private Reporter reporter;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        this.reporter = new Reporter(processingEnv);
    }


    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment re) {
        for (Element element : re.getElementsAnnotatedWith(JPAProjection.class)) {
            if(!element.getKind().isInterface())
                reporter.reportWrongAnnotation(element);

            TypeElement entity = getEntityElement(ValueExtractor.asTypeMirror(element));

            ValidationContext p = new ValidationContext(
                    entity,
                    processingEnv,
                    reporter);

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
