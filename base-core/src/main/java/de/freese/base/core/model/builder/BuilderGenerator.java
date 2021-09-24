// Created: 20.04.2020
package de.freese.base.core.model.builder;

import java.io.PrintStream;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

import de.freese.base.core.concurrent.accumulative.AccumulativeRunnableScheduled;
import de.freese.base.utils.ReflectionUtils;

/**
 * @author Thomas Freese
 */
public class BuilderGenerator
{
    /**
     * @param args String[]
     */
    public static void main(final String[] args)
    {
        BuilderGenerator generator = new BuilderGenerator();
        generator.createBuilder(AccumulativeRunnableScheduled.class, true, System.out);
        // generator.createBuilder(Tupel2.class, true, System.out);
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

        fields.values().stream().filter(field -> !field.getType().isPrimitive())
                .forEach(field -> printStream.printf("import %s;%n", field.getType().getName()));

        printStream.println("");

        printStream.println("/**");
        printStream.println(" * @author Thomas Freese");
        printStream.println(" */");
        printStream.printf("public class %sBuilder implements %s<%s>%n", simpleClazzName, Builder.class.getSimpleName(), simpleClazzName);
        printStream.println("{");

        // Fields
        fields.values().forEach(field -> {
            printStream.println("   /**");
            printStream.println("    *");
            printStream.println("    */");
            printStream.printf("    private %s %s;%n", field.getType().getSimpleName(), field.getName());
            printStream.println();
        });

        printStream.println("   /**");
        printStream.println("    * @see de.freese.base.core.model.builder.Builder#build()");
        printStream.println("    */");
        printStream.println("   @Override");
        printStream.printf("   public %s build()", simpleClazzName);
        printStream.println("   {");
        printStream.println("       // TODO");
        printStream.println("       return null;");
        printStream.println("   }");

        // Methods
        fields.values().forEach(field -> {
            String fieldName = field.getName();
            String typeName = field.getType().getSimpleName();

            printStream.println();
            printStream.println("   /**");
            printStream.printf("    * @param %s %s%n", fieldName, typeName);
            printStream.println("    */");
            printStream.printf("   public %sBuilder %s(%s %s)%n", simpleClazzName, fieldName, typeName, fieldName);
            printStream.println("   {");
            printStream.printf("       this.%s = %s%n", fieldName, fieldName);
            printStream.println();
            printStream.println("       return this;");
            printStream.println("   }");
        });

        printStream.println("}");
    }

    /**
     * @param clazz Class
     * @param withSuperAttributes boolean
     *
     * @return {@link Map}
     */
    private Map<String, Field> getFields(final Class<?> clazz, final boolean withSuperAttributes)
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

        return fields;
    }
}
