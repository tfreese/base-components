// Created: 16.01.2018
package de.freese.base.core.reactive;

import java.util.concurrent.Flow;

/***
 * Bridge between Reactive Streams API and the Java 9{@link java.util.concurrent.Flow} API.
 *
 * @see <a href="https://medium.com/@olehdokuka/mastering-own-reactive-streams-implementation-part-1-publisher-e8eaf928a78c">mastering-own-reactive-streams</a>
 * @author Thomas Freese
 */
public final class ReactiveStreamsFlowBridge
{
    /**
     * Flow Publisher that wraps a Reactive Streams Publisher.
     *
     * @param <T> the element type
     */
    private static final class FlowPublisherFromReactive<T> implements Flow.Publisher<T>
    {
        /**
         *
         */
        private final org.reactivestreams.Publisher<? extends T> reactiveStreams;

        /**
         * Erstellt ein neues {@link FlowPublisherFromReactive} Object.
         *
         * @param reactivePublisher {@link org.reactivestreams.Publisher}
         */
        public FlowPublisherFromReactive(final org.reactivestreams.Publisher<? extends T> reactivePublisher)
        {
            this.reactiveStreams = reactivePublisher;
        }

        /**
         * @see java.util.concurrent.Flow.Publisher#subscribe(java.util.concurrent.Flow.Subscriber)
         */
        @Override
        public void subscribe(final Flow.Subscriber<? super T> flow)
        {
            if (flow == null)
            {
                this.reactiveStreams.subscribe(null);
                return;
            }

            this.reactiveStreams.subscribe(new ReactiveToFlowSubscriber<T>(flow));
        }
    }

    /**
     * Wraps a Reactive Streams Processor and forwards methods of the Flow Processor to it.
     *
     * @param <T> the input type
     * @param <U> the output type
     */
    private static final class FlowToReactiveProcessor<T, U> implements Flow.Processor<T, U>
    {
        /**
         *
         */
        private final org.reactivestreams.Processor<? super T, ? extends U> reactiveStreams;

        /**
         * Erstellt ein neues {@link FlowToReactiveProcessor} Object.
         *
         * @param reactive {@link org.reactivestreams.Processor}
         */
        public FlowToReactiveProcessor(final org.reactivestreams.Processor<? super T, ? extends U> reactive)
        {
            this.reactiveStreams = reactive;
        }

        /**
         * @see java.util.concurrent.Flow.Subscriber#onComplete()
         */
        @Override
        public void onComplete()
        {
            this.reactiveStreams.onComplete();
        }

        /**
         * @see java.util.concurrent.Flow.Subscriber#onError(java.lang.Throwable)
         */
        @Override
        public void onError(final Throwable t)
        {
            this.reactiveStreams.onError(t);
        }

        /**
         * @see java.util.concurrent.Flow.Subscriber#onNext(java.lang.Object)
         */
        @Override
        public void onNext(final T t)
        {
            this.reactiveStreams.onNext(t);
        }

        /**
         * @see java.util.concurrent.Flow.Subscriber#onSubscribe(java.util.concurrent.Flow.Subscription)
         */
        @Override
        public void onSubscribe(final Flow.Subscription s)
        {
            this.reactiveStreams.onSubscribe(new ReactiveToFlowSubscription(s));
        }

        /**
         * @see java.util.concurrent.Flow.Publisher#subscribe(java.util.concurrent.Flow.Subscriber)
         */
        @Override
        public void subscribe(final Flow.Subscriber<? super U> s)
        {
            if (s == null)
            {
                this.reactiveStreams.subscribe(null);
                return;
            }

            this.reactiveStreams.subscribe(new ReactiveToFlowSubscriber<U>(s));
        }
    }

    /**
     * Wraps a Reactive Streams Subscriber and forwards methods of the Flow Subscriber to it.
     *
     * @param <T> the element type
     */
    private static final class FlowToReactiveSubscriber<T> implements Flow.Subscriber<T>
    {
        /**
         *
         */
        private final org.reactivestreams.Subscriber<? super T> reactiveStreams;

