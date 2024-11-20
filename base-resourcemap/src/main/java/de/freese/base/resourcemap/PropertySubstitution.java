// Created: 20 Nov. 2024
package de.freese.base.resourcemap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.UnaryOperator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Thomas Freese
 */
public final class PropertySubstitution {
    private static final Logger LOGGER = LoggerFactory.getLogger(PropertySubstitution.class);

    /**
     * Replace the placeholders '${...}', System.getProperty and System.getenv variables:
     *
     * <pre>
     *  hello = Hello
     *  world = World
     *  place = ${hello} ${world}
     *  sys.tmpdir = ${java.io.tmpdir}
     *  env.home = ${HOME}
     * </pre>
     */
    public static void replacePlaceHolder(final Map<String, String> map) {
        replacePlaceHolder(map, key -> {
            String value = map.get(key);

            if (value == null) {
                value = System.getProperty(key);
            }

            if (value == null) {
                value = System.getenv(key);
            }

            return value;
        });
    }

    public static void replacePlaceHolder(final Map<String, String> map, final UnaryOperator<String> placeHolderToValue) {
        final Map<String, String> missing = new HashMap<>();

        map.replaceAll((key, value) -> {
            if (!value.contains("${")) {
                return value;
            }

            String newValue = value;

            newValue = replacePlaceHolder(newValue, placeHolderToValue);

            if (!newValue.contains("${")) {
                return newValue;
            }

            // PlaceHolder in PlaceHolder.
            newValue = replacePlaceHolder(newValue, placeHolderToValue);

            if (newValue.contains("${")) {
                // PlaceHolder in PlaceHolder in PlaceHolder.
                newValue = replacePlaceHolder(newValue, placeHolderToValue);
            }

            if (newValue.contains("${")) {
                // Log it.
                missing.put(key, newValue);
            }

            return newValue;
        });

        missing.forEach((key, value) -> LOGGER.warn("no value found for placeholder: {} = {}", key, value));
    }

    public static String replacePlaceHolder(final String value, final UnaryOperator<String> placeHolderToValue) {
        final List<String> placeHolders = getPlaceHolder(value);

        String newValue = value;

        for (String placeHolder : placeHolders) {
            final String placeHolderValue = placeHolderToValue.apply(placeHolder);

            if (placeHolderValue != null) {
                newValue = newValue.replace("${" + placeHolder + "}", placeHolderValue);
            }
        }

        return newValue;
    }

    private static List<String> getPlaceHolder(final String value) {
        final List<String> placeHolders = new ArrayList<>();

        int startIndex = 0;
        int lastEndIndex = 0;

        while ((startIndex = value.indexOf("${", lastEndIndex)) != -1) {
            final int endIndex = value.indexOf('}', startIndex);

            if (endIndex != -1) {
                final String placeHolder = value.substring(startIndex + 2, endIndex);
                placeHolders.add(placeHolder);

                lastEndIndex = endIndex;
            }
        }

        return placeHolders;
    }

    private PropertySubstitution() {
        super();
    }
}
