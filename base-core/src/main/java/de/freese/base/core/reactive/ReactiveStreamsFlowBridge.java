package de.freese.base.core.reactive;

import java.util.concurrent.Flow;

import org.reactivestreams.Processor;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

/***
 * Bridge between Reactive Streams API and the Java 9{@link java.util.concurrent.Flow} API.<br>
 *
 * <a href="https://medium.com/@olehdokuka/mastering-own-reactive-streams-implementation-part-1-publisher-e8eaf928a78c">mastering-own-reactive-streams</a>
 *
 * @author Thomas Freese
 * @since 16.01.2018
 */
@SuppressWarnings({"UnusedLocalVariable", "rawtypes", "java:S1481"})
public final class ReactiveStreamsFlowBridge {
    /**
     * Flow Publisher that wraps a Reactive Streams Publisher.
     */
    private record FlowPublisherFromReactive<T>(Publisher<? extends T> reactiveStreams) implements Flow.Publisher<T> {
        @Override
        public void subscribe(final Flow.Subscriber<? super T> flow) {
            if (flow == null) {
                reactiveStreams.subscribe(null);
                return;
            }

            reactiveStreams.subscribe(new ReactiveToFlowSubscriber<>(flow));
        }
    }

    /**
     * Wraps a Reactive Streams Processor and forwards methods of the Flow Processor to it.
     *
     * @param <T> the input type
     * @param <U> the output type
     */
    private record FlowToReactiveProcessor<T, U>(Processor<? super T, ? extends U> reactiveStreams) implements Flow.Processor<T, U> {
        @Override
        public void onComplete() {
            reactiveStreams.onComplete();
        }

        @Override
        public void onError(final Throwable t) {
            reactiveStreams.onError(t);
        }

        @Override
        public void onNext(final T t) {
            reactiveStreams.onNext(t);
        }

        @Override
        public void onSubscribe(final Flow.Subscription s) {
            reactiveStreams.onSubscribe(new ReactiveToFlowSubscription(s));
        }

        @Override
        public void subscribe(final Flow.Subscriber<? super U> s) {
            if (s == null) {
                reactiveStreams.subscribe(null);
                return;
            }

            reactiveStreams.subscribe(new ReactiveToFlowSubscriber<>(s));
        }
    }

    /**
     * Wraps a Reactive Streams Subscriber and forwards methods of the Flow Subscriber to it.
     */
    private record FlowToReactiveSubscriber<T>(Subscriber<? super T> reactiveStreams) implements Flow.Subscriber<T> {
        @Override
        public void onComplete() {
            reactiveStreams.onComplete();
        }

        @Override
        public void onError(final Throwable throwable) {
            reactiveStreams.onError(throwable);
        }

        @Override
        public void onNext(final T item) {
            reactiveStreams.onNext(item);
        }

        @Override
        public void onSubscribe(final Flow.Subscription subscription) {
            reactiveStreams.onSubscribe(new ReactiveToFlowSubscription(subscription));
        }
    }

    /**
     * Wraps a Reactive Streams Subscription and converts the calls to a Flow Subscription.
     */
    private record FlowToReactiveSubscription(Subscription reactiveStreams) implements Flow.Subscription {
        @Override
        public void cancel() {
            reactiveStreams.cancel();
        }

        @Override
        public void request(final long n) {
            reactiveStreams.request(n);
        }
    }

    /**
     * Reactive Streams Publisher that wraps a Flow Publisher.
     */
    private record ReactivePublisherFromFlow<T>(Flow.Publisher<? extends T> flow) implements Publisher<T> {
        @Override
        public void subscribe(final Subscriber<? super T> reactive) {
            if (reactive == null) {
                flow.subscribe(null);
                return;
            }

            flow.subscribe(new FlowToReactiveSubscriber<>(reactive));
        }
    }

