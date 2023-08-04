// Created: 04.08.23
package de.freese.base.core.pool;

import java.lang.reflect.ParameterizedType;

/**
 * @author Thomas Freese
 */
public abstract class AbstractPool<T> implements ObjectPool<T> {

    private volatile boolean closed;

    @Override
    public final void close() throws Exception {
        if (isClosed()) {
            throw new IllegalStateException("pool is already closed");
        }

        doClose();
    }

    @Override
    public final void free(final T object) {
        if (isClosed()) {
            throw new IllegalStateException("pool is closed");
        }

        if (object == null) {
            return;
        }

        doFree(object);
    }

    @Override
    public final T get() {
        if (isClosed()) {
            throw new IllegalStateException("pool is closed");
        }

        return doGet();
    }

    @Override
    public String toString() {
        String clazzName = null;

        try {
            clazzName = tryDetermineType().getSimpleName();
        }
        catch (Exception ex) {
            T object = doGet();

            if (object != null) {
                clazzName = object.getClass().getSimpleName();

                doFree(object);
            }
        }

        StringBuilder builder = new StringBuilder();
        builder.append(getClass().getSimpleName());
        builder.append("<").append(clazzName).append(">");
        builder.append(": ");
        builder.append("idle = ").append(getNumIdle());
        builder.append(", active = ").append(getNumActive());

        return builder.toString();
    }

    protected abstract void doClose();

    protected abstract void doFree(final T object);

    protected abstract T doGet();

    protected boolean isClosed() {
        return closed;
    }

    protected void setClosed(final boolean closed) {
        this.closed = closed;
    }

    /**
     * This works only, if the Super-Class is not generic too !
     *
     * <pre>
     * {@code
     * public class MyObjectPool extends AbstractObjectPool<Integer>
     * }
     * </pre>
     */
    @SuppressWarnings("unchecked")
    protected Class<T> tryDetermineType() throws ClassCastException {
        ParameterizedType parameterizedType = (ParameterizedType) getClass().getGenericSuperclass();

        return (Class<T>) parameterizedType.getActualTypeArguments()[0];
    }
}
