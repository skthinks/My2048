package com.example;

import com.samskivert.mustache.Mustache;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import javax.tools.JavaFileObject;

@SupportedAnnotationTypes("com.example.AutoParcel")
public final class AutoParcelProcessor extends AbstractProcessor {

    List<String> defaultValueStatements;

    Collection<? extends Element> annotatedElements;

    List<String> toImport;

    String testClassParentInstance;

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        annotatedElements = roundEnv.getElementsAnnotatedWith(AutoParcel.class);

        List<TypeElement> types = new ArrayList<TypeElement>();
        types.addAll(ElementFilter.typesIn(annotatedElements));


        for (Element annotatedElement : annotatedElements){
            final String elementName = annotatedElement.getSimpleName().toString();
            defaultValueStatements = new ArrayList<>();
            toImport = new ArrayList<>();

            toImport.add("import android.os.Parcel;");
            toImport.add("import " + getClassName(annotatedElement)+";");
            toImport.add("import junit.framework.Assert;");
            toImport.add("import org.junit.Test;");

            String testClassInstanceName = "test"+annotatedElement.getSimpleName().toString();
            testClassParentInstance = testClassInstanceName;
            addDefaultValuesToFieldsForClass(annotatedElement, testClassInstanceName);

            String templateString = readTemplate();

            String string = Mustache.compiler().escapeHTML(false).compile(templateString).execute(new Object() {
                // Following objects are used by mustache to generate code from template.
                String name = elementName;
                Object defaultInstanceValues = defaultValueStatements;
                Object imports = toImport;

            });

            StringBuilder builder = new StringBuilder();
            builder.append(string);

            writeSourceFile(builder, elementName);
        }
        // We are the only ones handling AutoParcel annotations
        return true;
    }

    private void addDefaultValuesToFields(Element elementField, String testClassInstanceName) {
        String className = getClassName(elementField);
        if (className.contains(">")) {
            return;
        }
        String packageName = getPackageName(className);
        if (packageName.length() > 0 && !("java.lang.String").equals(className)) {
            toImport.add("import " + className+";");
        }

        switch (className) {
            case "int":
                String intField = testClassInstanceName
                        + ".set"
                        + getVariableName(elementField)
                        + "(1);";
                defaultValueStatements.add(intField);
                break;
            case "java.lang.String":
                String stringField = testClassInstanceName
                        + ".set"
                        + getVariableName(elementField)
                        + "(\"String\");";
                defaultValueStatements.add(stringField);
                break;
            default:
                String simpleClassName = getSimpleClassName(className);
                String newStatement = simpleClassName
                        + " test"
                        + simpleClassName
                        + " = "
                        + " new "
                        +  simpleClassName
                        + "();";
                String testChildClassInstanceName = "test" + simpleClassName;
                defaultValueStatements.add(newStatement);
                for (Element annotatedElement : annotatedElements) {
                    if (annotatedElement.getSimpleName().toString().equals(simpleClassName)) {
                        addDefaultValuesToFieldsForClass(annotatedElement, testChildClassInstanceName);
                    }
                }
                String objAssignment = testClassInstanceName
                        + ".set"
                        + simpleClassName
                        + "("
                        + testChildClassInstanceName
                        + ");";
                defaultValueStatements.add(objAssignment);
                break;
        }
    }


    private void addDefaultValuesToFieldsForClass(Element annotatedElement, String testClassInstanceName) {
        for (Element elementField : annotatedElement.getEnclosedElements()) {
            if (elementField.getKind().equals(ElementKind.FIELD)) {
                addDefaultValuesToFields(elementField, testClassInstanceName);
            }
        }
    }

    private String getSimpleClassName(String className) {
        String components[] = className.split("\\.");
        return components[components.length - 1];
    }

    private String getPackageName(String className) {
        String components[] = className.split("\\.");
        String packageName = "";
        for (int i = 0; i <= components.length - 2; i++) {
            packageName +=  components[i];
            packageName += i == (components.length - 2) ? ";" : ".";
        }
        return packageName;
    }

    private String getClassName(Element elementField) {
        TypeMirror fieldType = elementField.asType();
        String fullTypeClassName = fieldType.toString();
        return fullTypeClassName;
    }


    private String getVariableName(Element elementField) {
        String name = elementField.getSimpleName().toString();
        String capName = name.substring(0, 1).toUpperCase() + name.substring(1);
        return capName;
    }


    private String readTemplate() {
        String template = null;

        InputStream inputStream = this.getClass().getResourceAsStream("/templates/widget_template.txt");
        if (inputStream == null) return null;

        Scanner s = new Scanner(inputStream).useDelimiter("\\A");
        template = s.hasNext() ? s.next() : "";

        return template;
    }


    private void writeSourceFile(
            StringBuilder builder, String fileName) {
        try { // write the file
            JavaFileObject source = processingEnv.getFiler().createSourceFile("com.grofers.annotations.generated." + fileName + "Test");


            Writer writer = source.openWriter();
            writer.write(builder.toString());
            writer.flush();
            writer.close();
        } catch (IOException e) {
            // Note: calling e.printStackTrace() will print IO errors
            // that occur from the file already existing after its first run, this is normal
        }
    }
}
