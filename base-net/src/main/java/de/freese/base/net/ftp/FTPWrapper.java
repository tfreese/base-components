package de.freese.base.net.ftp;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Interface für einen FTP Client.
 *
 * @author Thomas Freese
 */
public interface FTPWrapper
{
    /**
     * Wechselt das Arbeitsverzeichnis (relativ).
     *
     * @param dir String
     *
     * @throws Exception Falls was schiefgeht.
     */
    void changeWorkingDirectory(final String dir) throws Exception;

    /**
     * Wechselt das Arbeitsverzeichnis.
     *
     * @param path String
     * @param createDirs Sollen nicht vorhandene Unter-Verzeichnisse angelegt werden ?
     *
     * @throws Exception Falls was schiefgeht.
     * @throws IllegalArgumentException Falls was fehlt.
     */
    void changeWorkingDirectory(String path, final boolean createDirs) throws Exception;

    /**
     * Verbindung herstellen.
     *
     * @param host String
     *
     * @throws Exception Falls was schiefgeht.
     */
    void connect(final String host) throws Exception;

    /**
     * Der FTP Client wird als Letztes auf null gesetzt.
     *
     * @throws Exception Falls was schiefgeht.
     */
    void disconnect() throws Exception;

    /**
     * Debug-Modus.
     *
     * @return boolean
     */
    boolean isDebug();

    /**
     * Anmeldung.
     *
     * @param user String
     * @param psw String
     *
     * @throws Exception Falls was schiefgeht.
     */
    void login(final String user, final String psw) throws Exception;

    /**
     * Abmeldung.
     *
     * @throws Exception Falls was schiefgeht.
     */
    void logout() throws Exception;

    /**
     * Liefert das aktuelle Arbeitsverzeichnis.
     *
     * @return String
     *
     * @throws Exception Falls was schiefgeht.
     */
    String printWorkingDirectory() throws Exception;

    /**
     * Laden einer Datei.
     *
     * @param dateiName String
     *
     * @return {@link ByteArrayOutputStream}
     *
     * @throws Exception Falls was schiefgeht.
     */
    ByteArrayOutputStream retrieveFile(final String dateiName) throws Exception;

    /**
     * Laden einer Datei.
     *
     * @param dateiName String
     * @param outputStream {@link OutputStream}
     *
     * @throws Exception Falls was schiefgeht.
     */
    void retrieveFile(final String dateiName, final OutputStream outputStream) throws Exception;

    /**
     * Debug-Modus.
     *
     * @param debug boolean
     */
    void setDebug(boolean debug);

    /**
     * Setzt den Port (default 21).
     *
     * @param port int
     */
    void setPort(final int port);

    /**
     * Setzt den TimeOut.
     *
     * @param timeOut int
     *
     * @throws Exception Falls was schiefgeht.
     * @throws IllegalArgumentException Falls was schiefgeht.
     */
    void setTimeOut(final int timeOut) throws Exception;

    /**
     * Speichern einer Datei.
     *
     * @param dateiName String
     * @param is {@link InputStream}
     *
     * @throws Exception Falls was schiefgeht.
     */
    void storeFile(final String dateiName, final InputStream is) throws Exception;
}
