// Created: 08.09.2016
package de.freese.base.persistence.jdbc.driver.logging;

import java.sql.DriverManager;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;

/**
 * @author Thomas Freese
 */
public class LoggingJdbcDriverServletContextListener implements ServletContextListener {

    @Override
    public void contextDestroyed(final ServletContextEvent sce) {
        // Empty
    }

    @Override
    public void contextInitialized(final ServletContextEvent sce) {
        try {
            DriverManager.registerDriver(new LoggingJdbcDriver());

            LoggingJdbcDriver.addDefaultLogMethods();
        }
        catch (RuntimeException ex) {
            throw ex;
        }
        catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
