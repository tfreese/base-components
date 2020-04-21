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
    private boolean addFullConstructor = false;

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
    private String packageName = null;

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
     * @param javaClazz Class
     * @return fieldModel {@link FieldModel}
     */
    public FieldModel addField(final String name, final Class<?> javaClazz)
    {
        FieldModel fieldModel = new FieldModel(name, this, javaClazz);
        this.fields.add(fieldModel);

        return fieldModel;
    }

    /**
     * @param clazz {@link Class}
     */
    public void addImport(final Class<?> clazz)
    {
        this.imports.add(clazz.getName());
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
            .filter(field -> !field.getJavaClazz().isPrimitive())
            .filter(field -> !field.getJavaClazz().isArray())
            .forEach(field -> set.add(field.getJavaClazz().getName()))
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
