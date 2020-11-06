/**
 * Created: 29.07.2018
 */

package de.freese.base.core.codegen.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Definiert das Model einer Klasse.
 *
 * @author Thomas Freese
 */
public class ClassModel extends AbstractModel
{
    /**
    *
    */
    private boolean addFullConstructor;

    /**
    *
    */
    private final List<FieldModel> fields = new ArrayList<>();

    /**
    *
    */
    private final Set<String> imports = new TreeSet<>();

    /**
    *
    */
    private final List<Class<?>> interfaces = new ArrayList<>();

    /**
    *
    */
    private String packageName;

    /**
    *
    */
    private boolean serializeable = true;

    /**
     * Erstellt ein neues {@link ClassModel} Object.
     *
     * @param name String
     */
    public ClassModel(final String name)
    {
        super(name);
    }

    /**
     * @param name String
     * @param fieldClazz Class
     * @return fieldModel {@link FieldModel}
     */
    public FieldModel addField(final String name, final Class<?> fieldClazz)
    {
        return addField(name, fieldClazz.getName());
    }

    /**
     * @param name String
     * @param fieldClazzName String
     * @return fieldModel {@link FieldModel}
     */
    public FieldModel addField(final String name, final String fieldClazzName)
    {
        FieldModel fieldModel = new FieldModel(name, this, fieldClazzName);
        this.fields.add(fieldModel);

        return fieldModel;
    }

    /**
     * @param clazz {@link Class}
     */
    public void addImport(final Class<?> clazz)
    {
        addImport(clazz.getName());
    }

    /**
     * @param clazzName String
     */
    public void addImport(final String clazzName)
    {
        this.imports.add(clazzName);
    }

    /**
     * @param iface {@link Class}
     */
    public void addInterface(final Class<?> iface)
    {
        this.interfaces.add(iface);
        addImport(iface);
    }

    /**
     * @return {@link List}
     */
    public List<FieldModel> getFields()
    {
        return this.fields;
    }

    /**
     * @return {@link Set}
     */
    public Set<String> getImports()
    {
        Set<String> set = new TreeSet<>(this.imports);

        // @formatter:off
        getFields().stream()
            .filter(field -> !field.isFieldClassPrimitive())
            .filter(field -> !field.isFieldClassArray())
            .forEach(field -> set.add(field.getFieldClazzName()))
            ;
        // @formatter:on

        return set;
    }

    /**
     * @return {@link List}
     */
    public List<Class<?>> getInterfaces()
    {
        return this.interfaces;
    }

    /**
     * @return String
     */
    public String getPackageName()
    {
        return this.packageName;
    }

    /**
     * Konstruktor mit allen Parametern einbauen.
     *
     * @return boolean
     */
    public boolean isAddFullConstructor()
    {
        return this.addFullConstructor;
    }

    /**
     * @return boolean
     */
    public boolean isSerializeable()
    {
        return this.serializeable;
    }

    /**
     * Konstruktor mit allen Parametern einbauen.
     *
     * @param addFullConstructor boolean
     */
    public void setAddFullConstructor(final boolean addFullConstructor)
    {
        this.addFullConstructor = addFullConstructor;
    }

    /**
     * @param packageName String
     */
    public void setPackageName(final String packageName)
    {
        this.packageName = packageName;
    }

    /**
     * @param serializeable boolean
     */
    public void setSerializeable(final boolean serializeable)
    {
        this.serializeable = serializeable;
    }
}
