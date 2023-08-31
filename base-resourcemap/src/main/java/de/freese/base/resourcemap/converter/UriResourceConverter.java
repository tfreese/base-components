package de.freese.base.resourcemap.converter;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * @author Thomas Freese
 */
public class UriResourceConverter extends AbstractResourceConverter<URI> {
    @Override
    public URI convert(final String key, final String value) {
        try {
            return new URI(value);
        }
        catch (URISyntaxException ex) {
            throwException(key, value, "Invalid URI");
        }

        return null;
    }
}
