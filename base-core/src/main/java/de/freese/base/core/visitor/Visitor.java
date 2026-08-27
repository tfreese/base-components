package de.freese.base.core.visitor;

/**
 * @author Thomas Freese
 */
@FunctionalInterface
public interface Visitor {
    default <T> void visitArray(final T[] array) {
        if (array == null) {
            return;
        }

        for (final T object : array) {
            visitObject(object);
        }
    }

    default void visitIterable(final Iterable<?> iterable) {
        if (iterable == null) {
            return;
        }

        for (final Object object : iterable) {
            visitObject(object);
        }
    }

    void visitObject(Object object);
}
