// Created: 08.09.2016
package de.freese.base.persistence.jdbc.driver.csv;

import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * JDBC-Driver for CSV-Files.<br>
 * Example: DriverManager.getConnection("jdbc:csv:PATH/FILENAME.csv;STRUCTURE;LAYOUT");<br>
 * For Backend a HSQLDB Text-Table is used.
 *
 * @author Thomas Freese
 */
public final class CsvDriver implements java.sql.Driver {
    public static final String URL_PREFIX = "jdbc:csv";

    private static final Driver INSTANCE = new CsvDriver();

    private static volatile boolean registered;

    static {
        if (!registered) {
            registered = true;

            try {
                // Register HSQLDB, not necessary for JDBC 4.0.
                // Class.forName("org.hsqldb.jdbc.JDBCDriver");

                // Den CsvDriver registrieren.
                DriverManager.registerDriver(INSTANCE);
            }
            catch (Exception ex) {
                PrintWriter writer = DriverManager.getLogWriter();

                if (writer != null) {
                    ex.printStackTrace(writer);
                }
                else {
                    ex.printStackTrace(System.err);
                }
            }
        }
    }

    private CsvDriver() {
        super();
    }

    @Override
    public boolean acceptsURL(final String url) throws SQLException {
        if ((url == null) || url.isBlank()) {
            return false;
        }

        return url.startsWith(URL_PREFIX);
    }

    @Override
    public Connection connect(final String url, final Properties info) throws SQLException {
        if (!acceptsURL(url)) {
            return null;
        }

        String[] files = substringsBetween(url, "[", "]");

        List<HsqldbTextTableBuilder> builderList = new ArrayList<>();

        for (String file : files) {
            HsqldbTextTableBuilder ttb = HsqldbTextTableBuilder.create();
            builderList.add(ttb);

            // Split URL in DB and Properties.
            String[] splits = file.split(";");

            String fileName = splits[0];

            if (fileName.startsWith(URL_PREFIX)) {
                fileName = fileName.substring(URL_PREFIX.length());
            }

            Path path = Paths.get(fileName);
            ttb.setPath(path);

            // CSV-Structure
            if (splits.length >= 2) {
                String[] columns = splits[1].split(",");

                for (String column : columns) {
                    ttb.addColumn(column);
                }
            }

            // CSV-Layout
            if (splits.length >= 3) {
                String[] layout = splits[2].split(",");

                for (String pair : layout) {
                    String[] keyValue = pair.split("=");

                    switch (keyValue[0]) {
                        case "ignore_first" -> ttb.setIgnoreFirst(Boolean.parseBoolean(keyValue[1]));
                        case "fs" -> ttb.setFieldSeparator(keyValue[1]);
                        case "all_quoted" -> ttb.setAllQuoted(Boolean.parseBoolean(keyValue[1]));
                        case "encoding" -> ttb.setEncoding(Charset.forName(keyValue[1]));
                        case "cache_rows" -> ttb.setCacheRows(Integer.parseInt(keyValue[1]));
                        case "cache_size" -> ttb.setCacheSize(Integer.parseInt(keyValue[1]));
                        case "tableName" -> ttb.setTableName(keyValue[1]);
                        default -> {
                            // Empty
                        }
                    }
                }
            }
        }

        HsqldbTextTableBuilder firstBuilder = builderList.remove(0);
        HsqldbTextTableBuilder[] builders = {};

        if (!builderList.isEmpty()) {
            builders = builderList.toArray(builders);
        }

        return firstBuilder.build(builders);
    }

    @Override
    public int getMajorVersion() {
        return 1;
    }

    @Override
    public int getMinorVersion() {
        return 0;
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return null;
    }

    @Override
    public DriverPropertyInfo[] getPropertyInfo(final String url, final Properties info) throws SQLException {
        return new DriverPropertyInfo[0];
    }

    @Override
    public boolean jdbcCompliant() {
        return false;
    }

    private String[] substringsBetween(final String value, final String open, final String close) {
        // return StringUtils.substringsBetween(value, "[", "]");

        final int strLen = value.length();

        if (strLen == 0) {
            return new String[0];
        }

        final int closeLen = close.length();
        final int openLen = open.length();
        final List<String> list = new ArrayList<>();
        int pos = 0;

        while (pos < (strLen - closeLen)) {
            int start = value.indexOf(open, pos);

            if (start < 0) {
                break;
            }

            start += openLen;

            final int end = value.indexOf(close, start);

            if (end < 0) {
                break;
            }

            list.add(value.substring(start, end));
            pos = end + closeLen;
        }

        if (list.isEmpty()) {
            return null;
        }

        return list.toArray(new String[0]);
    }
}
