package de.freese.base.calendar.feiertag;

/**
 * @author Thomas Freese
 */
public class InternerFeiertag extends Feiertag {
    private final int jahr;

    private final int monat;

    private final int tag;

    public InternerFeiertag(final int monat, final int tag, final FeiertagTyp typ) {
        this(0, monat, tag, typ, false);
    }

    public InternerFeiertag(final int jahr, final int monat, final int tag, final FeiertagTyp typ, final boolean variablerFeiertag) {
        super(typ, variablerFeiertag);

        this.jahr = jahr;
        this.monat = monat;
        this.tag = tag;
    }

    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof InternerFeiertag statischerFeiertag)) {
            return false;
        }

        return (getJahr() == statischerFeiertag.getJahr()) && (getMonat() == statischerFeiertag.getMonat()) && (getTag() == statischerFeiertag.getTag()) && getTyp().equals(statischerFeiertag.getTyp());
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    @Override
    public String toString() {
        return super.toString() + " (" + getJahr() + "_" + getMonat() + "_" + getTag() + ")";
    }

    private int getJahr() {
        return this.jahr;
    }

    private int getMonat() {
        return this.monat;
    }

    private int getTag() {
        return this.tag;
    }
}
