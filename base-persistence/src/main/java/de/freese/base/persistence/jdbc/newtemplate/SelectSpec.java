// Created: 18.08.23
package de.freese.base.persistence.jdbc.newtemplate;

import java.sql.Statement;
import java.util.function.Consumer;

/**
 * @author Thomas Freese
 */
public interface SelectSpec extends StatementSpec {
    SelectSpec statementConfigurer(Consumer<Statement> configurer);
}
