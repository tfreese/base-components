// Created: 16.06.2016
package de.freese.base.persistence.jdbc;

/**
 * @author Thomas Freese
 */
public record Person(long id, String name) {
    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("Person [");
        builder.append("id=").append(id());
        builder.append(", name=").append(name());
        builder.append("]");

        return builder.toString();
    }
}
