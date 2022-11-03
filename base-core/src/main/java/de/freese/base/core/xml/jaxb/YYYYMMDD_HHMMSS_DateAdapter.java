package de.freese.base.core.xml.jaxb;

import java.util.Date;

/**
 * JAXB Adapter für ein {@link Date} im "yyyy-MM-dd HH:mm:ss" Format.
 *
 * @author Thomas Freese
 */
public class YYYYMMDD_HHMMSS_DateAdapter extends DateAdapter
{
    public YYYYMMDD_HHMMSS_DateAdapter()
    {
        super("yyyy-MM-dd HH:mm:ss");
    }
}
