package de.freese.base.persistence.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;

import de.freese.base.persistence.jdbc.function.RowMapper;

/**
 * @author Thomas Freese
 * @since 16.06.2016
 */
public class PersonRowMapper implements RowMapper<Person> {
    @Override
    public Person mapRow(final ResultSet rs) throws SQLException {
        final long id = rs.getLong("ID");
        final String name = rs.getString("NAME");

        return new Person(id, name);
    }
}
