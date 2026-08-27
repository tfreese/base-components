package de.freese.base.core.model;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author Thomas Freese
 */
public class AuditInfo implements Serializable {
    @Serial
    private static final long serialVersionUID = 146275334726328864L;

    private LocalDateTime changed;
    private String changedBy;
    private LocalDateTime created;
    private String createdBy;
    private LocalDateTime validFrom;
    private LocalDateTime validUntil;

    public LocalDateTime getChanged() {
        return changed;
    }

    public String getChangedBy() {
        return changedBy;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public LocalDateTime getValidFrom() {
        return validFrom;
    }

    public LocalDateTime getValidUntil() {
        return validUntil;
    }

    public void setChanged(final LocalDateTime changed) {
        this.changed = changed;
    }

    public void setChangedBy(final String changedBy) {
        this.changedBy = changedBy;
    }

    public void setCreated(final LocalDateTime created) {
        this.created = created;
    }

    public void setCreatedBy(final String createdBy) {
        this.createdBy = createdBy;
    }

    public void setValidFrom(final LocalDateTime validFrom) {
        this.validFrom = validFrom;
    }

    public void setValidUntil(final LocalDateTime validUntil) {
        this.validUntil = validUntil;
    }
}
