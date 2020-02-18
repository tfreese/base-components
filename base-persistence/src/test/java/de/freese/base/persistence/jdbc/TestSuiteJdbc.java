/****/
package de.freese.base.persistence.jdbc;

import org.junit.platform.runner.JUnitPlatform;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.SuiteDisplayName;
import org.junit.runner.RunWith;

import java.util.concurrent.atomic.AtomicInteger;

/***
 * @author Thomas Freese
 */
@RunWith(JUnitPlatform.class)
@SuiteDisplayName("TestSuite for JDBC")
@SelectPackages("de.freese.base.persistence.jdbc")
public class TestSuiteJdbc
{
    /**
     *
     */
    public static final AtomicInteger ATOMIC_INTEGER = new AtomicInteger(1);

    /**
     * Erstellt ein neues {@link TestSuiteJdbc} Object.
     */
    public TestSuiteJdbc()
    {
        super();
    }
}
