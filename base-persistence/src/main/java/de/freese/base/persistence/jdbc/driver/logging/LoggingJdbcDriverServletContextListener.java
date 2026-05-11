// Created: 08.09.2016
package de.freese.base.persistence.jdbc.driver.logging;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;

import java.sql.DriverManager;

/**
 * @author Thomas Freese
 */
public class LoggingJdbcDriverServletContextListener implements ServletContextListener {
    @Override
    public void contextDestroyed(final ServletContextEvent event) {
        // Empty
    }

    @Override
    public void contextInitialized(final ServletContextEvent event) {
        try {
            DriverManager.registerDriver(new LoggingJdbcDriver());

            LoggingJdbcDriver.addDefaultLogMethods();
        } catch (final RuntimeException ex) {
            throw ex;
        } catch (final Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
