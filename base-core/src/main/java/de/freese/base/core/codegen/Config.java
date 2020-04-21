/**
 * Created: 26.07.2018
 */

package de.freese.base.core.codegen;

/**
 * Konfiguration für die Code-Erzeugung.
 *
 * @author Thomas Freese
 */
public class Config
{
    /**
    *
    */
    private boolean addFullConstructor = false;

    /**
    *
    */
    private String packageName = null;

    /**
    *
    */
    private boolean serializeable = false;

    /**
     * Erstellt ein neues {@link Config} Object.
     */
    public Config()
    {
        super();
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
