// Created: 16.01.2018
package de.freese.base.core.reactive;

import java.util.concurrent.Flow;

/***
 * Bridge between Reactive Streams API and the Java 9{@link java.util.concurrent.Flow} API.
 *
 * @see <a href="https://medium.com/@olehdokuka/mastering-own-reactive-streams-implementation-part-1-publisher-e8eaf928a78c">mastering-own-reactive-streams</a>
 *
 * @author Thomas Freese
 */
public final class ReactiveStreamsFlowBridge {
    /**
     * Flow Publisher that wraps a Reactive Streams Publisher.
     */
    private static final class FlowPublisherFromReactive<T> implements Flow.Publisher<T> {
        private final org.reactivestreams.Publisher<? extends T> reactiveStreams;

        FlowPublisherFromReactive(org.reactivestreams.Publisher<? extends T> reactivePublisher) {
            this.reactiveStreams = reactivePublisher;
        }

        @Override
        public void subscribe(final Flow.Subscriber<? super T> flow) {
            if (flow == null) {
                this.reactiveStreams.subscribe(null);
                return;
            }

            this.reactiveStreams.subscribe(new ReactiveToFlowSubscriber<>(flow));
        }
    }

    /**
     * Wraps a Reactive Streams Processor and forwards methods of the Flow Processor to it.
     *
     * @param <T> the input type
     * @param <U> the output type
     */
    private static final class FlowToReactiveProcessor<T, U> implements Flow.Processor<T, U> {
        private final org.reactivestreams.Processor<? super T, ? extends U> reactiveStreams;

        FlowToReactiveProcessor(final org.reactivestreams.Processor<? super T, ? extends U> reactive) {
            this.reactiveStreams = reactive;
        }

        @Override
        public void onComplete() {
            this.reactiveStreams.onComplete();
        }

        @Override
        public void onError(final Throwable t) {
            this.reactiveStreams.onError(t);
        }

        @Override
        public void onNext(final T t) {
            this.reactiveStreams.onNext(t);
        }

        @Override
        public void onSubscribe(final Flow.Subscription s) {
            this.reactiveStreams.onSubscribe(new ReactiveToFlowSubscription(s));
        }

        @Override
        public void subscribe(final Flow.Subscriber<? super U> s) {
            if (s == null) {
                this.reactiveStreams.subscribe(null);
                return;
            }

            this.reactiveStreams.subscribe(new ReactiveToFlowSubscriber<>(s));
        }
    }

    /**
     * Wraps a Reactive Streams Subscriber and forwards methods of the Flow Subscriber to it.
     */
    private static final class FlowToReactiveSubscriber<T> implements Flow.Subscriber<T> {
        private final org.reactivestreams.Subscriber<? super T> reactiveStreams;

        FlowToReactiveSubscriber(final org.reactivestreams.Subscriber<? super T> reactive) {
            this.reactiveStreams = reactive;
        }

        @Override
        public void onComplete() {
            this.reactiveStreams.onComplete();
        }

        @Override
        public void onError(final Throwable throwable) {
            this.reactiveStreams.onError(throwable);
        }

        @Override
        public void onNext(final T item) {
            this.reactiveStreams.onNext(item);
        }

        @Override
        public void onSubscribe(final Flow.Subscription subscription) {
            this.reactiveStreams.onSubscribe(new ReactiveToFlowSubscription(subscription));
        }
    }

    /**
     * Wraps a Reactive Streams Subscription and converts the calls to a Flow Subscription.
     */
    private static final class FlowToReactiveSubscription implements Flow.Subscription {
        private final org.reactivestreams.Subscription reactiveStreams;

        FlowToReactiveSubscription(final org.reactivestreams.Subscription reactive) {
            this.reactiveStreams = reactive;
        }

        @Override
        public void cancel() {
            this.reactiveStreams.cancel();
        }

        @Override
        public void request(final long n) {
            this.reactiveStreams.request(n);
        }
    }

    /**
     * Reactive Streams Publisher that wraps a Flow Publisher.
     */
    private static final class ReactivePublisherFromFlow<T> implements org.reactivestreams.Publisher<T> {
        private final Flow.Publisher<? extends T> flow;

        ReactivePublisherFromFlow(final Flow.Publisher<? extends T> flowPublisher) {
            this.flow = flowPublisher;
        }

        @Override
        public void subscribe(final org.reactivestreams.Subscriber<? super T> reactive) {
            if (reactive == null) {
                this.flow.subscribe(null);
                return;
            }

            this.flow.subscribe(new FlowToReactiveSubscriber<>(reactive));
        }
    }

    /**
     * Wraps a Flow Processor and forwards methods of the Reactive Streams Processor to it.
     *
     * @param <T> the input type
     * @param <U> the output type
     */
    private static final class ReactiveToFlowProcessor<T, U> implements org.reactivestreams.Processor<T, U> {
        private final Flow.Processor<? super T, ? extends U> flow;

        ReactiveToFlowProcessor(final Flow.Processor<? super T, ? extends U> flow) {
            this.flow = flow;
        }

        @Override
        public void onComplete() {
            this.flow.onComplete();
        }

        @Override
        public void onError(final Throwable t) {
            this.flow.onError(t);
        }

        @Override
        public void onNext(final T t) {
            this.flow.onNext(t);
        }