        /**
         * Erstellt ein neues {@link FlowToReactiveSubscriber} Object.
         *
         * @param reactive {@link org.reactivestreams.Subscriber}
         */
        public FlowToReactiveSubscriber(final org.reactivestreams.Subscriber<? super T> reactive)
        {
            this.reactiveStreams = reactive;
        }

        /**
         * @see java.util.concurrent.Flow.Subscriber#onComplete()
         */
        @Override
        public void onComplete()
        {
            this.reactiveStreams.onComplete();
        }

        /**
         * @see java.util.concurrent.Flow.Subscriber#onError(java.lang.Throwable)
         */
        @Override
        public void onError(final Throwable throwable)
        {
            this.reactiveStreams.onError(throwable);
        }

        /**
         * @see java.util.concurrent.Flow.Subscriber#onNext(java.lang.Object)
         */
        @Override
        public void onNext(final T item)
        {
            this.reactiveStreams.onNext(item);
        }

        /**
         * @see java.util.concurrent.Flow.Subscriber#onSubscribe(java.util.concurrent.Flow.Subscription)
         */
        @Override
        public void onSubscribe(final Flow.Subscription subscription)
        {
            this.reactiveStreams.onSubscribe(new ReactiveToFlowSubscription(subscription));
        }
    }

    /**
     * Wraps a Reactive Streams Subscription and converts the calls to a Flow Subscription.
     */
    private static final class FlowToReactiveSubscription implements Flow.Subscription
    {
        /**
         *
         */
        private final org.reactivestreams.Subscription reactiveStreams;

        /**
         * Erstellt ein neues {@link FlowToReactiveSubscription} Object.
         *
         * @param reactive {@link org.reactivestreams.Subscription}
         */
        public FlowToReactiveSubscription(final org.reactivestreams.Subscription reactive)
        {
            this.reactiveStreams = reactive;
        }

        /**
         * @see java.util.concurrent.Flow.Subscription#cancel()
         */
        @Override
        public void cancel()
        {
            this.reactiveStreams.cancel();
        }

        /**
         * @see java.util.concurrent.Flow.Subscription#request(long)
         */
        @Override
        public void request(final long n)
        {
            this.reactiveStreams.request(n);
        }
    }

    /**
     * Reactive Streams Publisher that wraps a Flow Publisher.
     *
     * @param <T> the element type
     */
    private static final class ReactivePublisherFromFlow<T> implements org.reactivestreams.Publisher<T>
    {
        /**
         *
         */
        private final Flow.Publisher<? extends T> flow;

        /**
         * Erstellt ein neues {@link ReactivePublisherFromFlow} Object.
         *
         * @param flowPublisher {@link Flow.Publisher}
         */
        @SuppressWarnings("javadoc")
        public ReactivePublisherFromFlow(final Flow.Publisher<? extends T> flowPublisher)
        {
            this.flow = flowPublisher;
        }

        /**
         * @see org.reactivestreams.Publisher#subscribe(org.reactivestreams.Subscriber)
         */
        @Override
        public void subscribe(final org.reactivestreams.Subscriber<? super T> reactive)
        {
            if (reactive == null)
            {
                this.flow.subscribe(null);
                return;
            }

            this.flow.subscribe(new FlowToReactiveSubscriber<T>(reactive));
        }
    }

    /**
     * Wraps a Flow Processor and forwards methods of the Reactive Streams Processor to it.
     *
     * @param <T> the input type
     * @param <U> the output type
     */
    private static final class ReactiveToFlowProcessor<T, U> implements org.reactivestreams.Processor<T, U>
    {
        /**
         *
         */
        private final Flow.Processor<? super T, ? extends U> flow;

        /**
         * Erstellt ein neues {@link ReactiveToFlowProcessor} Object.
         *
         * @param flow {@link Flow.Processor}
         */
        @SuppressWarnings("javadoc")
        public ReactiveToFlowProcessor(final Flow.Processor<? super T, ? extends U> flow)
        {
            this.flow = flow;
        }

