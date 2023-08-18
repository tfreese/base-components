// Created: 18.08.23
package de.freese.base.persistence.jdbc.newtemplate;

/**
 * @author Thomas Freese
 */
public class NewJdbcTemplate {

    //    static NewJdbcTemplate create(DataSource dataSource) {
    //        return new DefaultJdbcClient(dataSource);
    //    }

    public DeleteSpec delete(CharSequence sql) {
        return null;
    }

    public InsertSpec insert(CharSequence sql) {
        return null;
    }

    public SelectSpec select(CharSequence sql) {
        return new DefaultSelectSpec(sql);
    }

    public UpdateSpec update(CharSequence sql) {
        return null;
    }
}
