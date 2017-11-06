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

@SupportedAnnotationTypes("com.example.GenerateTest")
public final class AutoParcelProcessor extends AbstractProcessor {

    private List<String> defaultValueStatements;

    private Collection<? extends Element> annotatedElements;

    private List<String> toImport;

    private List<String> suiteImports;

    private List<String> suiteClasses;

    private static final String DEFAULT_STRING = "InstrumentationTestCommonLib.DEFAULT_STRING";

    private static final String DEFAULT_NUMBER = "InstrumentationTestCommonLib.DEFAULT_NUMBER";

    private static final String DEFAULT_CHAR = "InstrumentationTestCommonLib.DEFAULT_CHAR";

    private static final String DEFAULT_BOOL = "InstrumentationTestCommonLib.DEFAULT_BOOL";

    private static final String DEFAULT_LONG = "InstrumentationTestCommonLib.DEFAULT_LONG";



    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        annotatedElements = roundEnv.getElementsAnnotatedWith(GenerateTest.class);

        List<TypeElement> types = new ArrayList<TypeElement>();
        types.addAll(ElementFilter.typesIn(annotatedElements));

        suiteClasses =  new ArrayList<>();
        suiteImports = new ArrayList<>();

        for (final Element annotatedElement : annotatedElements){
            checkAndAddStatement(suiteClasses, annotatedElement.getSimpleName().toString() + "Test" +".class,");
            String className = getClassName(annotatedElement);
            checkAndAddStatement(suiteImports, "import " + className + ";");

            final String elementName = annotatedElement.getSimpleName().toString();
            defaultValueStatements = new ArrayList<>();
            toImport = new ArrayList<>();

            checkAndAddStatement(toImport, "import " + getClassName(annotatedElement)+";");

            String testClassInstanceName = "test"+annotatedElement.getSimpleName().toString();
            addDefaultValuesToFieldsForClass(annotatedElement, testClassInstanceName);

            boolean isParceller = annotatedElement.getAnnotation(GenerateTest.class).usesParcellerAnnotation();

            writeUnitTest(elementName, isParceller);
        }
        writeTestSuite();
        return true;
    }

    private void processListType(String testClassInstanceName, String className, Element elementField){
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
        processSingleType(className, null, elementField.getSimpleName().toString());
        checkAndAddStatement(defaultValueStatements,"test"
                + getSimpleClassName(className)
                + "List.add("
                + "test"
                + getSimpleClassName(className)
                + ");")
        ;
        checkAndAddStatement(defaultValueStatements,
                testClassInstanceName
                        + ".set"
                        + getVariableName(elementField.getSimpleName().toString())
                        + "("
                        + "test"
                        + getSimpleClassName(className)
                        + "List);"
        );
    }

    private void processMapType(String testClassInstanceName, String className, Element elementField){
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

    }


    private void addDefaultValuesToFields(Element elementField, String testClassInstanceName) {
        String className = getClassName(elementField);
        if (className.contains("Parcelable")) {
            return;
        } else if (className.contains("List") && className.contains("java.util")) {
            processListType(testClassInstanceName, className, elementField);
        } else if(className.contains("Map")&& className.contains("java.util")) {
            processMapType(testClassInstanceName, className, elementField);
        } else if (className.contains("Set") && className.contains("java.util")) {
            processSetType(testClassInstanceName, className, elementField);
        }
        else {
            processSingleType(className, testClassInstanceName, elementField.getSimpleName().toString());
        }

    }

    private void processSetType(String testClassInstanceName, String className, Element elementField) {
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
        processSingleType(className, null, elementField.getSimpleName().toString());
        checkAndAddStatement(defaultValueStatements,"test"
                + getSimpleClassName(className)
                + "Set.add("
                + "test"
                + getSimpleClassName(className)
                + ");")
        ;
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
            case "float":
            case "double":
            case "long":
            case "short":
            case "byte":
                String intField = testClassInstanceName
                        + ".set"
                        + getVariableName(elementField)
                        + "(" + DEFAULT_NUMBER + ");";
                checkAndAddStatement(defaultValueStatements, intField);
                break;
            case "java.lang.String":
                String stringField = testClassInstanceName
                        + ".set"
                        + getVariableName(elementField)
                        + "(\"" + DEFAULT_STRING +"\");";
                checkAndAddStatement(defaultValueStatements, stringField);
                break;
            case "boolean":
                String variableName = getVariableName(elementField);
                if (variableName.substring(0, 2).equals("Is")) {
                    variableName = variableName.substring(2);
                }
                String booleanField = testClassInstanceName
                        + ".set"
                        + variableName
                        + "(" + DEFAULT_BOOL + ");";
                checkAndAddStatement(defaultValueStatements, booleanField);
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
                checkAndAddStatement(defaultValueStatements, newStatement);
                /**
                 * Remove iteration check and throw warning if not annotated
                 */
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
            checkAndAddStatement(defaultValueStatements, "Long testLong = new Long(" + DEFAULT_LONG + ");");
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
            checkAndAddStatement(defaultValueStatements, "Boolean testBoolean = new Boolean(" + DEFAULT_BOOL + ");");
            return true;
        } else if (className.contains("Character")) {
            checkAndAddStatement(toImport, "import java.lang.Character;");
            checkAndAddStatement(defaultValueStatements, "Character testCharacter = new Character(" + DEFAULT_CHAR + "));");
            return true;
        } else if (className.contains("String")) {
            checkAndAddStatement(toImport, "import java.lang.String;");
            checkAndAddStatement(defaultValueStatements, "String testString = " + DEFAULT_STRING + ";");
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
            /*if (elementField.getKind().equals(ElementKind.FIELD)) {
                addDefaultValuesToFields(elementField, testClassInstanceName);
            }*/
            if (isElementFieldAndModifiersNotExist(elementField)) {
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


    private String readDefaultTestTemplate() {
        String template = null;

        InputStream inputStream = this.getClass().getResourceAsStream("/templates/test_template.txt");
        if (inputStream == null) return null;

        Scanner s = new Scanner(inputStream).useDelimiter("\\A");
        template = s.hasNext() ? s.next() : "";

        return template;
    }


    private void writeSourceFile(
            StringBuilder builder, String fileName) {
        try { // write the file
            JavaFileObject source = processingEnv.getFiler().createSourceFile("com.grofers.customerapp.testclasses." + fileName);


            Writer writer = source.openWriter();
            writer.write(builder.toString());
            writer.flush();
            writer.close();
        } catch (IOException e) {
            // Note: calling e.printStackTrace() will print IO errors
            // that occur from the file already existing after its first run, this is normal
        }
    }

    private void writeTestSuite() {
        String templateString = readSuiteTemplate();

        String string = Mustache.compiler().escapeHTML(false).compile(templateString).execute(new Object() {
            // Following objects are used by mustache to generate code from template.
            Object classes = suiteClasses;
            Object imports = suiteImports;
        });

        StringBuilder builder = new StringBuilder();
        builder.append(string);

        writeSourceFile(builder, "ModelTestSuite");
    }

    private void writeUnitTest(final String elementName, boolean isParceller) {
        String templateString = isParceller ? readParcelTestTemplate() : readDefaultTestTemplate();

        String string = Mustache.compiler().escapeHTML(false).compile(templateString).execute(new Object() {
            // Following objects are used by mustache to generate code from template.
            String name = elementName;
            Object defaultInstanceValues = defaultValueStatements;
            Object imports = toImport;
        });

        StringBuilder builder = new StringBuilder();
        builder.append(string);

        writeSourceFile(builder, elementName+"Test");
    }

    private String readParcelTestTemplate() {
        String template = null;

        InputStream inputStream = this.getClass().getResourceAsStream("/templates/test_parceler_template.txt");
        if (inputStream == null) return null;

        Scanner s = new Scanner(inputStream).useDelimiter("\\A");
        template = s.hasNext() ? s.next() : "";

        return template;
    }

    private String readSuiteTemplate() {
        String template = null;

        InputStream inputStream = this.getClass().getResourceAsStream("/templates/suite_template.txt");
        if (inputStream == null) return null;

        Scanner s = new Scanner(inputStream).useDelimiter("\\A");
        template = s.hasNext() ? s.next() : "";

        return template;
    }

    public boolean isElementFieldAndModifiersNotExist(Element elementField) {
        return ElementKind.FIELD.equals(elementField.getKind())
                && !elementField.getModifiers().contains(Modifier.STATIC)
                && !elementField.getModifiers().contains(Modifier.TRANSIENT)
                && !elementField.getModifiers().contains(Modifier.ABSTRACT)
                && !elementField.getModifiers().contains(Modifier.FINAL);
    }
}

