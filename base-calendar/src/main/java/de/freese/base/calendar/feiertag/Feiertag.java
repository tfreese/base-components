package de.freese.base.calendar.feiertag;

/**
 * Representiert einen Feiertag.
 *
 * @author Thomas Freese
 */
public class Feiertag
{
    /**
     *
     */
    private final FeiertagTyp typ;
    /**
     *
     */
    private final boolean variablerFeiertag;

    /**
     * Creates a new {@link Feiertag} object.
     *
     * @param typ {@link FeiertagTyp}
     * @param variablerFeiertag boolean
     */
    public Feiertag(final FeiertagTyp typ, final boolean variablerFeiertag)
    {
        super();

        this.typ = typ;
        this.variablerFeiertag = variablerFeiertag;
    }

    /**
     * Liefert den Namen des Feiertags.
     *
     * @return FeierTagTyp
     */
    public final FeiertagTyp getTyp()
    {
        return this.typ;
    }

    /**
     * Ist der Feiertag variabel ?
     *
     * @return boolean
     */
    public final boolean isVariablerFeiertag()
    {
        return this.variablerFeiertag;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        if (isVariablerFeiertag())
        {
            return "*" + getTyp().toString();
        }

        return getTyp().toString();
    }
}
