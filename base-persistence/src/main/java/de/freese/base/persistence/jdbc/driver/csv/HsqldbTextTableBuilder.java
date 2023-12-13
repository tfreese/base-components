// Created: 12.09.2016
package de.freese.base.persistence.jdbc.driver.csv;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/**
 * Builder für eine In-Memory HSQLDB mit Text-Tables mit CSV-Dateien als Backend.<br>
 * <br>
 * Defaults:
 * <ul>
 * <li>encoding = UTF-8</li>
 * <li>ignoreFirst = true</li>
 * <li>fieldSeparator = \semi = ';'</li>
 * <li>allQuoted = true</li>
 * <li>cacheRows = 10000</li>
 * <li>cacheSize = 1024 KB = 1 MB</li>
 * </ul>
 * Weitere Informationen unter:<br>
 * <a href="http://hsqldb.org/doc/guide/texttables-chapt.html">texttables</a><br>
 * <a href="http://hsqldb.org/doc/guide/ch06.html">ch06</a><br>
 *
 * @author Thomas Freese
 */
public final class HsqldbTextTableBuilder {
    public static HsqldbTextTableBuilder create() {
        return new HsqldbTextTableBuilder();
    }

    private static void validatePath(final Path path) {
        Objects.requireNonNull(path, "path required");

        if (!Files.exists(path)) {
            throw new IllegalArgumentException("file not exist");
        }

        if (!Files.isReadable(path)) {
            throw new IllegalArgumentException("file not readable");
        }
    }

    private final List<String> columns = new ArrayList<>();

    private boolean allQuoted = true;

    private int cacheRows = 10_000;
    /**
     * [kB]; 1024 KB = 1 MB
     */
    private int cacheSize = 1024;

    private Charset charset = StandardCharsets.UTF_8;

    private String fieldSeparator = "\\semi";

    private boolean ignoreFirst = true;

    private Path path;

    private String tableName;

    private HsqldbTextTableBuilder() {
        super();
    }

    /**
     * Definiert die Struktur der Text-Tabelle.<br>
     * Diese muss mit der Struktur der CSV-Datei übereinstimmen.<br>
     * Beispiel:<br>
     * <ul>
     * <li>addColumn("TEXT varchar(10) PRIMARY KEY")</li>
     * <li>addColumn("DATE date")</li>
     * <li>addColumn("TIMESTAMP timestamp")</li>
     * <li>addColumn("LONG bigint")</li>
     * <li>addColumn("DOUBLE decimal(4,3)")</li>
     * </ul>
     */
    public HsqldbTextTableBuilder addColumn(final String column) {
        this.columns.add(Objects.requireNonNull(column, "column required"));

        return this;
    }

