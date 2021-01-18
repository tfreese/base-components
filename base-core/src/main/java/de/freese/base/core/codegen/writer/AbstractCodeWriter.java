/**
 * Created: 29.07.2018
 */

package de.freese.base.core.codegen.writer;

import java.io.PrintStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import javax.annotation.processing.Generated;
import de.freese.base.core.codegen.model.ClassModel;
import de.freese.base.core.codegen.model.FieldModel;
import de.freese.base.utils.StringUtils;

/**
 * Java-Implementierung eines {@link CodeWriter}.
 *
 * @author Thomas Freese
 */
public abstract class AbstractCodeWriter implements CodeWriter
{
    /**
    *
    */
    protected static final String TAB = "    ";

    /**
     * Erstellt ein neues {@link AbstractCodeWriter} Object.
     */
    protected AbstractCodeWriter()
    {
        super();
    }

    /**
     * @see de.freese.base.core.codegen.writer.CodeWriter#write(de.freese.base.core.codegen.model.ClassModel, java.io.PrintStream)
     */
    @Override
    public void write(final ClassModel classModel, final PrintStream output) throws Exception
    {
        if (classModel.isSerializeable())
        {
            classModel.addInterface(Serializable.class);
        }

        if (classModel.isAddFullConstructor())
        {
            // Für Validierung.
            classModel.addImport(Objects.class);
        }

        // Default Class-Annotation.
        classModel.addImport(Generated.class);
        classModel.addAnnotation("@Generated(\"" + getClass().getName() + "\")");

        writePackage(classModel, output);
        writeImports(classModel, output);
        writeClassHeader(classModel, output);
        writeFields(classModel, output);
        writeConstructor(classModel, output);
        writeMethods(classModel, output);
        writeHashcode(classModel, output);
        writeEquals(classModel, output);
        writeToString(classModel, output);
        writeClassFooter(classModel, output);

        output.flush();
    }

    /**
     * @param classModel {@link ClassModel}
     * @param output {@link PrintStream}
     */
    protected void writeClassFooter(final ClassModel classModel, final PrintStream output)
    {
        output.println("}");
    }

    /**
     * @param classModel {@link ClassModel}
     * @param output {@link PrintStream}
     */
    protected void writeClassHeader(final ClassModel classModel, final PrintStream output)
    {
        // Class-JavaDoc
        output.println();
        writeJavaDoc(output, classModel.getComments(), "");

        // Class-Annotations
        classModel.getAnnotations().forEach(output::println);

        output.print("public class " + classModel.getName());

        // Interfaces
        if (!classModel.getInterfaces().isEmpty())
        {
            output.print(" implements ");

            StringJoiner joiner = new StringJoiner(", ");
            classModel.getInterfaces().forEach(i -> joiner.add(i.getSimpleName()));

            output.print(joiner.toString());
        }

        output.println();
        output.println("{");

        if (classModel.isSerializeable())
        {
            // UUID uuid = UUID.randomUUID();
            // long oid = (uuid.getMostSignificantBits() >> 32) ^ uuid.getMostSignificantBits();
            // oid ^= (uuid.getLeastSignificantBits() >> 32) ^ uuid.getLeastSignificantBits();
            long oid = (classModel.getPackageName() + "." + classModel.getName()).hashCode();

            writeJavaDoc(output, null, TAB);
            output.printf(TAB + "private static final long serialVersionUID = %dL;%n", oid);
        }
    }

