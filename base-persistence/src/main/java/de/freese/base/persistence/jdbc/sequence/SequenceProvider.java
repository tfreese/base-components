package de.freese.base.persistence.jdbc.sequence;

import java.sql.SQLException;

/**
 * Liefert die nächste ID der Sequence.
 *
 * @author Thomas Freese
 * @since 04.02.2017
 */
@FunctionalInterface
public interface SequenceProvider {
    long getNextID(String sequence) throws SQLException;
}
