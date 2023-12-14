package de.freese.base.resourcemap.scanner;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

/**
 * ResourceScanner für Property-Dateien mit der Spring-Implementierung.
 *
 * @author Thomas Freese
 */
public class SpringResourceScanner implements ResourceScanner {
    private static final Logger LOGGER = LoggerFactory.getLogger(SpringResourceScanner.class);

    private final ResourcePatternResolver resourcePatternResolver;

    public SpringResourceScanner() {
        super();

        this.resourcePatternResolver = new PathMatchingResourcePatternResolver();
    }

    @Override
    public Set<String> scanResources(final String basePath) {
        String path = basePath;

        if (path.endsWith("/")) {
            path = path.substring(0, path.lastIndexOf('/'));
        }

        try {
            final Resource[] resources = this.resourcePatternResolver.getResources("classpath*:" + path + "/*.properties");

            // Für ResourceBundle normalisieren
            final Set<String> bundleNames = new HashSet<>();

            for (Resource resource : resources) {
                bundleNames.add(path + "/" + resource.getFilename());
            }

            return bundleNames;
        }
        catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
        }

        return Collections.emptySet();
    }
}
