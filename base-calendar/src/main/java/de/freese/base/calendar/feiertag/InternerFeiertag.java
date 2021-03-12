package de.freese.base.calendar.feiertag;

/**
 * Representiert einen internen Feiertag.
 *
 * @author Thomas Freese
 */
public class InternerFeiertag extends Feiertag
{
    /**
     *
     */
    private final int jahr;

    /**
     *
     */
    private final int monat;

    /**
     *
     */
    private final int tag;

    /**
     * Creates a new {@link InternerFeiertag} object.
     *
     * @param monat int
     * @param tag int
     * @param typ {@link FeiertagTyp}
     */
    public InternerFeiertag(final int monat, final int tag, final FeiertagTyp typ)
    {
        this(0, monat, tag, typ, false);
    }

    /**
     * Creates a new {@link InternerFeiertag} object.
     *
     * @param jahr int
     * @param monat int
     * @param tag int
     * @param typ {@link FeiertagTyp}
     * @param variablerFeiertag boolean
     */
    public InternerFeiertag(final int jahr, final int monat, final int tag, final FeiertagTyp typ, final boolean variablerFeiertag)
    {
        super(typ, variablerFeiertag);

        this.jahr = jahr;
        this.monat = monat;
        this.tag = tag;
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object obj)
    {
        if (!(obj instanceof InternerFeiertag))
        {
            return false;
        }

        InternerFeiertag statischerFeiertag = (InternerFeiertag) obj;

        if (getJahr() != statischerFeiertag.getJahr())
        {
            return false;
        }

        if (getMonat() != statischerFeiertag.getMonat())
        {
            return false;
        }

        if (getTag() != statischerFeiertag.getTag())
        {
            return false;
        }

        if (!getTyp().equals(statischerFeiertag.getTyp()))
        {
            return false;
        }

        return true;
    }

    /**
     * @return int
     */
    private final int getJahr()
    {
        return this.jahr;
    }

    /**
     * @return int
     */
    private final int getMonat()
    {
        return this.monat;
    }

    /**
     * @return int
     */
    private final int getTag()
    {
        return this.tag;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        return toString().hashCode();
    }

    /**
     * @see de.freese.base.calendar.feiertag.Feiertag#toString()
     */
    @Override
    public String toString()
    {
        return super.toString() + " (" + getJahr() + "_" + getMonat() + "_" + getTag() + ")";
    }
}
