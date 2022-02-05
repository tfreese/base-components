// Created: 05.02.2022
package de.freese.base.resourcemap.provider;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.sql.DataSource;

/**
 * @author Thomas Freese
 */
public abstract class AbstractDatabaseResourceProvider implements ResourceProvider
{
    /**
     * Erstellt ein neues {@link AbstractDatabaseResourceProvider} Object.
     */
    protected AbstractDatabaseResourceProvider()
    {
        super();
    }

    /**
     * @see de.freese.base.resourcemap.provider.ResourceProvider#getResources(java.lang.String, java.util.Locale)
     */
    @Override
    public Map<String, String> getResources(final String bundleName, final Locale locale)
    {
        Map<String, String> map = new HashMap<>();

        try (Connection connection = getDataSource().getConnection();
             PreparedStatement preparedStatement = createPreparedStatement(connection))
        {
            setLocaleProperty(preparedStatement, locale);

            try (ResultSet resultSet = preparedStatement.executeQuery())
            {
                while (resultSet.next())
                {
                    populateMap(resultSet, map);
                }
            }
        }
        catch (RuntimeException ex)
        {
            throw ex;
        }
        catch (SQLException ex)
        {
            throw new RuntimeException(ex);
        }

        return map;
    }

    /**
     * The PreparedStatement where the Columns KEY and VALUE must be present.<br>
     * Example: select MY_KEY as KEY, MY_VALUE as VALUE from TABLE where LOCALE = '?'
     *
     * @param connection {@link Connection}
     *
     * @return {@link PreparedStatement}
     *
     * @throws SQLException Falls was schief geht.
     */
    protected abstract PreparedStatement createPreparedStatement(Connection connection) throws SQLException;

    /**
     * @return {@link DataSource}
     */
    protected abstract DataSource getDataSource();

    /**
     * Puts the Key and the Value into the Map.<br>
     * The Columns KEY and VALUE must be present in the {@link ResultSet}.
     *
     * @param resultSet {@link ResultSet}
     * @param map {@link Map}
     *
     * @throws SQLException Falls was schief geht.
     */
    protected void populateMap(final ResultSet resultSet, final Map<String, String> map) throws SQLException
    {
        map.put(resultSet.getString("KEY"), resultSet.getString("VALUE"));
    }

    /**
     * Puts the Locale Paremeter into the {@link PreparedStatement}.
     *
     * @param preparedStatement {@link PreparedStatement}
     * @param locale {@link Locale}
     *
     * @throws SQLException Falls was schief geht.
     */
    protected abstract void setLocaleProperty(PreparedStatement preparedStatement, Locale locale) throws SQLException;
}
