package de.freese.base.core.model;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * @author Thomas Freese
 */
public class AuditInfo implements Serializable {
    @Serial
    private static final long serialVersionUID = 146275334726328864L;

    private Date changed;
    private String changedBy;
    private Date created;
    private String createdBy;
    private Date validFrom;
    private Date validUntil;

    public Date getChanged() {
        return this.changed;
    }

    public String getChangedBy() {
        return this.changedBy;
    }

    public Date getCreated() {
        return this.created;
    }

    public String getCreatedBy() {
        return this.createdBy;
    }

    public Date getValidFrom() {
        return this.validFrom;
    }

    public Date getValidUntil() {
        return this.validUntil;
    }

    public void setChanged(final Date changed) {
        this.changed = changed;
    }

    public void setChangedBy(final String changedBy) {
        this.changedBy = changedBy;
    }

    public void setCreated(final Date created) {
        this.created = created;
    }

    public void setCreatedBy(final String createdBy) {
        this.createdBy = createdBy;
    }

    public void setValidFrom(final Date validFrom) {
        this.validFrom = validFrom;
    }

    public void setValidUntil(final Date validUntil) {
        this.validUntil = validUntil;
    }
}
