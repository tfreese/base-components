// Created: 20.04.2020
package de.freese.base.core.model.builder;

import java.io.PrintStream;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.function.UnaryOperator;

import de.freese.base.core.model.tupel.Tupel3;
import de.freese.base.utils.ReflectionUtils;

/**
 * @author Thomas Freese
 */
public class BuilderGenerator
{
    /**
     *
     */
    protected static final String INDENT = "    ";

    /**
     * @param args String[]
     */
    public static void main(final String[] args)
    {
        BuilderGenerator generator = new BuilderGenerator(fields -> {
            fields.remove("serialVersionUID");
            fields.remove("valueB");
            return fields;
        });
        generator.createBuilder(Tupel3.class, true, System.out);
    }

    /**
     *
     */
    private final UnaryOperator<Map<String, Field>> fieldHandler;

    /**
     * Erstellt ein neues {@link BuilderGenerator} Object.
     */
    public BuilderGenerator()
    {
        this(fields -> {
            fields.remove("serialVersionUID");
            return fields;
        });
    }

    /**
     * Erstellt ein neues {@link BuilderGenerator} Object.
     *
     * @param fieldHandler {@link UnaryOperator}
     */
    public BuilderGenerator(final UnaryOperator<Map<String, Field>> fieldHandler)
    {
        super();

        this.fieldHandler = Objects.requireNonNull(fieldHandler, "fieldHandler required");
    }

    /**
     * @param clazz Class
     * @param withSuperAttributes boolean
     * @param printStream {@link PrintStream}
     */
    public void createBuilder(final Class<?> clazz, final boolean withSuperAttributes, final PrintStream printStream)
    {
        Objects.requireNonNull(clazz, "clazz required");
        Objects.requireNonNull(printStream, "printStream required");

        Map<String, Field> fields = getFields(clazz, withSuperAttributes);

        final String simpleClazzName = clazz.getSimpleName().replace("Abstract", "");

        printStream.printf("import %s;%n", Builder.class.getName());

        // @formatter:off
        fields.values().stream()
                .filter(field -> !field.getType().isPrimitive())
                .map(field -> field.getType().getName())
                .distinct()
                .forEach(fieldType -> printStream.printf("import %s;%n", fieldType))
        ;
        // @formatter:on

        printStream.println();

        printStream.println("/**");
        printStream.println(" * @author Thomas Freese");
        printStream.println(" */");
        printStream.printf("public class %sBuilder implements %s<%s>%n", simpleClazzName, Builder.class.getSimpleName(), simpleClazzName);
        printStream.println("{");

        // Fields
        fields.values().forEach(field -> {
            printStream.println(INDENT + "/**");
            printStream.println(INDENT + " *");
            printStream.println(INDENT + " */");
            printStream.printf("%sprivate %s %s;%n", INDENT, field.getType().getSimpleName(), field.getName());
        });

        printStream.println();
        printStream.println(INDENT + "/**");
        printStream.println(INDENT + " * @see de.freese.base.core.model.builder.Builder#build()");
        printStream.println(INDENT + " */");
        printStream.println(INDENT + "@Override");
        printStream.printf("%spublic %s build()%n", INDENT, simpleClazzName);
        printStream.println(INDENT + "{");
        printStream.println(INDENT + INDENT + "// TODO");
        printStream.println(INDENT + INDENT + "return null;");
        printStream.println(INDENT + "}");

        // Methods
        fields.values().forEach(field -> {
            String fieldName = field.getName();
            String typeName = field.getType().getSimpleName();

            printStream.println();
            printStream.println(INDENT + "/**");
            printStream.printf(INDENT + " * @param %s %s%n", fieldName, typeName);
            printStream.println(INDENT + " */");
            printStream.printf("%spublic %sBuilder %s(%s %s)%n", INDENT, simpleClazzName, fieldName, typeName, fieldName);
            printStream.println(INDENT + "{");
            printStream.printf(INDENT + INDENT + "this.%s = %s%n", fieldName, fieldName);
            printStream.println();
            printStream.println(INDENT + INDENT + "return this;");
            printStream.println(INDENT + "}");
        });

        printStream.println("}");
    }

    /**
     * @param clazz Class
     * @param withSuperAttributes boolean
     *
     * @return {@link Map}
     */
    protected Map<String, Field> getFields(final Class<?> clazz, final boolean withSuperAttributes)
    {
        Map<String, Field> fields = new TreeMap<>();

        if (withSuperAttributes)
        {
            ReflectionUtils.doWithFields(clazz, field -> fields.put(field.getName(), field));
        }
        else
        {
            ReflectionUtils.doWithLocalFields(clazz, field -> fields.put(field.getName(), field));
        }

        return this.fieldHandler.apply(fields);
    }
}
