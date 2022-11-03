package de.freese.base.core.property;

import java.util.Date;
import java.util.Objects;

/**
 * Object für den Inhalt eines Property's eines Users.
 *
 * @author Thomas Freese
 */
public class UserProperty extends Property
{
    private boolean changed;

    private boolean created;

    private boolean deleted;

    private Date lastAccess;

    private PropertyType typ;

    private String userName;

    @Override
    public boolean equals(final Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (!(o instanceof final UserProperty that))
        {
            return false;
        }
        return changed == that.changed && created == that.created && deleted == that.deleted && Objects.equals(userName, that.userName);
    }

    public Date getLastAccess()
    {
        return this.lastAccess;
    }

    public PropertyType getTyp()
    {
        return this.typ;
    }

    public String getUserName()
    {
        return this.userName;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(changed, created, deleted, userName);
    }

    public boolean isChanged()
    {
        return this.changed;
    }

    public boolean isCreated()
    {
        return this.created;
    }

    public boolean isDeleted()
    {
        return this.deleted;
    }

    public void setChanged(final boolean isChanged)
    {
        if (this.created)
        {
            return;
        }

        this.changed = isChanged;
    }

    public void setCreated(final boolean isCreated)
    {
        this.created = isCreated;
    }

    public void setDeleted(final boolean isDeleted)
    {
        this.deleted = isDeleted;
    }

    public void setLastAccess(final Date date)
    {
        this.lastAccess = date;
    }

    public void setType(final PropertyType typ)
    {
        this.typ = typ;
    }

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
