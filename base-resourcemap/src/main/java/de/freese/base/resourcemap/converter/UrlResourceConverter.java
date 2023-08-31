package de.freese.base.resourcemap.converter;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * @author Thomas Freese
 */
public class UrlResourceConverter extends AbstractResourceConverter<URL> {
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
