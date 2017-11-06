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
import javax.lang.model.element.Modifier;
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

    private static final String DEFAULT_STRING = "default_string";

    private static final int DEFAULT_NUMBER = 1;

    private static final char DEFAULT_CHAR = 'A';


    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        annotatedElements = roundEnv.getElementsAnnotatedWith(AutoParcel.class);

        List<TypeElement> types = new ArrayList<TypeElement>();
        types.addAll(ElementFilter.typesIn(annotatedElements));


        for (final Element annotatedElement : annotatedElements){
            final String elementName = annotatedElement.getSimpleName().toString();
            defaultValueStatements = new ArrayList<>();
            toImport = new ArrayList<>();

            checkAndAddStatement(toImport, "import android.os.Parcel;");
            checkAndAddStatement(toImport, "import " + getClassName(annotatedElement)+";");
            checkAndAddStatement(toImport, "import junit.framework.Assert;");
            checkAndAddStatement(toImport, "import org.junit.Test;");

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
        if (className.contains("Parcelable")) {
            return;
        } else if (className.contains("List") && className.contains("java.util")) {
            checkAndAddStatement(toImport, "import java.util.List;");
            checkAndAddStatement(toImport, "import java.util.ArrayList;");
            className = getClassNameFromCast(className);
            String packageName = getPackageName(className);
            if (packageName.length() > 0) {
                checkAndAddStatement(toImport, "import " + className+";");
            }
            checkAndAddStatement(defaultValueStatements, "List<"
                    + getSimpleClassName(className)
                    + ">"
                    + " test"
                    + getSimpleClassName(className)
                    + "List = new ArrayList<>();"
            );
            /*defaultValueStatements.add("List<"
                    + getSimpleClassName(className)
                    + ">"
                    + "test"
                    + getSimpleClassName(className)
                    + "List = new ArrayList<>();"
            );*/
            processSingleType(className, null, elementField.getSimpleName().toString());
            checkAndAddStatement(defaultValueStatements,"test"
                    + getSimpleClassName(className)
                    + "List.add("
                    + "test"
                    + getSimpleClassName(className)
                    + ");")
            ;
            /*defaultValueStatements.add("test"
                    + getSimpleClassName(className)
                    + "List.add("
                    + "test"
                    + getSimpleClassName(className)
                    + ");"
            );*/
            /*defaultValueStatements.add(testClassInstanceName
                    + ".set"
                    + getSimpleClassName(className)
                    + "List("
                    + "test"
                    + getSimpleClassName(className)
                    + ");"
            );*/
            checkAndAddStatement(defaultValueStatements,
                    testClassInstanceName
                            + ".set"
                            + getVariableName(elementField.getSimpleName().toString())
                            + "("
                            + "test"
                            + getSimpleClassName(className)
                            + "List);"
            );
        } else if(className.contains("Map")&& className.contains("java.util")) {
                checkAndAddStatement(toImport, "import java.util.Map;");
                checkAndAddStatement(toImport, "import java.util.HashMap;");
                className = getClassNameFromCast(className);
                className.replace(" ", "");
                String[] classNames = className.split(",");
                String testAdditions[] = new String[2];
                checkAndAddStatement(defaultValueStatements, "Map<"
                    + getSimpleClassName(classNames[0])
                    + ", "
                    + getSimpleClassName(classNames[1])
                    + ">"
                    + "test"
                    + getSimpleClassName(classNames[0])
                    + getSimpleClassName(classNames[1])
                    + "Map = new HashMap<>();");

                int count = 0;
                for (String classNamePart : classNames) {
                    String packageName = getPackageName(classNamePart);
                    if (packageName.length() > 0) {
                        checkAndAddStatement(toImport, "import " + classNamePart + ";");
                        processSingleType(classNamePart, null, elementField.getSimpleName().toString());
                        testAdditions[count] = "test"+getSimpleClassName(classNamePart);
                    }
                    else {
                        processSingleType(classNamePart, testClassInstanceName, elementField.getSimpleName().toString());
                    }
                    count++;

                /*defaultValueStatements.add("List<"
                        + getSimpleClassName(className)
                        + ">"
                        + "test"
                        + getSimpleClassName(className)
                        + "List = new ArrayList<>();"
                );*/
                /*defaultValueStatements.add("test"
                        + getSimpleClassName(className)
                        + "List.add("
                        + "test"
                        + getSimpleClassName(className)
                        + ");"
                );*/
                /*defaultValueStatements.add(testClassInstanceName
                        + ".set"
                        + getSimpleClassName(className)
                        + "List("
                        + "test"
                        + getSimpleClassName(className)
                        + ");"
                );*/

                }
            checkAndAddStatement(defaultValueStatements, "test"
                    + getSimpleClassName(classNames[0])
                    + getSimpleClassName(classNames[1])
                    + "Map.put("
                    + (testAdditions[0])
                    + ","
                    + (testAdditions[1])
                    + ");");
            checkAndAddStatement(defaultValueStatements,
                    testClassInstanceName
                            + ".set"
                            + getVariableName(elementField.getSimpleName().toString())
                            + "("
                            + "test"
                            + getSimpleClassName(classNames[0])
                            + getSimpleClassName(classNames[1])
                            + "Map);");

        } else if (className.contains("Set") && className.contains("java.util")) {
            checkAndAddStatement(toImport, "import java.util.Set;");
            checkAndAddStatement(toImport, "import java.util.HashSet;");
            className = getClassNameFromCast(className);
            String packageName = getPackageName(className);
            if (packageName.length() > 0) {
                checkAndAddStatement(toImport, "import " + className+";");
            }
            checkAndAddStatement(defaultValueStatements, "Set<"
                    + getSimpleClassName(className)
                    + ">"
                    + " test"
                    + getSimpleClassName(className)
                    + "Set = new HashSet<>();"
            );
            /*defaultValueStatements.add("List<"
                    + getSimpleClassName(className)
                    + ">"
                    + "test"
                    + getSimpleClassName(className)
                    + "List = new ArrayList<>();"
            );*/
            processSingleType(className, null, elementField.getSimpleName().toString());
            checkAndAddStatement(defaultValueStatements,"test"
                    + getSimpleClassName(className)
                    + "Set.add("
                    + "test"
                    + getSimpleClassName(className)
                    + ");")
            ;
            /*defaultValueStatements.add("test"
                    + getSimpleClassName(className)
                    + "List.add("
                    + "test"
                    + getSimpleClassName(className)
                    + ");"
            );*/
            /*defaultValueStatements.add(testClassInstanceName
                    + ".set"
                    + getSimpleClassName(className)
                    + "List("
                    + "test"
                    + getSimpleClassName(className)
                    + ");"
            );*/
            checkAndAddStatement(defaultValueStatements,
                    testClassInstanceName
                            + ".set"
                            + getVariableName(elementField.getSimpleName().toString())
                            + "("
                            + "test"
                            + getSimpleClassName(className)
                            + "Set);"
            );
        }
        else {
            processSingleType(className, testClassInstanceName, elementField.getSimpleName().toString());
        }

    }

    public boolean checkAndAddStatement(List<String> list, String statement) {
        if (!list.contains(statement)){
            list.add(statement);
            return true;
        }
        return false;
    }

    private void processSingleType(String className, String testClassInstanceName, String elementField) {
        if (checkAndProcessWrapper(className, testClassInstanceName, elementField))
            return;
        switch (className) {
            case "int":
                String intField = testClassInstanceName
                        + ".set"
                        + getVariableName(elementField)
                        + "(1);";
                //defaultValueStatements.add(intField);
                checkAndAddStatement(defaultValueStatements, intField);
                break;
            case "java.lang.String":
                String stringField = testClassInstanceName
                        + ".set"
                        + getVariableName(elementField)
                        + "(\"String\");";
                //defaultValueStatements.add(stringField);
                checkAndAddStatement(defaultValueStatements, stringField);
                break;
            default:
                String packageName = getPackageName(className);
                if (packageName.length() > 0) {
                    checkAndAddStatement(toImport, "import " + className+";");
                }
                String simpleClassName = getSimpleClassName(className);
                String newStatement = simpleClassName
                        + " test"
                        + simpleClassName
                        + " = "
                        + " new "
                        +  simpleClassName
                        + "();";
                String testChildClassInstanceName = "test" + simpleClassName;
                //defaultValueStatements.add(newStatement);
                checkAndAddStatement(defaultValueStatements, newStatement);
                for (Element annotatedElement : annotatedElements) {
                    if (annotatedElement.getSimpleName().toString().equals(simpleClassName)) {
                        addDefaultValuesToFieldsForClass(annotatedElement, testChildClassInstanceName);
                    }
                }
                if (testClassInstanceName != null) {
                    String objAssignment = testClassInstanceName
                            + ".set"
                            + getVariableName(elementField)
                            + "("
                            + testChildClassInstanceName
                            + ");";
                    //defaultValueStatements.add(objAssignment);
                    checkAndAddStatement(defaultValueStatements, objAssignment);
                }
                break;
        }
    }

    private boolean checkAndProcessWrapper(String className, String testClassInstanceName, String elementField) {
        /**
         * NUMERICAL WRAPPERS
         */
        if (className.contains("Integer")) {
            checkAndAddStatement(toImport, "import java.lang.Integer;");
            checkAndAddStatement(defaultValueStatements, "Integer testInteger = new Integer(" + DEFAULT_NUMBER + ");");
            return true;
        } else if (className.contains("Long")) {
            checkAndAddStatement(toImport, "import java.lang.Long;");
            checkAndAddStatement(defaultValueStatements, "Long testLong = new Long(" + DEFAULT_NUMBER + ");");
            return true;
        } else if (className.contains("Short")) {
            checkAndAddStatement(toImport, "import java.lang.Short;");
            checkAndAddStatement(defaultValueStatements, "Short testShort = new Short(" + DEFAULT_NUMBER + ");");
            return true;
        } else if (className.contains("Byte")) {
            checkAndAddStatement(toImport, "import java.lang.Byte;");
            checkAndAddStatement(defaultValueStatements, "Byte testByte = new Byte(" + DEFAULT_NUMBER + ")");
            return true;
        } else if (className.contains("Float")) {
            checkAndAddStatement(toImport, "import java.lang.Long;");
            checkAndAddStatement(defaultValueStatements, "Float testFloat = new Float(" + DEFAULT_NUMBER + ");");
            return true;
        } else if (className.contains("Double")) {
            checkAndAddStatement(toImport, "import java.lang.Long;");
            checkAndAddStatement(defaultValueStatements, "Double testDouble = new Double(" + DEFAULT_NUMBER + ");");
            return true;
        }
        /**
         * Boolean
         */
        else if (className.contains("Boolean")) {
            checkAndAddStatement(toImport, "import java.lang.Boolean;");
            checkAndAddStatement(defaultValueStatements, "Boolean testBoolean = new Boolean(false);");
            return true;
        } else if (className.contains("Character")) {
            checkAndAddStatement(toImport, "import java.lang.Character;");
            checkAndAddStatement(defaultValueStatements, "Character testCharacter = new Character(" + DEFAULT_CHAR + "));");
            return true;
        } else if (className.contains("String")) {
            checkAndAddStatement(toImport, "import java.lang.String;");
            checkAndAddStatement(defaultValueStatements, "String testString = \"" + DEFAULT_STRING + "\";");
            if (testClassInstanceName != null) {
                String stringField = testClassInstanceName
                        + ".set"
                        + getVariableName(elementField)
                        + "(testString);";
                checkAndAddStatement(defaultValueStatements, stringField);
            }
            return true;
        }
        return false;
    }

    private String getClassNameFromCast(String className) {
        int startIndex = className.indexOf("<");
        int lastIndex = className.indexOf(">");
        return className.substring(startIndex+1, lastIndex);
    }


    private void addDefaultValuesToFieldsForClass(Element annotatedElement, String testClassInstanceName) {
        for (Element elementField : annotatedElement.getEnclosedElements()) {
            if (elementField.getKind().equals(ElementKind.FIELD) && !elementField.getModifiers().contains(Modifier.STATIC)) {
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


    private String getVariableName(String name) {
        return name.substring(0, 1).toUpperCase() + name.substring(1);
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
