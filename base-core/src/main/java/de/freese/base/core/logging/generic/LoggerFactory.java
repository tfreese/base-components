// Created: 29.04.2022
package de.freese.base.core.logging.generic;

import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;

/**
 * @author Thomas Freese
 * @see org.jboss.logging.LoggerProviders
 */
public final class LoggerFactory {
    private static final LoggerProvider LOGGER_PROVIDER = find();

    public static Logger createLogger(final Class<?> clazz) {
        return LOGGER_PROVIDER.createLogger(clazz);
    }

    public static Logger createLogger(final String name) {
        return LOGGER_PROVIDER.createLogger(name);
    }

    private static LoggerProvider find() {
        final ClassLoader classLoader = LoggerFactory.class.getClassLoader();

        try {
            // Check the system property.
            final String loggerProvider = System.getProperty("logger.provider");

            if (loggerProvider != null) {
                if ("jul".equalsIgnoreCase(loggerProvider)) {
                    return tryJuL("system property");
                }
                else if ("slf4j".equalsIgnoreCase(loggerProvider)) {
                    return trySlf4j("system property");
                }
            }
        }
        catch (Throwable th) {
            // Ignore
        }

        // Next try for a service provider.
        try {
            final ServiceLoader<LoggerProvider> loader = ServiceLoader.load(LoggerProvider.class, classLoader);

            for (LoggerProvider loggerProvider : loader) {
                if (loggerProvider == null) {
                    continue;
                }

                logProvider(loggerProvider, "service loader");

                return loggerProvider;
            }
        }
        catch (ServiceConfigurationError ignore) {
            // Ignore
        }

        try {
            return trySlf4j(null);
        }
        catch (Exception ex) {
            // Ignore
        }

        return tryJuL(null);
    }

    private static void logProvider(final LoggerProvider provider, final String via) {
        final Logger logger = provider.createLogger(LoggerFactory.class);

        if (via == null) {
            logger.info("Using Logging Provider: %s", provider);
        }
        else {
            logger.info("Using Logging Provider: %s found via %s", provider, via);
        }
    }

    private static JulLoggerProvider tryJuL(final String via) {
        final JulLoggerProvider provider = new JulLoggerProvider();

        logProvider(provider, via);

        return provider;
    }

    private static LoggerProvider trySlf4j(final String via) {
        final LoggerProvider provider = new Slf4JLoggerProvider();

        logProvider(provider, via);

        return provider;
    }

    private LoggerFactory() {
        super();
    }
}
