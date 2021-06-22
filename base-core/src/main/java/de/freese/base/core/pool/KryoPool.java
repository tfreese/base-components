// Created: 22.06.2021
package de.freese.base.core.pool;

import java.lang.ref.SoftReference;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * A pool of objects that can be reused to avoid allocations.<br>
 * The pool is optionally thread safe and can be configured to use soft references.<br>
 * <a href="https://github.com/EsotericSoftware/kryo/blob/master/src/com/esotericsoftware/kryo/util/Pool.java">Github</a>
 *
 * @author Nathan Sweet
 * @author Martin Grotzke
 * @author Thomas Freese
 * @param <T> Type
 */
public abstract class KryoPool<T>
{
    /**
     * Objects implementing this interface will have {@link #reset()} called when passed to {@link KryoPool#free(Object)}.
     */
    public interface Poolable
    {
        /**
         * Resets the object for reuse. Object references should be nulled and fields may be set to default values.
         */
        void reset();
    }

    /**
     * Wraps queue values with {@link SoftReference} for {@link KryoPool}.
     *
     * @author Martin Grotzke
     * @author Thomas Freese
     * @param <T> Type
     */
    static class SoftReferenceQueue<T> implements Queue<T>
    {
        /**
         *
         */
        private final Queue<SoftReference<T>> delegate;

        /**
         * Erstellt ein neues {@link SoftReferenceQueue} Object.
         *
         * @param delegate {@link Queue}
         */
        public SoftReferenceQueue(final Queue<SoftReference<T>> delegate)
        {
            this.delegate = delegate;
        }

        /**
         * @see java.util.Queue#add(java.lang.Object)
         */
        @Override
        public boolean add(final T e)
        {
            return false;
        }

        /**
         * @see java.util.Collection#addAll(java.util.Collection)
         */
        @Override
        public boolean addAll(final Collection<? extends T> c)
        {
            return false;
        }

        /**
         *
         */
        void clean()
        {
            this.delegate.removeIf(o -> o.get() == null);
        }

        /**
         *
         */
        void cleanOne()
        {
            for (Iterator<SoftReference<T>> iter = this.delegate.iterator(); iter.hasNext();)
            {
                if (iter.next().get() == null)
                {
                    iter.remove();
                    break;
                }
            }
        }

        /**
         * @see java.util.Collection#clear()
         */
        @Override
        public void clear()
        {
            this.delegate.clear();
        }

        /**
         * @see java.util.Collection#contains(java.lang.Object)
         */
        @Override
        public boolean contains(final Object o)
        {
            return false;
        }

        /**
         * @see java.util.Collection#containsAll(java.util.Collection)
         */
        @Override
        public boolean containsAll(final Collection<?> c)
        {
            return false;
        }

        /**
         * @see java.util.Queue#element()
         */
        @Override
        public T element()
        {
            return null;
        }

        /**
         * @see java.util.Collection#isEmpty()
         */
        @Override
        public boolean isEmpty()
        {
            return false;
        }

        /**
         * @see java.util.Collection#iterator()
         */
        @Override
        public Iterator<T> iterator()
        {
            return null;
        }

        /**
         * @see java.util.Queue#offer(java.lang.Object)
         */
        @Override
        public boolean offer(final T e)
        {
            return this.delegate.add(new SoftReference<>(e));
        }

        /**
         * @see java.util.Queue#peek()
         */
        @Override
        public T peek()
        {
            return null;
        }

        /**
         * @see java.util.Queue#poll()
         */
        @Override
        public T poll()
        {
            while (true)
            {
                SoftReference<T> reference = this.delegate.poll();

                if (reference == null)
                {
                    return null;
                }

                T object = reference.get();

                if (object != null)
                {
                    return object;
                }
            }
        }

        /**
         * @see java.util.Queue#remove()
         */
        @Override
        public T remove()
        {
            return null;
        }

        /**
         * @see java.util.Collection#remove(java.lang.Object)
         */
        @Override
        public boolean remove(final Object o)
        {
            return false;
        }

        /**
         * @see java.util.Collection#removeAll(java.util.Collection)
         */
        @Override
        public boolean removeAll(final Collection<?> c)
        {
            return false;
        }

        /**
         * @see java.util.Collection#retainAll(java.util.Collection)
         */
        @Override
        public boolean retainAll(final Collection<?> c)
        {
            return false;
        }

        /**
         * @see java.util.Collection#size()
         */
        @Override
        public int size()
        {
            return this.delegate.size();
        }

        /**
         * @see java.util.Collection#toArray()
         */
        @Override
        public Object[] toArray()
        {
            return null;
        }

        /**
         * @see java.util.Collection#toArray(java.lang.Object[])
         */
        @Override
        public <E> E[] toArray(final E[] a)
        {
            return null;
        }
    }

