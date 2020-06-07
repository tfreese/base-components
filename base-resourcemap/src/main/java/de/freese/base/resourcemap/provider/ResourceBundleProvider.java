package de.freese.base.resourcemap.provider;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link ResourceProvider} auf Basis eines {@link ResourceBundle}.
 *
 * @author Thomas Freese
 */
public final class ResourceBundleProvider implements ResourceProvider
{
    /**
     *
     */
    private final static Logger LOGGER = LoggerFactory.getLogger(ResourceBundleProvider.class);

    /**
     * Erstellt ein neues {@link ResourceBundleProvider} Object.
     */
    public ResourceBundleProvider()
    {
        super();
    }

    /**
     * @see de.freese.base.resourcemap.provider.ResourceProvider#getResources(java.lang.String, java.util.Locale, java.lang.ClassLoader)
     */
    @Override
    public Map<String, String> getResources(final String baseName, final Locale locale, final ClassLoader classLoader)
    {
        Map<String, String> bundles = new HashMap<>();

        try
        {
            ResourceBundle resourceBundle = ResourceBundle.getBundle(baseName, locale, classLoader);

            Enumeration<String> keys = resourceBundle.getKeys();

            while (keys.hasMoreElements())
            {
                String key = keys.nextElement();
                String value = resourceBundle.getString(key);

                bundles.put(key, value);
            }
        }
        catch (Exception ex)
        {
            LOGGER.error(null, ex);
        }

        return bundles;
    }
}
