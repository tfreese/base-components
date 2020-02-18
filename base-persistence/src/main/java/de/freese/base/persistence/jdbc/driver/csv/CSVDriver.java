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
import org.apache.commons.lang3.StringUtils;

/**
 * JDBC-Driver für CSV-Dateien.<br>
 * Beispiel: DriverManager.getConnection("jdbc:csv:PATH/FILENAME.csv;STRUKTUR;LAYOUT");<br>
 * Als Backend wird eine Text-Table von HSQLDB verwendet.
 *
 * @see HsqldbTextTableBuilder
 * @author Thomas Freese
 */
public class CSVDriver implements java.sql.Driver
{
    /**
     *
     */
    private static final Driver INSTANCE = new CSVDriver();

    /**
     *
     */
    private static volatile boolean registered = false;

    /**
     *
     */
    public static final String URL_PREFIX = "jdbc:csv";

    /**
     *
     */
    static
    {
        if (!registered)
        {
            registered = true;

            try
            {
                // HSQLDB registrieren.
                Class.forName("org.hsqldb.jdbc.JDBCDriver");

                // Den CSVDriver registrieren.
                DriverManager.registerDriver(INSTANCE);
            }
            catch (Exception ex)
            {
                @SuppressWarnings("resource")
                PrintWriter writer = DriverManager.getLogWriter();

                if (writer != null)
                {
                    ex.printStackTrace(writer);
                }
                else
                {
                    ex.printStackTrace(System.err);
                }
            }
        }
    }

    /**
     * Erzeugt eine neue Instanz von {@link Driver}
     */
    private CSVDriver()
    {
        super();
    }

    /**
     * @see java.sql.Driver#acceptsURL(java.lang.String)
     */
    @Override
    public boolean acceptsURL(final String url) throws SQLException
    {
        if (StringUtils.isBlank(url))
        {
            return false;
        }

        return url.startsWith(URL_PREFIX);
    }

    /**
     * @see java.sql.Driver#connect(java.lang.String, java.util.Properties)
     */
    @Override
    public Connection connect(final String url, final Properties info) throws SQLException
    {
        if (!acceptsURL(url))
        {
            return null;
        }

        String[] files = StringUtils.substringsBetween(url, "[", "]");
        List<HsqldbTextTableBuilder> builderList = new ArrayList<>();

        for (String file : files)
        {
            HsqldbTextTableBuilder ttb = HsqldbTextTableBuilder.create();
            builderList.add(ttb);

            // URL aufspalten in DB und Properties.
            String[] splits = file.split(";");

            String fileName = splits[0];

            if (fileName.startsWith(URL_PREFIX))
            {
                fileName = fileName.substring(URL_PREFIX.length());
            }

            Path path = Paths.get(fileName);
            ttb.setPath(path);

            // CSV-Struktur
            if (splits.length >= 2)
            {
                String[] columns = splits[1].split(",");

                for (String column : columns)
                {
                    ttb.addColumn(column);
                }
            }

            // CSV-Layout
            if (splits.length >= 3)
            {
                String[] layout = splits[2].split(",");

                for (String pair : layout)
                {
                    String[] keyValue = pair.split("=");

                    switch (keyValue[0])
                    {
                        case "ignore_first":
                            ttb.setIgnoreFirst(Boolean.parseBoolean(keyValue[1]));
                            break;

                        case "fs":
                            ttb.setFieldSeparator(keyValue[1]);
                            break;

                        case "all_quoted":
                            ttb.setAllQuoted(Boolean.parseBoolean(keyValue[1]));
                            break;

                        case "encoding":
                            ttb.setEncoding(Charset.forName(keyValue[1]));
                            break;

                        case "cache_rows":
                            ttb.setCacheRows(Integer.parseInt(keyValue[1]));
                            break;

                        case "cache_size":
                            ttb.setCacheSize(Integer.parseInt(keyValue[1]));
                            break;

                        case "tableName":
                            ttb.setTableName(keyValue[1]);
                            break;

                        default:
                            break;
                    }
                }
            }
        }

        HsqldbTextTableBuilder firstBuilder = builderList.remove(0);
        HsqldbTextTableBuilder[] builders = new HsqldbTextTableBuilder[0];

        if (!builderList.isEmpty())
        {
            builders = builderList.toArray(builders);
        }

        return firstBuilder.build(builders);
    }

    /**
     * @see java.sql.Driver#getMajorVersion()
     */
    @Override
    public int getMajorVersion()
    {
        return 1;
    }

    /**
     * @see java.sql.Driver#getMinorVersion()
     */
    @Override
    public int getMinorVersion()
    {
        return 0;
    }

    /**
     * @see java.sql.Driver#getParentLogger()
     */
    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException
    {
        return null;
    }

    /**
     * @see java.sql.Driver#getPropertyInfo(java.lang.String, java.util.Properties)
     */
    @Override
    public DriverPropertyInfo[] getPropertyInfo(final String url, final Properties info) throws SQLException
    {
        return new DriverPropertyInfo[0];
    }

    /**
     * @see java.sql.Driver#jdbcCompliant()
     */
    @Override
    public boolean jdbcCompliant()
    {
        return false;
    }
}
