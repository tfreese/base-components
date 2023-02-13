package de.freese.base.core.visitor;

/**
 *
 */
public interface Visitable {
    default void visit(final Visitor visitor) {
        visitor.visitObject(this);
    }
}
