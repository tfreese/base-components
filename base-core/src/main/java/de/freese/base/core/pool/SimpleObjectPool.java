package de.freese.base.core.pool;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * @author Thomas Freese
 */
public final class SimpleObjectPool<T> extends AbstractObjectPool<T> {
    private final Supplier<T> supplier;

    public SimpleObjectPool(final Supplier<T> supplier) {
        super();

        this.supplier = Objects.requireNonNull(supplier, "supplier required");
    }

    @Override
    protected T create() {
        return supplier.get();
    }
}
