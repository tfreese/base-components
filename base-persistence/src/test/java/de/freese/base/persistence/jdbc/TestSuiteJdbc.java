/****/
package de.freese.base.persistence.jdbc;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * TestSuits laufen nur auf Basis JUnit4.
 * 
 * @author Thomas Freese
 */
// @RunWith(JUnitPlatform.class) // Basis JUnit4
// @SuiteDisplayName("TestSuite for JDBC")
// @SelectPackages("de.freese.base.persistence.jdbc")
public class TestSuiteJdbc
{
    /**
     *
     */
    public static final AtomicInteger ATOMIC_INTEGER = new AtomicInteger(1);
}
