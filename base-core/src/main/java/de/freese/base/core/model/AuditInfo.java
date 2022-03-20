package de.freese.base.core.model;

import java.io.Serializable;
import java.util.Date;

/**
 * Klasse für Object-Informationen.
 *
 * @author Thomas Freese
 */
public class AuditInfo implements Serializable
{
    /**
     *
     */
    private static final long serialVersionUID = 146275334726328864L;
    /**
     * Wann wurde das Objekt erstellt.
     */
    private Date erstelltAm;
    /**
     * Wer hat das Objekt erstellt.
     */
    private String erstelltVon = "";
    /**
     * Wann wurde das Objekt geändert.
     */
    private Date geaendertAm;
    /**
     * Wer hat das Objekt geändert.
     */
    private String geaendertVon = "";
    /**
     * Bis wann ist das Objekt gültig.
     */
    private Date gueltigBis;
    /**
     * Ab wann ist das Objekt gültig.
     */
    private Date gueltigVon;

    /**
     * @return {@link Date}
     */
    public Date getErstelltAm()
    {
        return this.erstelltAm;
    }

    /**
     * @return String
     */
    public String getErstelltVon()
    {
        return this.erstelltVon;
    }

    /**
     * @return {@link Date}
     */
    public Date getGeaendertAm()
    {
        return this.geaendertAm;
    }

    /**
     * @return String
     */
    public String getGeaendertVon()
    {
        return this.geaendertVon;
    }

    /**
     * @return {@link Date}
     */
    public Date getGueltigBis()
    {
        return this.gueltigBis;
    }

    /**
     * @return {@link Date}
     */
    public Date getGueltigVon()
    {
        return this.gueltigVon;
    }

    /**
     * @param erstelltAm {@link Date}
     */
    public void setErstelltAm(final Date erstelltAm)
    {
        this.erstelltAm = erstelltAm;
    }

    /**
     * @param erstelltVon String
     */
    public void setErstelltVon(final String erstelltVon)
    {
        this.erstelltVon = erstelltVon;
    }

    /**
     * @param geaendertAm {@link Date}
     */
    public void setGeaendertAm(final Date geaendertAm)
    {
        this.geaendertAm = geaendertAm;
    }

    /**
     * @param geaendertVon String
     */
    public void setGeaendertVon(final String geaendertVon)
    {
        this.geaendertVon = geaendertVon;
    }

    /**
     * @param gueltigBis {@link Date}
     */
    public void setGueltigBis(final Date gueltigBis)
    {
        this.gueltigBis = gueltigBis;
    }

    /**
     * @param gueltigVon {@link Date}
     */
    public void setGueltigVon(final Date gueltigVon)
    {
        this.gueltigVon = gueltigVon;
    }
}
