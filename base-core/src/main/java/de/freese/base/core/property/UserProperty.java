package de.freese.base.core.property;

import java.util.Date;

/**
 * Object fuer den Inhalt eines Propertys eines Users.
 *
 * @author Thomas Freese
 */
public class UserProperty extends Property
{
    /**
     *
     */
    private boolean changed = false;

    /**
     *
     */
    private boolean created = false;

    /**
     *
     */
    private boolean deleted = false;

    /**
     *
     */
    private Date lastAccess = null;

    /**
     *
     */
    private PropertyType typ = null;

    /**
     *
     */
    private String userName = null;

    /**
     * Erstellt ein neues {@link UserProperty} Object.
     */
    public UserProperty()
    {
        super();
    }

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

        if (!super.equals(obj))
        {
            return false;
        }

        if (!(obj instanceof UserProperty))
        {
            return false;
        }

        UserProperty other = (UserProperty) obj;

        if (this.changed != other.changed)
        {
            return false;
        }

        if (this.deleted != other.deleted)
        {
            return false;
        }

        if (this.created != other.created)
        {
            return false;
        }

        if (getUserName() == null)
        {
            if (other.getUserName() != null)
            {
                return false;
            }
        }
        else if (!getUserName().equals(other.getUserName()))
        {
            return false;
        }

        return true;
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
     * True, wenn dieses Property als geaendert markiert wurde.
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
     * True, wenn dieses Property als geluescht markiert wurde.
     *
     * @return boolean
     */
    public boolean isDeleted()
    {
        return this.deleted;
    }

    /**
     * True, wenn dieses Property als geaendert markiert wurde.
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
     * True, wenn dieses Property als geloescht markiert wurde.
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
