// Created: 04.12.2017
package de.freese.base.persistence.jdbc;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import de.freese.base.utils.JdbcUtils;

/**
 * Inspired by org.springframework.boot.jdbc.DatabaseDriver
 *
 * @author Thomas Freese
 */
public enum DatabaseType {
    DB2("DB2"),
    DB2ZOS("DB2ZOS"),
    DERBY("Apache Derby"),
    H2("H2"),
    HSQL("HSQL Database Engine"),
    MYSQL("MySQL"),
    ORACLE("Oracle"),
    POSTGRES("PostgreSQL"),
    SQLSERVER("Microsoft SQL Server"),
    SYBASE("Sybase");

    private static final Map<String, DatabaseType> cache;

    static {
        cache = new HashMap<>();

        for (DatabaseType type : values()) {
            cache.put(type.getProductName(), type);
        }
    }

    public static DatabaseType fromMetaData(final DataSource dataSource) throws SQLException {
        String databaseProductName = JdbcUtils.getDatabaseProductName(dataSource);

        if ("DB2".equals(databaseProductName)) {
            String databaseProductVersion = JdbcUtils.getDatabaseProductVersion(dataSource);

            if (!databaseProductVersion.startsWith("SQL")) {
                databaseProductName = "DB2ZOS";
            }
            else {
                databaseProductName = commonDatabaseName(databaseProductName);
            }
        }
        else {
            databaseProductName = commonDatabaseName(databaseProductName);
        }

        return fromProductName(databaseProductName);
    }

    public static DatabaseType fromProductName(final String productName) {
        if (!cache.containsKey(productName)) {
            throw new IllegalArgumentException("DatabaseType not found for product name: [" + productName + "]");
        }

        return cache.get(productName);
    }

    private static String commonDatabaseName(final String source) {
        String name = source;

        if ((source != null) && source.startsWith("DB2")) {
            name = "DB2";
        }
        else if ("Sybase SQL Server".equals(source) || "Adaptive Server Enterprise".equals(source) || "ASE".equals(source) || "sql server".equalsIgnoreCase(source)) {
            name = "Sybase";
        }

        return name;
    }

    private final String productName;

    DatabaseType(final String productName) {
        this.productName = productName;
    }

    public String getProductName() {
        return this.productName;
    }
}
