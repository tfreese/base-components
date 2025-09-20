// Created: 20 Nov. 2024
package de.freese.base.resourcemap;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Thomas Freese
 */
public final class PropertySubstitutionDemo {
    private static final Logger LOGGER = LoggerFactory.getLogger(PropertySubstitutionDemo.class);

    static void main() {
        final Map<String, String> map = new HashMap<>(Map.of(
                "one", "1",
                "two", "2",
                "four", "${three} + 1",
                "three", "${one} + ${two}",
                "sys.tmpdir", "${java.io.tmpdir}",
                "env.home", "${HOME}",
                "test", "${missing}"
        ));

        PropertySubstitution.replacePlaceHolder(map);

        map.forEach((key, value) -> LOGGER.info("{} = {}", key, value));
    }

    private PropertySubstitutionDemo() {
        super();
    }
}