    /**
     * @param classModel {@link ClassModel}
     * @param output {@link PrintStream}
     */
    protected void writeConstructor(final ClassModel classModel, final PrintStream output)
    {
        output.println();
        writeJavaDoc(output, Arrays.asList("Default Constructor"), TAB);

        output.println(TAB + "public " + classModel.getName() + "()");
        output.println(TAB + "{");
        output.println(TAB + TAB + "super();");
        output.println(TAB + "}");

        if (classModel.isAddFullConstructor())
        {
            output.println();
            writeJavaDoc(output, Arrays.asList("Full Constructor"), TAB, w -> {
                for (FieldModel fieldModel : classModel.getFields())
                {
                    output.printf(TAB + " * @param %s %s%n", fieldModel.getName(), fieldModel.getFieldClazzSimpleName());
                }
            });

            output.print(TAB + "public " + classModel.getName() + "(");

            for (Iterator<FieldModel> iterator = classModel.getFields().iterator(); iterator.hasNext();)
            {
                FieldModel fieldModel = iterator.next();

                output.print(fieldModel.getFieldClazzSimpleName() + " " + fieldModel.getName());

                if (iterator.hasNext())
                {
                    output.print(", ");
                }
            }

            output.println(")");
            output.println(TAB + "{");
            output.println(TAB + TAB + "super();");
            output.println();

            for (FieldModel fieldModel : classModel.getFields())
            {
                // if (fieldModel.getColumn().isNullable())
                // {
                // output.printf(TAB + TAB + "this.%1$s = %1$s;%n", fieldModel.getName());
                // }
                // else
                // {
                output.printf(TAB + TAB + "this.%1$s = Objects.requireNonNull(%1$s, \"not null value: %1$s required\");%n", fieldModel.getName());
                // }
            }

            output.println(TAB + "}");
        }
    }

    /**
     * @param classModel {@link ClassModel}
     * @param output {@link PrintStream}
     */
    protected void writeEquals(final ClassModel classModel, final PrintStream output)
    {
        String className = classModel.getName();

        output.println();
        writeJavaDoc(output, Arrays.asList("@see java.lang.Object#equals(java.lang.Object)"), TAB);
        output.println(TAB + "@Override");
        output.println(TAB + "public boolean equals(final Object obj)");
        output.println(TAB + "{");

        output.println(TAB + TAB + "if (this == obj)");
        output.println(TAB + TAB + "{");
        output.println(TAB + TAB + TAB + "return true;");
        output.println(TAB + TAB + "}");

        output.println();
        output.println(TAB + TAB + "if (obj == null)");
        output.println(TAB + TAB + "{");
        output.println(TAB + TAB + TAB + "return false;");
        output.println(TAB + TAB + "}");

        output.println();
        output.println(TAB + TAB + "if (getClass() != obj.getClass())");
        output.println(TAB + TAB + "{");
        output.println(TAB + TAB + TAB + "return false;");
        output.println(TAB + TAB + "}");

        output.println();
        output.printf(TAB + TAB + "%1$s other = (%1$s) obj;%n", className);

        for (FieldModel fieldModel : classModel.getFields())
        {
            String fieldName = fieldModel.getName();

            output.println();

            if (fieldModel.isFieldClassPrimitive())
            {
                output.printf(TAB + TAB + "if (this.%1$s != other.%1$s)%n", fieldName);
                output.println(TAB + TAB + "{");
                output.println(TAB + TAB + TAB + "return false;");
                output.println(TAB + TAB + "}");
            }
            else
            {
                output.println(TAB + TAB + "if (this." + fieldName + " == null)");
                output.println(TAB + TAB + "{");
                output.println(TAB + TAB + TAB + "if (other." + fieldName + " == null)");
                output.println(TAB + TAB + TAB + "{");
                output.println(TAB + TAB + TAB + TAB + "return false;");
                output.println(TAB + TAB + TAB + "}");
                output.println(TAB + TAB + "}");
                output.printf(TAB + TAB + "else if(!this.%1$s.equals(other.%1$s))%n", fieldName);
                output.println(TAB + TAB + "{");
                output.println(TAB + TAB + TAB + "return false;");
                output.println(TAB + TAB + "}");
            }
        }

        output.println();
        output.println(TAB + TAB + "return true;");
        output.println(TAB + "}");
    }

    /**
     * @param classModel {@link ClassModel}
     * @param output {@link PrintStream}
     */
    protected void writeFields(final ClassModel classModel, final PrintStream output)
    {
        if (classModel.isSerializeable())
        {
            output.println();
        }

        for (Iterator<FieldModel> iterator = classModel.getFields().iterator(); iterator.hasNext();)
        {
            FieldModel fieldModel = iterator.next();

            writeJavaDoc(output, fieldModel.getComments(), TAB);
            fieldModel.getAnnotations().forEach(i -> output.println(TAB + i));

            if (fieldModel.isCollection())
            {
                output.printf(TAB + "private List<%s> %s = %s;%n", fieldModel.getFieldClazzSimpleName(), fieldModel.getName(),
                        fieldModel.getDefaultValueAsString());
            }
            else
            {
                output.printf(TAB + "private %s %s = %s;%n", fieldModel.getFieldClazzSimpleName(), fieldModel.getName(), fieldModel.getDefaultValueAsString());
            }

            if (iterator.hasNext())
            {
                output.println();
            }
        }
    }

