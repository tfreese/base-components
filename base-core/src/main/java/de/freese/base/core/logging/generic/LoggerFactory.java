// Created: 29.04.2022
package de.freese.base.core.logging.generic;

/**
 * @author Thomas Freese
 */
public final class LoggerFactory
{
    /**
     *
     */
    private static LoggerFactoryDelegate loggerFactoryDelegate;

    static
    {
        String className = null;

        try
        {
            Class.forName("org.slf4j.impl.StaticLoggerBinder");
            className = "de.freese.base.core.logging.Slf4JLoggerFactoryDelegate";
        }
        catch (Throwable ex)
        {
            System.err.println("No org.slf4j.impl.StaticLoggerBinder found in ClassPath, trying with log4j2...");
        }

        if (className == null)
        {
            try
            {
                Class.forName("org.apache.logging.log4j.Logger");
                className = "de.freese.base.core.logging.Log4j2LoggerFactoryDelegate";
            }
            catch (Throwable ex)
            {
                System.err.println("No org.apache.logging.log4j.Logger found in ClassPath, trying with log4j...");
            }
        }

        if (className == null)
        {
            try
            {
                Class.forName("org.apache.log4j.Logger");
                className = "de.freese.base.core.logging.Log4JLoggerFactoryDelegate";
            }
            catch (Throwable ex)
            {
                System.err.println("No org.apache.log4j.Logger found in ClassPath, falling back default...");
            }
        }

        try
        {
            if (className != null)
            {
                Class<?> loggerClass = Class.forName(className.strip(), true, LoggerFactory.class.getClassLoader());
                loggerFactoryDelegate = (LoggerFactoryDelegate) loggerClass.getDeclaredConstructor().newInstance();
            }
            else
            {
                fallbackToDefault();
            }
        }
        catch (Throwable ex)
        {
            fallbackToDefault();
        }

        Logger logger = createLogger(LoggerFactory.class);
        logger.debug("Using %s for logging.", loggerFactoryDelegate);
    }

    /**
     * @param clazz Class
     *
     * @return Logger
     */
    public static Logger createLogger(Class<?> clazz)
    {
        return loggerFactoryDelegate.createLogger(clazz);
    }

    /**
     * Java Util Logging
     */
    private static void fallbackToDefault()
    {
        loggerFactoryDelegate = new JulLoggerFactoryDelegate();
    }

    /**
     *
     */
    private LoggerFactory()
    {
        super();
    }
}
