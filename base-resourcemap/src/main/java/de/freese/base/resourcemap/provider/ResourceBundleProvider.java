package de.freese.base.resourcemap.provider;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link ResourceProvider} for a {@link ResourceBundle}.
 *
 * @author Thomas Freese
 */
public final class ResourceBundleProvider implements ResourceProvider
{
    private static final Logger LOGGER = LoggerFactory.getLogger(ResourceBundleProvider.class);

    /**
     * @see de.freese.base.resourcemap.provider.ResourceProvider#getResources(java.lang.String, java.util.Locale)
     */
    @Override
    public Map<String, String> getResources(final String bundleName, final Locale locale)
    {
        Map<String, String> bundles = new HashMap<>();

        try
        {
            ResourceBundle resourceBundle = ResourceBundle.getBundle(bundleName, locale);

            for (String key : resourceBundle.keySet())
            {
                String value = resourceBundle.getString(key);

                bundles.put(key, value);
            }

            //            Enumeration<String> keys = resourceBundle.getKeys();
            //
            //            while (keys.hasMoreElements())
            //            {
            //                String key = keys.nextElement();
            //                String value = resourceBundle.getString(key);
            //
            //                bundles.put(key, value);
            //            }
        }
        catch (Exception ex)
        {
            LOGGER.error(ex.getMessage(), ex);
        }

        return bundles;
    }
}