    /**
     * @param classModel {@link ClassModel}
     * @param output {@link PrintStream}
     */
    protected void writeHashcode(final ClassModel classModel, final PrintStream output)
    {
        output.println();
        writeJavaDoc(output, Arrays.asList("@see java.lang.Object#hashCode()"), TAB);
        output.println(TAB + "@Override");
        output.println(TAB + "public int hashCode()");
        output.println(TAB + "{");

        output.println(TAB + TAB + "final int prime = 31;");
        output.println(TAB + TAB + "int result = 1;");

        // double vorhanden ?
        for (FieldModel fieldModel : classModel.getFields())
        {
            if (fieldModel.getFieldClazz().equals(double.class))
            {
                output.println(TAB + TAB + "long temp = 0L;");
                break;
            }
        }

        output.println();

        for (FieldModel fieldModel : classModel.getFields())
        {
            String fieldName = fieldModel.getName();
            Class<?> clazz = fieldModel.getFieldClazz();

            if (clazz.equals(int.class))
            {
                output.println(TAB + TAB + "result = prime * result + this." + fieldName + ";");
            }
            else if (clazz.equals(long.class))
            {
                output.printf(TAB + TAB + "result = prime * result + (int) (this.%1$s ^ (this.%1$s >>> 32));%n", fieldName);
            }
            else if (clazz.equals(double.class))
            {
                output.println(TAB + TAB + "temp = Double.doubleToLongBits(this." + fieldName + ");");
                output.println(TAB + TAB + "result = prime * result + (int) (temp ^ (temp >>> 32));");
            }
            else if (clazz.equals(float.class))
            {
                output.println(TAB + TAB + "result = prime * result + Float.floatToIntBits(this." + fieldName + ");");
            }
            else
            {
                output.printf(TAB + TAB + "result = prime * result + ((this.%1$s == null) ? 0 : this.%1$s.hashCode());%n", fieldName);
            }
        }

        output.println();
        output.println(TAB + TAB + "return result;");
        output.println(TAB + "}");
    }

    /**
     * @param classModel {@link ClassModel}
     * @param output {@link PrintStream}
     */
    protected void writeImports(final ClassModel classModel, final PrintStream output)
    {
        output.println();
        classModel.getImports().forEach(i -> output.printf("import %s;%n", i));
    }

    /**
     * @param output {@link PrintStream}
     * @param comments {@link List}
     * @param indent String
     */
    protected void writeJavaDoc(final PrintStream output, final List<String> comments, final String indent)
    {
        writeJavaDoc(output, comments, indent, null);
    }

    /**
     * @param output {@link PrintStream}
     * @param comments {@link List}
     * @param indent String
     * @param paramsOrReturn {@link Consumer}
     */
    protected void writeJavaDoc(final PrintStream output, final List<String> comments, final String indent, final Consumer<PrintStream> paramsOrReturn)
    {
        output.println(indent + "/**");

        if ((comments == null) || comments.isEmpty())
        {
            // output.println(indent + " *");
        }
        else
        {
            for (Iterator<String> iterator = comments.iterator(); iterator.hasNext();)
            {
                String comment = iterator.next();

                output.print(indent + " * " + comment);

                if (iterator.hasNext())
                {
                    output.print("<br>");
                }

                output.println();
            }

            if (paramsOrReturn != null)
            {
                output.println(indent + " *");
            }
        }

        if (paramsOrReturn != null)
        {
            paramsOrReturn.accept(output);
        }

        output.println(indent + " */");
    }