        /**
         * @see org.reactivestreams.Subscriber#onComplete()
         */
        @Override
        public void onComplete()
        {
            this.flow.onComplete();
        }

        /**
         * @see org.reactivestreams.Subscriber#onError(java.lang.Throwable)
         */
        @Override
        public void onError(final Throwable t)
        {
            this.flow.onError(t);
        }

        /**
         * @see org.reactivestreams.Subscriber#onNext(java.lang.Object)
         */
        @Override
        public void onNext(final T t)
        {
            this.flow.onNext(t);
        }

        /**
         * @see org.reactivestreams.Subscriber#onSubscribe(org.reactivestreams.Subscription)
         */
        @Override
        public void onSubscribe(final org.reactivestreams.Subscription s)
        {
            this.flow.onSubscribe(new FlowToReactiveSubscription(s));
        }

        /**
         * @see org.reactivestreams.Publisher#subscribe(org.reactivestreams.Subscriber)
         */
        @Override
        public void subscribe(final org.reactivestreams.Subscriber<? super U> s)
        {
            if (s == null)
            {
                this.flow.subscribe(null);
                return;
            }

            this.flow.subscribe(new FlowToReactiveSubscriber<U>(s));
        }
    }

    /**
     * Wraps a Reactive Streams Subscriber and forwards methods of the Flow Subscriber to it.
     *
     * @param <T> the element type
     */
    private static final class ReactiveToFlowSubscriber<T> implements org.reactivestreams.Subscriber<T>
    {
        /**
         *
         */
        private final Flow.Subscriber<? super T> flow;

        /**
         * Erstellt ein neues {@link ReactiveToFlowSubscriber} Object.
         *
         * @param flow {@link Flow.Subscriber}
         */
        @SuppressWarnings("javadoc")
        public ReactiveToFlowSubscriber(final Flow.Subscriber<? super T> flow)
        {
            this.flow = flow;
        }

        /**
         * @see org.reactivestreams.Subscriber#onComplete()
         */
        @Override
        public void onComplete()
        {
            this.flow.onComplete();
        }

        /**
         * @see org.reactivestreams.Subscriber#onError(java.lang.Throwable)
         */
        @Override
        public void onError(final Throwable throwable)
        {
            this.flow.onError(throwable);
        }

        /**
         * @see org.reactivestreams.Subscriber#onNext(java.lang.Object)
         */
        @Override
        public void onNext(final T item)
        {
            this.flow.onNext(item);
        }

        /**
         * @see org.reactivestreams.Subscriber#onSubscribe(org.reactivestreams.Subscription)
         */
        @Override
        public void onSubscribe(final org.reactivestreams.Subscription subscription)
        {
            this.flow.onSubscribe(new FlowToReactiveSubscription(subscription));
        }
    }

    /**
     * Wraps a Flow Subscription and converts the calls to a Reactive Streams Subscription.
     */
    private static final class ReactiveToFlowSubscription implements org.reactivestreams.Subscription
    {
        /**
         *
         */
        private final Flow.Subscription flow;

        /**
         * Erstellt ein neues {@link ReactiveToFlowSubscription} Object.
         *
         * @param flow {@link Flow.Subscription}
         */
        @SuppressWarnings("javadoc")
        public ReactiveToFlowSubscription(final Flow.Subscription flow)
        {
            this.flow = flow;
        }

        /**
         * @see org.reactivestreams.Subscription#cancel()
         */
        @Override
        public void cancel()
        {
            this.flow.cancel();
        }

        /**
         * @see org.reactivestreams.Subscription#request(long)
         */
        @Override
        public void request(final long n)
        {
            this.flow.request(n);
        }
    }

