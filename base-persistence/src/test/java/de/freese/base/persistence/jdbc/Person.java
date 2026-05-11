// Created: 16.06.2016
package de.freese.base.persistence.jdbc;

/**
 * @author Thomas Freese
 */
public record Person(long id, String name) {
    @Override
    public String toString() {
        return "Person ["
                + "id=" + this.id()
                + ", name=" + this.name()
                + "]";
    }
}