    /**
     * @param classModel {@link ClassModel}
     * @param output {@link PrintStream}
     */
    protected void writeMethods(final ClassModel classModel, final PrintStream output)
    {
        for (FieldModel fieldModel : classModel.getFields())
        {
            String name = fieldModel.getName();
            String typeName = fieldModel.getFieldClazzSimpleName();

            // Setter
            output.println();

            if (fieldModel.isCollection())
            {
                writeJavaDoc(output, fieldModel.getComments(), TAB, w -> w.println(TAB + " * @param " + name + " List<" + typeName + ">"));
                output.println(TAB + "public void set" + StringUtils.capitalize(name) + "(List<" + typeName + "> " + name + ")");
            }
            else
            {
                writeJavaDoc(output, fieldModel.getComments(), TAB, w -> w.println(TAB + " * @param " + name + " " + typeName));
                output.println(TAB + "public void set" + StringUtils.capitalize(name) + "(" + typeName + " " + name + ")");
            }

            output.println(TAB + "{");

            // if (fieldModel.getColumn().isNullable())
            // {
            output.printf(TAB + TAB + "this.%1$s = %1$s;%n", name);
            // }
            // else
            // {
            // output.printf(TAB + TAB + "this.%1$s = Objects.requireNonNull(%1$s, \"not null value: %1$s required\");%n", fieldName);
            // }

            output.println(TAB + "}");

            // Getter
            output.println();

            if (fieldModel.isCollection())
            {
                writeJavaDoc(output, fieldModel.getComments(), TAB, w -> w.println(TAB + " * @return List<" + typeName + ">"));
                output.println(TAB + "public List<" + typeName + "> get" + StringUtils.capitalize(name) + "()");
            }
            else
            {
                writeJavaDoc(output, fieldModel.getComments(), TAB, w -> w.println(TAB + " * @return " + typeName));
                output.println(TAB + "public " + typeName + " get" + StringUtils.capitalize(name) + "()");
            }

            output.println(TAB + "{");
            output.println(TAB + TAB + "return this." + name + ";");
            output.println(TAB + "}");
        }
    }

    /**
     * @param classModel {@link ClassModel}
     * @param output {@link PrintStream}
     */
    protected void writePackage(final ClassModel classModel, final PrintStream output)
    {
        output.printf("// Created: %1$tY-%1$tm-%1$td %1$tH.%1$tM.%1$tS,%1$tL%n", new Date());
        output.printf("package %s;%n", classModel.getPackageName());
    }

    /**
     * @param classModel {@link ClassModel}
     * @param fields {@link ClassModel}
     * @param output {@link PrintStream}
     */
    protected void writeToString(final ClassModel classModel, final List<FieldModel> fields, final PrintStream output)
    {
        String className = classModel.getName();

        output.println();
        writeJavaDoc(output, Arrays.asList("@see java.lang.Object#toString()"), TAB);
        output.println(TAB + "@Override");
        output.println(TAB + "public String toString()");
        output.println(TAB + "{");

        output.println(TAB + TAB + "StringBuilder sb = new StringBuilder();");
        output.println(TAB + TAB + "sb.append(\"" + className + " [\");");

        for (Iterator<FieldModel> iterator = fields.iterator(); iterator.hasNext();)
        {
            FieldModel fieldModel = iterator.next();

            String fieldName = fieldModel.getName();

            if (fieldModel.isFieldClassArray())
            {
                output.printf(TAB + TAB + "sb.append(\"%1$s = \").append(Arrays.toString(this.%1$s))", fieldName);
            }
            else
            {
                output.printf(TAB + TAB + "sb.append(\"%1$s = \").append(this.%1$s)", fieldName);
            }

            if (iterator.hasNext())
            {
                output.println(".append(\", \");");
            }
            else
            {
                output.println(";");
            }
        }

        output.println(TAB + TAB + "sb.append(\"]\");");

        output.println();
        output.println(TAB + TAB + "return sb.toString();");
        output.println(TAB + "}");
    }

    /**
     * @param classModel {@link ClassModel}
     * @param output {@link PrintStream}
     */
    protected void writeToString(final ClassModel classModel, final PrintStream output)
    {
        writeToString(classModel, classModel.getFields().stream().filter(f -> f.isUseForToStringMethod()).collect(Collectors.toList()), output);
    }
}
