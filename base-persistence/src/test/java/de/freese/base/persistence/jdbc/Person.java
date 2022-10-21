// Created: 16.06.2016
package de.freese.base.persistence.jdbc;

/**
 * @author Thomas Freese
 */
public class Person
{
    private final long id;

    private final String nachname;

    private final String vorname;

    public Person(final long id, final String nachname, final String vorname)
    {
        super();

        this.id = id;
        this.nachname = nachname;
        this.vorname = vorname;
    }

    public long getId()
    {
        return this.id;
    }

    public String getNachname()
    {
        return this.nachname;
    }

    public String getVorname()
    {
        return this.vorname;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("Person [");
        builder.append("id=").append(this.id);
        builder.append(", nachname=").append(this.nachname);
        builder.append(", vorname=").append(this.vorname);
        builder.append("]");

        return builder.toString();
    }
}