    /**
     * Converts a Reactive Streams Processor into a Flow Processor.
     *
     * @param <T> the input value type
     * @param <U> the output value type
     * @param reactiveStreamsProcessor the source Reactive Streams Processor to convert
     * @return the equivalent Flow Processor
     */
    @SuppressWarnings("unchecked")
    public static <T, U> Flow.Processor<T, U> toFlow(final org.reactivestreams.Processor<? super T, ? extends U> reactiveStreamsProcessor)
    {
        if (reactiveStreamsProcessor == null)
        {
            throw new NullPointerException("reactiveStreamsProcessor");
        }

        if (reactiveStreamsProcessor instanceof Flow.Processor)
        {
            return (Flow.Processor<T, U>) reactiveStreamsProcessor;
        }

        if (reactiveStreamsProcessor instanceof ReactiveToFlowProcessor)
        {
            return (Flow.Processor<T, U>) (((ReactiveToFlowProcessor<T, U>) reactiveStreamsProcessor).flow);
        }

        return new FlowToReactiveProcessor<>(reactiveStreamsProcessor);
    }

    /**
     * Converts a Reactive Streams Publisher into a Flow Publisher.
     *
     * @param <T> the element type
     * @param reactiveStreamsPublisher the source Reactive Streams Publisher to convert
     * @return the equivalent Flow Publisher
     */
    @SuppressWarnings("unchecked")
    public static <T> Flow.Publisher<T> toFlow(final org.reactivestreams.Publisher<? extends T> reactiveStreamsPublisher)
    {
        if (reactiveStreamsPublisher == null)
        {
            throw new NullPointerException("reactiveStreamsPublisher");
        }

        if (reactiveStreamsPublisher instanceof Flow.Publisher)
        {
            return (Flow.Publisher<T>) reactiveStreamsPublisher;
        }

        if (reactiveStreamsPublisher instanceof ReactivePublisherFromFlow)
        {
            return (Flow.Publisher<T>) (((ReactivePublisherFromFlow<T>) reactiveStreamsPublisher).flow);
        }

        return new FlowPublisherFromReactive<>(reactiveStreamsPublisher);
    }

    /**
     * Converts a Flow Processor into a Reactive Streams Processor.
     *
     * @param <T> the input value type
     * @param <U> the output value type
     * @param flowProcessor the source Flow Processor to convert
     * @return the equivalent Reactive Streams Processor
     */
    @SuppressWarnings("unchecked")
    public static <T, U> org.reactivestreams.Processor<T, U> toReactiveStreams(final Flow.Processor<? super T, ? extends U> flowProcessor)
    {
        if (flowProcessor == null)
        {
            throw new NullPointerException("flowProcessor");
        }

        if (flowProcessor instanceof org.reactivestreams.Processor)
        {
            return (org.reactivestreams.Processor<T, U>) flowProcessor;
        }

        if (flowProcessor instanceof FlowToReactiveProcessor)
        {
            return (org.reactivestreams.Processor<T, U>) (((FlowToReactiveProcessor<T, U>) flowProcessor).reactiveStreams);
        }

        return new ReactiveToFlowProcessor<>(flowProcessor);
    }

    /**
     * Converts a Flow Publisher into a Reactive Streams Publisher.
     *
     * @param <T> the element type
     * @param flowPublisher the source Flow Publisher to convert
     * @return the equivalent Reactive Streams Publisher
     */
    @SuppressWarnings("unchecked")
    public static <T> org.reactivestreams.Publisher<T> toReactiveStreams(final Flow.Publisher<? extends T> flowPublisher)
    {
        if (flowPublisher == null)
        {
            throw new NullPointerException("flowPublisher");
        }

        if (flowPublisher instanceof org.reactivestreams.Publisher)
        {
            return (org.reactivestreams.Publisher<T>) flowPublisher;
        }

        if (flowPublisher instanceof FlowPublisherFromReactive)
        {
            return (org.reactivestreams.Publisher<T>) (((FlowPublisherFromReactive<T>) flowPublisher).reactiveStreams);
        }

        return new ReactivePublisherFromFlow<>(flowPublisher);
    }

    /**
     * Erzeugt eine neue Instanz von {@link ReactiveStreamsFlowBridge}.
     */
    private ReactiveStreamsFlowBridge()
    {
        throw new IllegalStateException("No instances!");
    }
}
