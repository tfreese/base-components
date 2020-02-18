// Created: 04.12.2017
package de.freese.base.persistence.jdbc;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

/**
 * Geklaut von org.springframework.data.jdbc.support.DatabaseType
 *
 * @author Thomas Freese
 */
public enum DatabaseType
{
    /**
     *
     */
    DB2("DB2"),
    /**
     *
     */
    DB2ZOS("DB2ZOS"),
    /**
     *
     */
    DERBY("Apache Derby"),
    /**
     *
     */
    H2("H2"),
    /**
     *
     */
    HSQL("HSQL Database Engine"),
    /**
     *
     */
    MYSQL("MySQL"),
    /**
     *
     */
    ORACLE("Oracle"),
    /**
     *
     */
    POSTGRES("PostgreSQL"),
    /**
     *
     */
    SQLSERVER("Microsoft SQL Server"),
    /**
     *
     */
    SYBASE("Sybase");

    /**
     *
     */
    private static final Map<String, DatabaseType> cache;

    static
    {
        cache = new HashMap<>();

        for (DatabaseType type : values())
        {
            cache.put(type.getProductName(), type);
        }
    }

    /**
     * Convenience method that pulls a database product name from the DataSource's metadata.
     *
     * @param dataSource {@link DataSource}
     *
     * @return {@link DatabaseType}
     *
     * @throws SQLException Falls was schief geht.
     */
    public static DatabaseType fromMetaData(final DataSource dataSource) throws SQLException
    {
        String databaseProductName = JdbcUtils.getDatabaseProductName(dataSource);

        if ("DB2".equals(databaseProductName))
        {
            String databaseProductVersion = JdbcUtils.getDatabaseProductVersion(dataSource);

            if (!databaseProductVersion.startsWith("SQL"))
            {
                databaseProductName = "DB2ZOS";
            }
            else
            {
                databaseProductName = commonDatabaseName(databaseProductName);
            }
        }
        else
        {
            databaseProductName = commonDatabaseName(databaseProductName);
        }

        return fromProductName(databaseProductName);
    }

    /**
     * @param productName String
     *
     * @return {@link DatabaseType}
     */
    public static DatabaseType fromProductName(final String productName)
    {
        if (!cache.containsKey(productName))
        {
            throw new IllegalArgumentException("DatabaseType not found for product name: [" + productName + "]");
        }

        return cache.get(productName);
    }

    /**
     * @param source String
     *
     * @return String
     */
    private static String commonDatabaseName(final String source)
    {
        String name = source;

        if ((source != null) && source.startsWith("DB2"))
        {
            name = "DB2";
        }
        else if ("Sybase SQL Server".equals(source) || "Adaptive Server Enterprise".equals(source) || "ASE".equals(source)
                || "sql server".equalsIgnoreCase(source))
        {
            name = "Sybase";
        }

        return name;
    }

    /**
     *
     */
    private final String productName;

    /**
     * Erzeugt eine neue Instanz von {@link DatabaseType}.
     *
     * @param productName String
     */
    private DatabaseType(final String productName)
    {
        this.productName = productName;
    }

    /**
     * @return String
     */
    public String getProductName()
    {
        return this.productName;
    }
}
