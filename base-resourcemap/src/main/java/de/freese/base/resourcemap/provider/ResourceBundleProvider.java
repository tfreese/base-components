package de.freese.base.resourcemap.provider;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * {@link ResourceProvider} for a {@link ResourceBundle}.
 *
 * @author Thomas Freese
 */
public final class ResourceBundleProvider implements ResourceProvider {
    @Override
    public Map<String, String> getResources(final String bundleName, final Locale locale) {
        final Map<String, String> bundles = new HashMap<>();

        try {
            final ResourceBundle resourceBundle = ResourceBundle.getBundle(bundleName, locale);

            for (String key : resourceBundle.keySet()) {
                final String value = resourceBundle.getString(key);

                bundles.put(key, value);
            }

            //            final Enumeration<String> keys = resourceBundle.getKeys();
            //
            //            while (keys.hasMoreElements())
            //            {
            //                final String key = keys.nextElement();
            //                final String value = resourceBundle.getString(key);
            //
            //                bundles.put(key, value);
            //            }
        }
        catch (RuntimeException ex) {
            throw ex;
        }
        catch (Exception ex) {
            throw new RuntimeException(ex);
        }

        return bundles;
    }
}
