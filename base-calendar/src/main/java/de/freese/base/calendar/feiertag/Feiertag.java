package de.freese.base.calendar.feiertag;

/**
 * @author Thomas Freese
 */
public class Feiertag
{
    private final FeiertagTyp typ;

    private final boolean variablerFeiertag;

    public Feiertag(final FeiertagTyp typ, final boolean variablerFeiertag)
    {
        super();

        this.typ = typ;
        this.variablerFeiertag = variablerFeiertag;
    }

    public final FeiertagTyp getTyp()
    {
        return this.typ;
    }

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