    /**
     * Liefert eine {@link Connection} der HSQLDB In-Memory Instanz.<br>
     * Diese Connection MUSS bei Programmende geschlossen werden !<br>
     * Alle {@link HsqldbTextTableBuilder} werden zusammen in einer In-Memory Datenbank abgebildet.<br>
     * Die {@link #build(HsqldbTextTableBuilder...)}-Methode darf vorher auf den übergebenen Buildern NICHT aufgerufen werden!<br>
     * So können auch Queries zwischen den CSV-Dateien ausgeführt werden.
     */
    public Connection build(final HsqldbTextTableBuilder... builders) throws SQLException {
        // Damit Text-Tables auch im Memory-Mode funktionieren.
        System.setProperty("textdb.allow_full_path", "true");

        List<HsqldbTextTableBuilder> list = new ArrayList<>();
        list.add(this);
        list.addAll(Arrays.asList(builders));

        // Validierung.
        for (HsqldbTextTableBuilder ttb : list) {
            validatePath(ttb.path);

            if (ttb.columns.isEmpty()) {
                throw new IllegalStateException("no columns defined");
            }
        }

        StringBuilder url = new StringBuilder();
        // url.append("jdbc:hsqldb:mem:").append(this.tableName).append("-").append(System.currentTimeMillis());
        url.append("jdbc:hsqldb:mem:").append(System.currentTimeMillis());
        url.append(";shutdown=true");
        url.append(",readonly=true");
        url.append(",files_readonly=true");

        Connection connection = DriverManager.getConnection(url.toString());
        // connection.setReadOnly(true);

        try {
            for (HsqldbTextTableBuilder ttb : list) {
                try (Statement statement = connection.createStatement()) {
                    // Tabelle mit Struktur anlegen.
                    StringBuilder sql = new StringBuilder();
                    sql.append("CREATE TEXT TABLE ").append(ttb.tableName);
                    sql.append(" (");

                    for (Iterator<String> iterator = ttb.columns.iterator(); iterator.hasNext(); ) {
                        String column = iterator.next();
                        sql.append(column);

                        if (iterator.hasNext()) {
                            sql.append(", ");
                        }
                    }

                    sql.append(")");

                    statement.execute(sql.toString());

                    // Falls die CSV-Datei sich ändert oder beim Start nicht vorhanden ist.
                    // SET TABLE testcsv SOURCE OFF // Disconnect von der CSV-Datei
                    // SET TABLE testcsv SOURCE ON // Connect von der CSV-Datei
                    //
                    // Backend der Tabelle mit Layout angeben.
                    sql = new StringBuilder();
                    sql.append("SET TABLE ").append(ttb.tableName).append(" SOURCE ");
                    sql.append("\"");
                    sql.append(ttb.path);
                    sql.append(";ignore_first=").append(ttb.ignoreFirst); // Header
                    sql.append(";fs=").append(ttb.fieldSeparator); // Field Separator; \space, \t
                    sql.append(";all_quoted=").append(ttb.allQuoted); // Daten in DoubleQuotes
                    sql.append(";encoding=").append(ttb.charset.name());
                    sql.append(";cache_rows=").append(ttb.cacheRows); // max. n Zeilen im Cache
                    sql.append(";cache_size=").append(ttb.cacheSize); // max. Cache-Größe in kB
                    // sql.append(";qs=\\quote"); // Quote Character falls nicht '"'
                    sql.append("\"");

                    statement.execute(sql.toString());
                }
            }
        }
        catch (Exception ex) {
            connection.close();
            throw ex;
        }

        return connection;
    }

    /**
     * Flag, ob alle Daten in Anführungszeichen stehen (Double-Quotes).<br>
     * Default = true
     */
    public HsqldbTextTableBuilder setAllQuoted(final boolean allQuoted) {
        this.allQuoted = allQuoted;

        return this;
    }

    /**
     * Maximale Anzahl von Zeilen im Cache.<br>
     * Default = 10000
     */
    public HsqldbTextTableBuilder setCacheRows(final int cacheRows) {
        this.cacheRows = cacheRows;

        return this;
    }

    /**
     * Maximale Cache-Größe in kB.<br>
     * Default = 1024 KB = 1 MB
     */
    public HsqldbTextTableBuilder setCacheSize(final int cacheSize) {
        this.cacheSize = cacheSize;

        return this;
    }

    /**
     * Setzt den {@link Charset} der CSV-Datei.<br>
     * Default = UTF-8
     */
    public HsqldbTextTableBuilder setEncoding(final Charset charset) {
        this.charset = Objects.requireNonNull(charset, "charset required");

        return this;
    }

    /**
     * Setzt das Trennzeichen der Datenfelder.<br>
     * Default = \semi = ';'<br>
     * <br>
     * Andere Separatoren: \space, \t, \comma
     */
    public HsqldbTextTableBuilder setFieldSeparator(final String fieldSeparator) {
        this.fieldSeparator = Objects.requireNonNull(fieldSeparator, "fieldSeparator required");

        if ("\\comma".equals(this.fieldSeparator)) {
            this.fieldSeparator = ",";
        }

        return this;
    }

    /**
     * Wenn true, wird die erste Zeile übersprungen, in der Regel der Header.<br>
     * Default = true
     */
    public HsqldbTextTableBuilder setIgnoreFirst(final boolean ignoreFirst) {
        this.ignoreFirst = ignoreFirst;

        return this;
    }

    /**
     * Setzt den {@link Path} zur CSV-Datei.<br>
     * Der Tabellenname wird großgeschrieben und '.' durch '_' ersetzt,
     */
    public HsqldbTextTableBuilder setPath(final Path path) {
        validatePath(path);
        this.path = path;
        setTableName(this.path.getFileName().toString().replace('.', '_').toUpperCase());

        return this;
    }

    /**
     * Setzt den Tabellennamen.<br>
     * Default = Dateiname vom Pfad, upper case und '.' durch '_' ersetzt<br>
     */
    public HsqldbTextTableBuilder setTableName(final String tableName) {
        this.tableName = Objects.requireNonNull(tableName, "tableName required");

        return this;
    }
}
