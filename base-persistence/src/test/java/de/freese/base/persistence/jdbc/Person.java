// Created: 16.06.2016
package de.freese.base.persistence.jdbc;

/**
 * @author Thomas Freese
 */
public record Person(long id, String lastName, String firstName) {
    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("Person [");
        builder.append("id=").append(this.id);
        builder.append(", lastName=").append(this.lastName);
        builder.append(", firstName=").append(this.firstName);
        builder.append("]");

        return builder.toString();
    }
}
