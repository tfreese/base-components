// Created: 16.06.2016
package de.freese.base.persistence.jdbc;

/**
 * @author Thomas Freese
 */
public class Person {
    private final String firstName;
    private final long id;
    private final String lastName;

    public Person(final long id, final String lastName, final String firstName) {
        super();

        this.id = id;
        this.lastName = lastName;
        this.firstName = firstName;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public long getId() {
        return this.id;
    }

    public String getLastName() {
        return this.lastName;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Person [");
        builder.append("id=").append(this.id);
        builder.append(", lastName=").append(this.lastName);
        builder.append(", firstName=").append(this.firstName);
        builder.append("]");

        return builder.toString();
    }
}
