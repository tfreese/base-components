/**
 * Created: 16.06.2016
 */

package de.freese.base.persistence.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import de.freese.base.persistence.jdbc.template.function.RowMapper;

/**
 * @author Thomas Freese
 */
public class PersonRowMapper implements RowMapper<Person>
{
    /**
     * Erstellt ein neues {@link PersonRowMapper} Object.
     */
    public PersonRowMapper()
    {
        super();
    }

    /**
     * @see de.freese.base.persistence.jdbc.template.function.RowMapper#mapRow(java.sql.ResultSet)
     */
    @Override
    public Person mapRow(final ResultSet rs) throws SQLException
    {
        long id = rs.getLong("ID");
        String nachname = rs.getString("NAME");
        String vorname = rs.getString("VORNAME");

        return new Person(id, nachname, vorname);
    }
}
