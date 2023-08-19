// Created: 18.08.23
package de.freese.base.persistence.jdbc.newtemplate;

import de.freese.base.persistence.jdbc.template.function.PreparedStatementSetter;

/**
 * @author Thomas Freese
 */
public interface StatementSpec {

    ResultQuerySpec execute();

    ResultQuerySpec execute(PreparedStatementSetter pss);

    StatementSpec param(Object value);

}
