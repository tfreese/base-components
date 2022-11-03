package de.freese.base.core.model;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * Klasse für Object-Informationen.
 *
 * @author Thomas Freese
 */
public class AuditInfo implements Serializable
{
    @Serial
    private static final long serialVersionUID = 146275334726328864L;

    private Date erstelltAm;

    private String erstelltVon = "";

    private Date geaendertAm;

    private String geaendertVon = "";

    private Date gueltigBis;

    private Date gueltigVon;

    public Date getErstelltAm()
    {
        return this.erstelltAm;
    }

    public String getErstelltVon()
    {
        return this.erstelltVon;
    }

    public Date getGeaendertAm()
    {
        return this.geaendertAm;
    }

    public String getGeaendertVon()
    {
        return this.geaendertVon;
    }

    public Date getGueltigBis()
    {
        return this.gueltigBis;
    }

    public Date getGueltigVon()
    {
        return this.gueltigVon;
    }

    public void setErstelltAm(final Date erstelltAm)
    {
        this.erstelltAm = erstelltAm;
    }

    public void setErstelltVon(final String erstelltVon)
    {
        this.erstelltVon = erstelltVon;
    }

    public void setGeaendertAm(final Date geaendertAm)
    {
        this.geaendertAm = geaendertAm;
    }

    public void setGeaendertVon(final String geaendertVon)
    {
        this.geaendertVon = geaendertVon;
    }

    public void setGueltigBis(final Date gueltigBis)
    {
        this.gueltigBis = gueltigBis;
    }

    public void setGueltigVon(final Date gueltigVon)
    {
        this.gueltigVon = gueltigVon;
    }
}