    /**
     *
     */
    private final Queue<T> freeObjects;

    /**
     *
     */
    private int peak;

    /**
     * Creates a pool with no maximum.
     *
     * @param threadSafe boolean
     * @param softReferences boolean
     */
    protected KryoPool(final boolean threadSafe, final boolean softReferences)
    {
        this(threadSafe, softReferences, Integer.MAX_VALUE);
    }

    /**
     * @param threadSafe boolean
     * @param softReferences boolean
     * @param maximumCapacity int; The maximum number of free objects to store in this pool.<br>
     *            Objects are not created until {@link #obtain()} is called and no free objects are available.
     */
    @SuppressWarnings("unchecked")
    protected KryoPool(final boolean threadSafe, final boolean softReferences, final int maximumCapacity)
    {
        Queue<T> queue;

        if (threadSafe)
        {
            queue = new LinkedBlockingQueue<>(maximumCapacity)
            {
                /**
                 *
                 */
                private static final long serialVersionUID = 1L;

                @Override
                public boolean add(final T o)
                {
                    return super.offer(o);
                }
            };
        }
        else if (softReferences)
        {
            // More efficient clean() than ArrayDeque.
            queue = new LinkedList<>()
            {
                /**
                 *
                 */
                private static final long serialVersionUID = 1L;

                @Override
                public boolean add(final T object)
                {
                    if (size() >= maximumCapacity)
                    {
                        return false;
                    }

                    super.add(object);

                    return true;
                }
            };
        }
        else
        {
            queue = new ArrayDeque<>()
            {
                /**
                 *
                 */
                private static final long serialVersionUID = 1L;

                @Override
                public boolean offer(final T object)
                {
                    if (size() >= maximumCapacity)
                    {
                        return false;
                    }
                    super.offer(object);
                    return true;
                }
            };
        }

        this.freeObjects = softReferences ? new SoftReferenceQueue<>(((Queue<SoftReference<T>>) queue)) : queue;
    }

    /**
     * If using soft references, all soft references whose objects have been garbage collected are removed from the pool. This can be useful to reduce the
     * number of objects in the pool before calling {@link #getFree()} or when the pool has no maximum capacity. It is not necessary to call {@link #clean()}
     * before calling {@link #free(Object)}, which will try to remove an empty reference if the maximum capacity has been reached.
     */
    public void clean()
    {
        if (this.freeObjects instanceof SoftReferenceQueue)
        {
            ((SoftReferenceQueue<T>) this.freeObjects).clean();
        }
    }

    /**
     * Removes all free objects from this pool.
     */
    public void clear()
    {
        this.freeObjects.clear();
    }

    /**
     * @return Object
     */
    protected abstract T create();

    /**
     * Puts the specified object in the pool, making it eligible to be returned by {@link #obtain()}. If the pool already contains the maximum number of free
     * objects, the specified object is reset but not added to the pool.
     * <p>
     * If using soft references and the pool contains the maximum number of free objects, the first soft reference whose object has been garbage collected is
     * discarded to make room.
     *
     * @param object Object
     */
    public void free(final T object)
    {
        if (object == null)
        {
            throw new IllegalArgumentException("object cannot be null.");
        }

        reset(object);

        if (!this.freeObjects.offer(object) && (this.freeObjects instanceof SoftReferenceQueue))
        {
            ((SoftReferenceQueue<T>) this.freeObjects).cleanOne();

            this.freeObjects.offer(object);
        }

        this.peak = Math.max(this.peak, this.freeObjects.size());
    }

    /**
     * The number of objects available to be obtained.
     * <p>
     * If using soft references, this number may include objects that have been garbage collected. {@link #clean()} may be used first to remove empty soft
     * references.
     *
     * @return int
     */
    public int getFree()
    {
        return this.freeObjects.size();
    }

    /**
     * The all-time highest number of free objects. This can help determine if a pool's maximum capacity is set appropriately. It can be reset any time with
     * {@link #resetPeak()}.
     * <p>
     * If using soft references, this number may include objects that have been garbage collected.
     *
     * @return int
     */
    public int getPeak()
    {
        return this.peak;
    }

    /**
     * Returns an object from this pool. The object may be new (from {@link #create()}) or reused (previously {@link #free(Object) freed}).
     *
     * @return Object
     */
    public T obtain()
    {
        T object = this.freeObjects.poll();

        return object != null ? object : create();
    }

    /**
     * Called when an object is freed to clear the state of the object for possible later reuse. The default implementation calls {@link Poolable#reset()} if
     * the object is {@link Poolable}.
     *
     * @param object Object
     */
    protected void reset(final T object)
    {
        if (object instanceof Poolable)
        {
            ((Poolable) object).reset();
        }
    }

    /**
     *
     */
    public void resetPeak()
    {
        this.peak = 0;
    }
}
