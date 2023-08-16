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
import java.util.Objects;
import java.util.Scanner;
import java.util.stream.Collectors;
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
public class DatabasePopulator {
    public static final Logger LOGGER = LoggerFactory.getLogger(DatabasePopulator.class);

    private final List<URL> scriptUrls = new ArrayList<>();

    public void addScript(final URL scriptUrl) {
        this.scriptUrls.add(scriptUrl);
    }

    public void populate(final Connection connection) throws Exception {
        for (URL scriptUrl : this.scriptUrls) {
            List<String> sqls = parseSQLs(scriptUrl);

            // sqls.forEach(System.out::println);
            try (Statement statement = connection.createStatement()) {
                for (String sql : sqls) {
                    if (LOGGER.isDebugEnabled()) {
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

    public void populate(final DataSource dataSource) throws Exception {
        try (Connection connection = dataSource.getConnection()) {
            populate(connection);
        }
    }

    protected List<String> parseSQLs(final URL scriptUrl) throws Exception {
        String sqlScript = parseScript(scriptUrl);

        List<String> sqls = new ArrayList<>();

        // SQLs ending with ';'.
        try (Scanner scanner = new Scanner(sqlScript)) {
            scanner.useDelimiter(";");

            while (scanner.hasNext()) {
                String sql = scanner.next().strip();
                sqls.add(sql);
            }
        }

        return sqls;
    }

    protected String parseScript(final URL scriptUrl) throws Exception {
        List<String> fileLines = null;

        if (scriptUrl != null) {
            // Funktioniert nicht, wenn die Skripte in einem anderen Archiv liegen.
            Path path = Paths.get(scriptUrl.toURI());

            try (Stream<String> lines = Files.lines(path)) {
                fileLines = lines.toList();
            }
            catch (Exception ex) {
                // Ignore
            }
        }

        if ((fileLines == null) && (scriptUrl != null)) {
            // InputStream inputStream = getClass().getClassLoader().getResourceAsStream(script);
            try (InputStream inputStream = scriptUrl.openStream();
                 InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
                 BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
                fileLines = bufferedReader.lines().toList();
            }
        }

        if (fileLines == null) {
            throw new IllegalStateException("no SQLs found");
        }

        // @formatter:off
        return fileLines.stream()
                .filter(Objects::nonNull)
                .filter(l -> !l.isEmpty())
                .filter(l -> !l.startsWith("--"))
                .filter(l -> !l.startsWith("#"))
                .map(String::strip)
                .filter(l -> !l.isEmpty())
                .collect(Collectors.joining(" "))
                ;
        // @formatter:on
    }
}
