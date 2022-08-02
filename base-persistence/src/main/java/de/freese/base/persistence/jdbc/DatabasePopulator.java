// Created: 30.11.2016
package de.freese.base.persistence.jdbc;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Erstellt die DB-Struktur anhand der definierten SQL-Skripte.<br>
 * Ein SQL muss immer mit einem ';' abgeschlossen sein.
 *
 * @author Thomas Freese
 */
public class DatabasePopulator
{
    /**
     *
     */
    public static final Logger LOGGER = LoggerFactory.getLogger(DatabasePopulator.class);
    /**
     *
     */
    private final List<URL> scripts = new ArrayList<>();

    /**
     * Fügt ein SQL-Skript hinzu,
     *
     * @param script {@link URL}
     */
    public void addScript(final URL script)
    {
        this.scripts.add(script);
    }

    /**
     * Erstellt die DB-Struktur anhand der definierten SQL-Skripte.
     *
     * @param connection {@link Connection}
     *
     * @throws Exception Falls was schiefgeht.
     */
    public void populate(final Connection connection) throws Exception
    {
        for (URL script : this.scripts)
        {
            List<String> sqls = getScriptSQLs(script);

            // sqls.forEach(System.out::println);
            try (Statement statement = connection.createStatement())
            {
                for (String sql : sqls)
                {
                    if (LOGGER.isDebugEnabled())
                    {
                        LOGGER.debug(sql);
                    }

                    statement.execute(sql);

                    // int rowsAffected = statement.getUpdateCount();
                    //
                    // LOGGER.info("{}: Rows affected = {}", sql, rowsAffected);
                }
            }
        }
    }

    /**
     * Erstellt die DB-Struktur anhand der definierten SQL-Skripte.
     *
     * @param dataSource {@link DataSource}
     *
     * @throws Exception Falls was schiefgeht.
     */
    public void populate(final DataSource dataSource) throws Exception
    {
        try (Connection connection = dataSource.getConnection())
        {
            populate(connection);
        }
    }

    /**
     * Liefert die Zeilen aus dem SQL-Skript.
     *
     * @param script String
     *
     * @return {@link List}
     *
     * @throws Exception Falls was schiefgeht.
     */
    protected List<String> getScriptLines(final URL script) throws Exception
    {
        List<String> fileLines = null;

        if (script != null)
        {
            // Funktioniert nicht, wenn die Skripte in einem anderen Archiv liegen.
            Path path = Paths.get(script.toURI());

            try (Stream<String> lines = Files.lines(path))
            {
                fileLines = lines.toList();
            }
            catch (Exception ex)
            {
                // Ignore
            }
        }

        if ((fileLines == null) && (script != null))
        {
            // InputStream inputStream = getClass().getClassLoader().getResourceAsStream(script);
            try (InputStream inputStream = script.openStream();
                 InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
                 BufferedReader bufferedReader = new BufferedReader(inputStreamReader))
            {
                fileLines = bufferedReader.lines().toList();
            }
        }

        if (fileLines == null)
        {
            return new ArrayList<>();
        }

        // @formatter:off
        List<String> scriptLines = fileLines.stream()
                .map(String::strip)
                .filter(l -> !l.isEmpty())
                .filter(l -> !l.startsWith("--"))
                .filter(l -> !l.startsWith("#"))
                .map(l -> l.replace("\n", " ").replace("\r", " "))
                .map(String::strip)
                .toList()
                ;
        // @formatter:on

        return scriptLines;
    }

    /**
     * Liefert die SQLs aus dem Skript.
     *
     * @param script {@link URL}
     *
     * @return {@link List}
     *
     * @throws Exception Falls was schiefgeht.
     */
    protected List<String> getScriptSQLs(final URL script) throws Exception
    {
        List<String> scriptLines = getScriptLines(script);

        List<String> sqls = new ArrayList<>();
        sqls.add(scriptLines.get(0));

        // SQLs sind immer mit einem ';' abgeschlossen.
        for (int i = 1; i < scriptLines.size(); i++)
        {
            String prevSql = sqls.get(sqls.size() - 1);
            String line = scriptLines.get(i);

            if (!prevSql.endsWith(";"))
            {
                sqls.set(sqls.size() - 1, prevSql + " " + line);
            }
            else
            {
                sqls.add(line);
            }
        }

        return sqls;
    }
}
