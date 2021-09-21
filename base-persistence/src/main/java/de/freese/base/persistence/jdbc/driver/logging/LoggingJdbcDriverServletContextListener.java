// Created: 08.09.2016
package de.freese.base.persistence.jdbc.driver.logging;

import java.sql.DriverManager;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * @author Thomas Freese
 */
public class LoggingJdbcDriverServletContextListener implements ServletContextListener
{
    /**
     * Erstellt ein neues {@link LoggingJdbcDriverServletContextListener} Object.
     */
    public LoggingJdbcDriverServletContextListener()
    {
        super();
    }

    /**
     * @see javax.servlet.ServletContextListener#contextDestroyed(javax.servlet.ServletContextEvent)
     */
    @Override
    public void contextDestroyed(final ServletContextEvent sce)
    {
        // Empty
    }

    /**
     * @see javax.servlet.ServletContextListener#contextInitialized(javax.servlet.ServletContextEvent)
     */
    @Override
    public void contextInitialized(final ServletContextEvent sce)
    {
        try
        {
            DriverManager.registerDriver(new LoggingJdbcDriver());

            LoggingJdbcDriver.addDefaultLogMethods();
        }
        catch (RuntimeException ex)
        {
            throw ex;
        }
        catch (Exception ex)
        {
            throw new RuntimeException(ex);
        }
    }
}