        @Override
        public void onSubscribe(final org.reactivestreams.Subscription s) {
            this.flow.onSubscribe(new FlowToReactiveSubscription(s));
        }

        @Override
        public void subscribe(final org.reactivestreams.Subscriber<? super U> s) {
            if (s == null) {
                this.flow.subscribe(null);
                return;
            }

            this.flow.subscribe(new FlowToReactiveSubscriber<>(s));
        }
    }

    /**
     * Wraps a Reactive Streams Subscriber and forwards methods of the Flow Subscriber to it.
     */
    private static final class ReactiveToFlowSubscriber<T> implements org.reactivestreams.Subscriber<T> {
        private final Flow.Subscriber<? super T> flow;

        ReactiveToFlowSubscriber(final Flow.Subscriber<? super T> flow) {
            this.flow = flow;
        }

        @Override
        public void onComplete() {
            this.flow.onComplete();
        }

        @Override
        public void onError(final Throwable throwable) {
            this.flow.onError(throwable);
        }

        @Override
        public void onNext(final T item) {
            this.flow.onNext(item);
        }

        @Override
        public void onSubscribe(final org.reactivestreams.Subscription subscription) {
            this.flow.onSubscribe(new FlowToReactiveSubscription(subscription));
        }
    }

    /**
     * Wraps a Flow Subscription and converts the calls to a Reactive Streams Subscription.
     */
    private static final class ReactiveToFlowSubscription implements org.reactivestreams.Subscription {
        private final Flow.Subscription flow;

        ReactiveToFlowSubscription(final Flow.Subscription flow) {
            this.flow = flow;
        }

        @Override
        public void cancel() {
            this.flow.cancel();
        }

        @Override
        public void request(final long n) {
            this.flow.request(n);
        }
    }

    /**
     * Converts a Reactive Streams Processor into a Flow Processor.
     *
     * @param <T> the input value type
     * @param <U> the output value type
     * @param reactiveStreamsProcessor the source Reactive Streams Processor to convert
     *
     * @return the equivalent Flow Processor
     */
    @SuppressWarnings("unchecked")
    public static <T, U> Flow.Processor<T, U> toFlow(final org.reactivestreams.Processor<? super T, ? extends U> reactiveStreamsProcessor) {
        if (reactiveStreamsProcessor == null) {
            throw new NullPointerException("reactiveStreamsProcessor");
        }

        if (reactiveStreamsProcessor instanceof Flow.Processor) {
            return (Flow.Processor<T, U>) reactiveStreamsProcessor;
        }

        if (reactiveStreamsProcessor instanceof ReactiveToFlowProcessor) {
            return (Flow.Processor<T, U>) (((ReactiveToFlowProcessor<T, U>) reactiveStreamsProcessor).flow);
        }

        return new FlowToReactiveProcessor<>(reactiveStreamsProcessor);
    }

    /**
     * Converts a Reactive Streams Publisher into a Flow Publisher.
     *
     * @param reactiveStreamsPublisher the source Reactive Streams Publisher to convert
     *
     * @return the equivalent Flow Publisher
     */
    @SuppressWarnings("unchecked")
    public static <T> Flow.Publisher<T> toFlow(final org.reactivestreams.Publisher<? extends T> reactiveStreamsPublisher) {
        if (reactiveStreamsPublisher == null) {
            throw new NullPointerException("reactiveStreamsPublisher");
        }

        if (reactiveStreamsPublisher instanceof Flow.Publisher) {
            return (Flow.Publisher<T>) reactiveStreamsPublisher;
        }

        if (reactiveStreamsPublisher instanceof ReactivePublisherFromFlow) {
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
     *
     * @return the equivalent Reactive Streams Processor
     */
    @SuppressWarnings("unchecked")
    public static <T, U> org.reactivestreams.Processor<T, U> toReactiveStreams(final Flow.Processor<? super T, ? extends U> flowProcessor) {
        if (flowProcessor == null) {
            throw new NullPointerException("flowProcessor");
        }

        if (flowProcessor instanceof org.reactivestreams.Processor) {
            return (org.reactivestreams.Processor<T, U>) flowProcessor;
        }

        if (flowProcessor instanceof FlowToReactiveProcessor) {
            return (org.reactivestreams.Processor<T, U>) (((FlowToReactiveProcessor<T, U>) flowProcessor).reactiveStreams);
        }

        return new ReactiveToFlowProcessor<>(flowProcessor);
    }

    /**
     * Converts a Flow Publisher into a Reactive Streams Publisher.
     *
     * @param flowPublisher the source Flow Publisher to convert
     *
     * @return the equivalent Reactive Streams Publisher
     */
    @SuppressWarnings("unchecked")
    public static <T> org.reactivestreams.Publisher<T> toReactiveStreams(final Flow.Publisher<? extends T> flowPublisher) {
        if (flowPublisher == null) {
            throw new NullPointerException("flowPublisher");
        }

        if (flowPublisher instanceof org.reactivestreams.Publisher) {
            return (org.reactivestreams.Publisher<T>) flowPublisher;
        }

        if (flowPublisher instanceof FlowPublisherFromReactive) {
            return (org.reactivestreams.Publisher<T>) (((FlowPublisherFromReactive<T>) flowPublisher).reactiveStreams);
        }

        return new ReactivePublisherFromFlow<>(flowPublisher);
    }

    private ReactiveStreamsFlowBridge() {
        throw new IllegalStateException("No instances!");
    }
}
