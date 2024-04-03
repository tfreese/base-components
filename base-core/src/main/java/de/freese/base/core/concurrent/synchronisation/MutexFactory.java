// Created: 03.04.24
package de.freese.base.core.concurrent.synchronisation;

import org.springframework.util.ConcurrentReferenceHashMap;

/**
 * Example:
 * <pre>{@code
 * public Session getSession(String userId) {
 *      synchronized (stringMutexFactory.getMutex(userId)) {
 *          // Create session. If one is already present, return that one.
 *      }
 * }
 * }</pre>
 *
 * @author Thomas Freese
 */
public final class MutexFactory<T> {
    private static final int CONCURRENCY_LEVEL = 16;
    private static final int INITIAL_CAPACITY = 16;
    private static final float LOAD_FACTOR = 0.75F;
    private static final ConcurrentReferenceHashMap.ReferenceType REFERENCE_TYPE = ConcurrentReferenceHashMap.ReferenceType.WEAK;

    private final ConcurrentReferenceHashMap<T, Mutex<T>> map;

    public MutexFactory() {
        super();

        map = new ConcurrentReferenceHashMap<>(INITIAL_CAPACITY,
                LOAD_FACTOR,
                CONCURRENCY_LEVEL,
                REFERENCE_TYPE
        );

        // From Hibernate
        // map = new ConcurrentReferenceHashMap<>(INITIAL_CAPACITY,
        //         LOAD_FACTOR,
        //         CONCURRENCY_LEVEL,
        //         REFERENCE_TYPE,
        //         REFERENCE_TYPE,
        //         null
        // );
    }

    public Mutex<T> getMutex(final T key) {
        return map.computeIfAbsent(key, Mutex::new);
    }
}
