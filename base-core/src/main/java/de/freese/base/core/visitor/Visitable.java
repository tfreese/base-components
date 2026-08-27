package de.freese.base.core.visitor;

/**
 * @author Thomas Freese
 */
public interface Visitable {
    default void visit(final Visitor visitor) {
        visitor.visitObject(this);
    }
}
