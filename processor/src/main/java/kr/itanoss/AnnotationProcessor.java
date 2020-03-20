package kr.itanoss;

import com.google.auto.service.AutoService;
import lombok.Data;
import lombok.Value;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.lang.annotation.Annotation;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toList;

@AutoService(Processor.class)
@SupportedAnnotationTypes("*")
public class AnnotationProcessor extends AbstractProcessor {

    private Messager messager;
    private List<Element> deferedElement = new LinkedList<>();

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        messager = processingEnv.getMessager();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (Element element : deferedElement) {
            printConstructors(element);
        }
        deferedElement.clear();

        Set<? extends Element> interestingElements = roundEnv.getElementsAnnotatedWith(Interesting.class);
        for (Element element : interestingElements) {
            if (isAnnotatedWithAny(element, Value.class, Data.class)) {
                deferedElement.add(element);
                continue;
            }

            printConstructors(element);
        }

        return false;
    }

    private boolean isAnnotatedWithAny(Element element, Class<? extends Annotation>... annotationTypes) {
        for (Class<? extends Annotation> annotationType : annotationTypes) {
            if (element.getAnnotation(annotationType) != null) {
                return true;
            }
        }

        return false;
    }

    private void printConstructors(Element element) {
        List<ExecutableElement> constructors = element.getEnclosedElements().stream()
                .filter(e -> e.getKind() == ElementKind.CONSTRUCTOR)
                .map(e -> (ExecutableElement) e)
                .collect(toList());
        List<String> parameterNames = constructors.stream()
                .flatMap(ee -> ee.getParameters().stream())
                .map(ve -> ve.getSimpleName().toString())
                .collect(toList());
        messager.printMessage(Diagnostic.Kind.NOTE, "Constructors: " + constructors.toString());
        messager.printMessage(Diagnostic.Kind.NOTE, "Parameter names: " + parameterNames.toString());
    }
}