    /**
     * Wraps a Flow Processor and forwards methods of the Reactive Streams Processor to it.
     *
     * @param <T> the input type
     * @param <U> the output type
     */
    private record ReactiveToFlowProcessor<T, U>(Flow.Processor<? super T, ? extends U> flow) implements Processor<T, U> {
        @Override
        public void onComplete() {
            flow.onComplete();
        }

        @Override
        public void onError(final Throwable t) {
            flow.onError(t);
        }

        @Override
        public void onNext(final T t) {
            flow.onNext(t);
        }

        @Override
        public void onSubscribe(final Subscription s) {
            flow.onSubscribe(new FlowToReactiveSubscription(s));
        }

        @Override
        public void subscribe(final Subscriber<? super U> s) {
            if (s == null) {
                flow.subscribe(null);
                return;
            }

            flow.subscribe(new FlowToReactiveSubscriber<>(s));
        }
    }

    /**
     * Wraps a Reactive Streams Subscriber and forwards methods of the Flow Subscriber to it.
     */
    private record ReactiveToFlowSubscriber<T>(Flow.Subscriber<? super T> flow) implements Subscriber<T> {
        @Override
        public void onComplete() {
            flow.onComplete();
        }

        @Override
        public void onError(final Throwable throwable) {
            flow.onError(throwable);
        }

        @Override
        public void onNext(final T item) {
            flow.onNext(item);
        }

        @Override
        public void onSubscribe(final Subscription subscription) {
            flow.onSubscribe(new FlowToReactiveSubscription(subscription));
        }
    }

    /**
     * Wraps a Flow Subscription and converts the calls to a Reactive Streams Subscription.
     */
    private record ReactiveToFlowSubscription(Flow.Subscription flow) implements Subscription {

        @Override
        public void cancel() {
            flow.cancel();
        }

        @Override
        public void request(final long n) {
            flow.request(n);
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
        return switch (reactiveStreamsProcessor) {
            case null -> throw new NullPointerException("reactiveStreamsProcessor");
            case final Flow.Processor processor -> (Flow.Processor<T, U>) reactiveStreamsProcessor;
            case final ReactiveToFlowProcessor reactiveToFlowProcessor -> (Flow.Processor<T, U>) ((ReactiveToFlowProcessor<T, U>) reactiveStreamsProcessor).flow;
            default -> new FlowToReactiveProcessor<>(reactiveStreamsProcessor);
        };
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
        return switch (reactiveStreamsPublisher) {
            case null -> throw new NullPointerException("reactiveStreamsPublisher");
            case final Flow.Publisher publisher -> (Flow.Publisher<T>) reactiveStreamsPublisher;
            case final ReactivePublisherFromFlow reactivePublisherFromFlow -> (Flow.Publisher<T>) ((ReactivePublisherFromFlow<T>) reactiveStreamsPublisher).flow;
            default -> new FlowPublisherFromReactive<>(reactiveStreamsPublisher);
        };
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
        return switch (flowProcessor) {
            case null -> throw new NullPointerException("flowProcessor");
            case final Processor processor -> (Processor<T, U>) flowProcessor;
            case final FlowToReactiveProcessor flowToReactiveProcessor -> (Processor<T, U>) ((FlowToReactiveProcessor<T, U>) flowProcessor).reactiveStreams;
            default -> new ReactiveToFlowProcessor<>(flowProcessor);
        };
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
        return switch (flowPublisher) {
            case null -> throw new NullPointerException("flowPublisher");
            case final Publisher publisher -> (Publisher<T>) flowPublisher;
            case final FlowPublisherFromReactive flowPublisherFromReactive -> (Publisher<T>) ((FlowPublisherFromReactive<T>) flowPublisher).reactiveStreams;
            default -> new ReactivePublisherFromFlow<>(flowPublisher);
        };
    }

    private ReactiveStreamsFlowBridge() {
        super();

        throw new IllegalStateException("No instances!");
    }
}
