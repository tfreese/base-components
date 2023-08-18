// Created: 18.08.23
package de.freese.base.persistence.jdbc.newtemplate;

/**
 * @author Thomas Freese
 */
public interface StatementSpec {
    /**
     * First Index is 1.
     **/
    StatementSpec param(int index, Object value);

}
