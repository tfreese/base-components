/**
 * Created: 29.07.2018
 */

package de.freese.base.core.codegen.model;

import java.util.Objects;

/**
 * Definiert das Model eines Klassen-Attributs.
 *
 * @author Thomas Freese
 */
public class FieldModel extends AbstractModel
{
    /**
     *
     */
    private final ClassModel classModel;

    /**
    *
    */
    private String defaultValueAsString = null;

    /**
     *
     */
    private final Class<?> javaClazz;

    /**
     * Erstellt ein neues {@link FieldModel} Object.
     *
     * @param name String
     * @param classModel {@link ClassModel}
     * @param javaClazz Class
     */
    FieldModel(final String name, final ClassModel classModel, final Class<?> javaClazz)
    {
        super(name);

        this.classModel = Objects.requireNonNull(classModel, "classModel required");
        this.javaClazz = Objects.requireNonNull(javaClazz, "javaClazz required");
    }

    /**
     * @return {@link ClassModel}
     */
    public ClassModel getClassModel()
    {
        return this.classModel;
    }

    /**
     * @return String
     */
    public String getDefaultValueAsString()
    {
        return this.defaultValueAsString;
    }

    /**
     * @return Class<?>
     */
    public Class<?> getJavaClazz()
    {
        return this.javaClazz;
    }

    /**
     * @param defaultValueAsString String
     */
    public void setDefaultValueAsString(final String defaultValueAsString)
    {
        this.defaultValueAsString = defaultValueAsString;
    }
}
