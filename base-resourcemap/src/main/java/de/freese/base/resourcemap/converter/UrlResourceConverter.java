package de.freese.base.resourcemap.converter;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * @author Thomas Freese
 */
public class UrlResourceConverter extends AbstractResourceConverter<URL> {
    /**
     * @see de.freese.base.resourcemap.converter.ResourceConverter#convert(java.lang.String, java.lang.String)
     */
    @Override
    public URL convert(final String key, final String value) {
        try {
            return new URI(value).toURL();
        }
        catch (URISyntaxException | MalformedURLException ex) {
            throwException(key, value, "Invalid URL");
        }

        return null;
    }
}
