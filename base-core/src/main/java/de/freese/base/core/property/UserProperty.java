package de.freese.base.core.property;

import java.util.Date;

/**
 * Object für den Inhalt eines Propertys eines Users.
 *
 * @author Thomas Freese
 */
public class UserProperty extends Property
{
    /**
     *
     */
    private boolean changed;
    /**
     *
     */
    private boolean created;
    /**
     *
     */
    private boolean deleted;
    /**
     *
     */
    private Date lastAccess;
    /**
     *
     */
    private PropertyType typ;
    /**
     *
     */
    private String userName;

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object obj)
    {
        if (this == obj)
        {
            return true;
        }

        if (!super.equals(obj) || !(obj instanceof UserProperty other) || (this.changed != other.changed) || (this.deleted != other.deleted))
        {
            return false;
        }

        if (this.created != other.created)
        {
            return false;
        }

        if (getUserName() == null)
        {
            return other.getUserName() == null;
        }

        return getUserName().equals(other.getUserName());
    }

    /**
     * Liefert das Datum des letzten Zugriffs.
     *
     * @return {@link Date}
     */
    public Date getLastAccess()
    {
        return this.lastAccess;
    }

    /**
     * Liefert den Typ.
     *
     * @return {@link PropertyType}
     */
    public PropertyType getTyp()
    {
        return this.typ;
    }

    /**
     * Liefert den Namen des Users.
     *
     * @return String
     */
    public String getUserName()
    {
        return this.userName;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = super.hashCode();
        result = (prime * result) + (this.changed ? 1231 : 1237);
        result = (prime * result) + (this.deleted ? 1231 : 1237);
        result = (prime * result) + (this.created ? 1231 : 1237);
        result = (prime * result) + ((getUserName() == null) ? 0 : getUserName().hashCode());

        return result;
    }

    /**
     * True, wenn dieses Property als geändert markiert wurde.
     *
     * @return boolean
     */
    public boolean isChanged()
    {
        return this.changed;
    }

    /**
     * True, wenn dieses Property als neu markiert wurde.
     *
     * @return boolean
     */
    public boolean isCreated()
    {
        return this.created;
    }

    /**
     * True, wenn dieses Property als gelöscht markiert wurde.
     *
     * @return boolean
     */
    public boolean isDeleted()
    {
        return this.deleted;
    }

    /**
     * True, wenn dieses Property als geändert markiert wurde.
     *
     * @param isChanged boolean
     */
    public void setChanged(final boolean isChanged)
    {
        if (this.created)
        {
            return;
        }

        this.changed = isChanged;
    }

    /**
     * True, wenn dieses Property als neu markiert wurde.
     *
     * @param isCreated boolean
     */
    public void setCreated(final boolean isCreated)
    {
        this.created = isCreated;
    }

    /**
     * True, wenn dieses Property als gelöscht markiert wurde.
     *
     * @param isDeleted boolean
     */
    public void setDeleted(final boolean isDeleted)
    {
        this.deleted = isDeleted;
    }

    /**
     * Setzt das Datum des letzten Zugriffs.
     *
     * @param date {@link Date}
     */
    public void setLastAccess(final Date date)
    {
        this.lastAccess = date;
    }

    /**
     * Setzt den Typ.
     *
     * @param typ {@link PropertyType}
     */
    public void setType(final PropertyType typ)
    {
        this.typ = typ;
    }

    /**
     * Setzt den Namen des Users.
     *
     * @param userName String
     */
    public void setUserName(final String userName)
    {
        this.userName = userName;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder(super.toString());
        builder.append(", isChanged=").append(this.changed);
        builder.append(", isDeleted=").append(this.deleted);
        builder.append(", isNew=").append(this.created);

        return builder.toString();
    }
}
