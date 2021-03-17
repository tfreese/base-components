// Created: 03.01.2018
package de.freese.base.core.function;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Diese Util-Klasse dient zur Exception-Behandlung bei Lambdas.<br>
 * Für Checked- und Unchecked-Exceptions stehen unterschiedliche Methoden zur Verfügung.
 *
 * @author Thomas Freese
 */
public final class LambdaExceptions
{
    /**
     * Default Exception-Handler.<br>
     * Exceptions werden als {@link RuntimeException} geworfen.
     */
    private static final ExceptionHandler THROW_RUNTIME_EXCEPTION_HANDLER = ex -> {
        if (ex instanceof RuntimeException)
        {
            throw (RuntimeException) ex;
        }
        else if (ex instanceof IOException)
        {
            throw new UncheckedIOException((IOException) ex);
        }

        throw new RuntimeException(ex);
    };

    /**
     * Kapselt einen {@link ThrowingBiConsumer} in einen {@link BiConsumer}.<br>
     * Exceptions werden als {@link RuntimeException} geworfen.
     *
     * @param exceptionalBiConsumer {@link ThrowingBiConsumer}
     * @return {@link Supplier}
     */
    public static <T, U, E extends Exception> BiConsumer<T, U> toBiConsumer(final ThrowingBiConsumer<T, U, E> exceptionalBiConsumer)
    {
        return toBiConsumer(exceptionalBiConsumer, THROW_RUNTIME_EXCEPTION_HANDLER);
    }

    /**
     * Kapselt einen {@link ThrowingBiConsumer} in einen {@link BiConsumer}.<br>
     * Exceptions werden dem {@link ExceptionHandler} übergeben.
     *
     * @param exceptionalBiConsumer {@link ThrowingBiConsumer}
     * @param handler {@link ExceptionHandler}
     * @return {@link Supplier}
     */
    public static <T, U, E extends Exception> BiConsumer<T, U> toBiConsumer(final ThrowingBiConsumer<T, U, E> exceptionalBiConsumer,
                                                                            final ExceptionHandler handler)
    {
        return (t, u) -> {
            try
            {
                exceptionalBiConsumer.accept(t, u);
            }
            catch (Exception ex)
            {
                handler.handle(ex);
            }
        };
    }

    /**
     * Kapselt eine {@link ThrowingFunction} in eine {@link Function}.<br>
     * Exceptions werden als {@link RuntimeException} geworfen.
     *
     * @param exceptionalFunction {@link ThrowingFunction}
     * @return {@link Function}
     */
    public static <T, U, R, E extends Exception> BiFunction<T, U, R> toBiFunction(final ThrowingBiFunction<T, U, R, E> exceptionalFunction)
    {
        return toBiFunction(exceptionalFunction, THROW_RUNTIME_EXCEPTION_HANDLER);
    }

    /**
     * Kapselt eine {@link ThrowingFunction} in eine {@link Function}.<br>
     * Exceptions werden dem {@link ExceptionHandler} übergeben.
     *
     * @param exceptionalFunction {@link ThrowingConsumer}
     * @param handler {@link ExceptionHandler}
     * @return {@link Consumer}
     */
    public static <T, U, R, E extends Exception> BiFunction<T, U, R> toBiFunction(final ThrowingBiFunction<T, U, R, E> exceptionalFunction,
                                                                                  final ExceptionHandler handler)
    {
        return (t, u) -> {
            try
            {
                return exceptionalFunction.apply(t, u);
            }
            catch (Exception ex)
            {
                handler.handle(ex);
            }

            return null;
        };
    }

    /**
     * Kapselt einen {@link ThrowingConsumer} in einen {@link Consumer}.<br>
     * Exceptions werden als {@link RuntimeException} geworfen.
     *
     * @param exceptionalConsumer {@link ThrowingConsumer}
     * @return {@link Consumer}
     */
    public static <T, E extends Exception> Consumer<T> toConsumer(final ThrowingConsumer<T, E> exceptionalConsumer)
    {
        return toConsumer(exceptionalConsumer, THROW_RUNTIME_EXCEPTION_HANDLER);
    }

    /**
     * Kapselt einen {@link ThrowingConsumer} in einen {@link Consumer}.<br>
     * Exceptions werden dem {@link ExceptionHandler} übergeben.
     *
     * @param exceptionalConsumer {@link ThrowingConsumer}
     * @param handler {@link ExceptionHandler}
     * @return {@link Consumer}
     */
    public static <T, E extends Exception> Consumer<T> toConsumer(final ThrowingConsumer<T, E> exceptionalConsumer, final ExceptionHandler handler)
    {
        return t -> {
            try
            {
                exceptionalConsumer.accept(t);
            }
            catch (Exception ex)
            {
                handler.handle(ex);
            }
        };
    }

    /**
     * Kapselt eine {@link ThrowingFunction} in eine {@link Function}.<br>
     * Exceptions werden als {@link RuntimeException} geworfen.
     *
     * @param exceptionalFunction {@link ThrowingFunction}
     * @return {@link Function}
     */
    public static <T, R, E extends Exception> Function<T, R> toFunction(final ThrowingFunction<T, R, E> exceptionalFunction)
    {
        return toFunction(exceptionalFunction, THROW_RUNTIME_EXCEPTION_HANDLER);
    }

    /**
     * Kapselt eine {@link ThrowingFunction} in eine {@link Function}.<br>
     * Exceptions werden dem {@link ExceptionHandler} übergeben.
     *
     * @param exceptionalFunction {@link ThrowingConsumer}
     * @param handler {@link ExceptionHandler}
     * @return {@link Consumer}
     */
    public static <T, R, E extends Exception> Function<T, R> toFunction(final ThrowingFunction<T, R, E> exceptionalFunction, final ExceptionHandler handler)
    {
        return t -> {
            try
            {
                return exceptionalFunction.apply(t);
            }
            catch (Exception ex)
            {
                handler.handle(ex);
            }

            return null;
        };
    }

    /**
     * Kapselt einen {@link ThrowingPredicate} in einen {@link Predicate}.<br>
     * Exceptions werden als {@link RuntimeException} geworfen.
     *
     * @param exceptionalPredicate {@link ThrowingPredicate}
     * @return {@link Predicate}
     */
    public static <T, E extends Exception> Predicate<T> toPredicate(final ThrowingPredicate<T, E> exceptionalPredicate)
    {
        return toPredicate(exceptionalPredicate, THROW_RUNTIME_EXCEPTION_HANDLER);
    }

    /**
     * Kapselt einen {@link ThrowingPredicate} in einen {@link Predicate}.<br>
     * Exceptions werden dem {@link ExceptionHandler} übergeben.
     *
     * @param exceptionalPredicate {@link ThrowingPredicate}
     * @param handler {@link ExceptionHandler}
     * @return {@link Predicate}
     */
    public static <T, E extends Exception> Predicate<T> toPredicate(final ThrowingPredicate<T, E> exceptionalPredicate, final ExceptionHandler handler)
    {
        return t -> {
            try
            {
                return exceptionalPredicate.test(t);
            }
            catch (Exception ex)
            {
                handler.handle(ex);
            }

            return false;
        };
    }

    /**
     * Kapselt einen {@link ThrowingSupplier} in einen {@link Supplier}.<br>
     * Exceptions werden als {@link RuntimeException} geworfen.
     *
     * @param exceptionalSupplier {@link ThrowingSupplier}
     * @return {@link Supplier}
     */
    public static <R, E extends Exception> Supplier<R> toSupplier(final ThrowingSupplier<R, E> exceptionalSupplier)
    {
        return toSupplier(exceptionalSupplier, THROW_RUNTIME_EXCEPTION_HANDLER);
    }

    /**
     * Kapselt einen {@link ThrowingSupplier} in einen {@link Supplier}.<br>
     * Exceptions werden dem {@link ExceptionHandler} übergeben.
     *
     * @param exceptionalSupplier {@link ThrowingSupplier}
     * @param handler {@link ExceptionHandler}
     * @return {@link Supplier}
     */
    public static <R, E extends Exception> Supplier<R> toSupplier(final ThrowingSupplier<R, E> exceptionalSupplier, final ExceptionHandler handler)
    {
        return () -> {
            try
            {
                return exceptionalSupplier.get();
            }
            catch (Exception ex)
            {
                handler.handle(ex);
            }

            return null;
        };
    }

    /**
     * Erweitert einen {@link BiConsumer} mit ExceptionHandling.<br>
     * Exceptions werden als {@link RuntimeException} geworfen.
     *
     * @param consumer {@link BiConsumer}
     * @return {@link Consumer}
     */
    public static <T, U> BiConsumer<T, U> wrapBiConsumer(final BiConsumer<T, U> consumer)
    {
        return wrapBiConsumer(consumer, THROW_RUNTIME_EXCEPTION_HANDLER);
    }

    /**
     * Erweitert einen {@link BiConsumer} mit ExceptionHandling.<br>
     * Exceptions werden dem {@link ExceptionHandler} übergeben.
     *
     * @param consumer {@link BiConsumer}
     * @param handler {@link ExceptionHandler}
     * @return {@link BiConsumer}
     */
    public static <T, U> BiConsumer<T, U> wrapBiConsumer(final BiConsumer<T, U> consumer, final ExceptionHandler handler)
    {
        return (t, u) -> {
            try
            {
                consumer.accept(t, u);
            }
            catch (Exception ex)
            {
                handler.handle(ex);
            }
        };
    }

    /**
     * Erweitert eine {@link Function} mit ExceptionHandling.<br>
     * Exceptions werden als {@link RuntimeException} geworfen.
     *
     * @param function {@link Function}
     * @return {@link Consumer}
     */
    public static <T, U, R> BiFunction<T, U, R> wrapBiFunction(final BiFunction<T, U, R> function)
    {
        return wrapBiFunction(function, THROW_RUNTIME_EXCEPTION_HANDLER);
    }

    /**
     * Erweitert eine {@link Function} mit ExceptionHandling.<br>
     * Exceptions werden dem {@link ExceptionHandler} übergeben.
     *
     * @param function {@link Function}
     * @param handler {@link ExceptionHandler}
     * @return {@link Consumer}
     */
    public static <T, U, R> BiFunction<T, U, R> wrapBiFunction(final BiFunction<T, U, R> function, final ExceptionHandler handler)
    {
        return (t, u) -> {
            try
            {
                return function.apply(t, u);
            }
            catch (Exception ex)
            {
                handler.handle(ex);
            }

            return null;
        };
    }

    /**
     * Erweitert einen {@link Consumer} mit ExceptionHandling.<br>
     * Exceptions werden als {@link RuntimeException} geworfen.
     *
     * @param consumer {@link Consumer}
     * @return {@link Consumer}
     */
    public static <T> Consumer<T> wrapConsumer(final Consumer<T> consumer)
    {
        return wrapConsumer(consumer, THROW_RUNTIME_EXCEPTION_HANDLER);
    }

    /**
     * Erweitert einen {@link Consumer} mit ExceptionHandling.<br>
     * Exceptions werden dem {@link ExceptionHandler} übergeben.
     *
     * @param consumer {@link Consumer}
     * @param handler {@link ExceptionHandler}
     * @return {@link Consumer}
     */
    public static <T> Consumer<T> wrapConsumer(final Consumer<T> consumer, final ExceptionHandler handler)
    {
        return t -> {
            try
            {
                consumer.accept(t);
            }
            catch (Exception ex)
            {
                handler.handle(ex);
            }
        };
    }

    /**
     * Erweitert eine {@link Function} mit ExceptionHandling.<br>
     * Exceptions werden als {@link RuntimeException} geworfen.
     *
     * @param function {@link Function}
     * @return {@link Consumer}
     */
    public static <T, R> Function<T, R> wrapFunction(final Function<T, R> function)
    {
        return wrapFunction(function, THROW_RUNTIME_EXCEPTION_HANDLER);
    }

    /**
     * Erweitert eine {@link Function} mit ExceptionHandling.<br>
     * Exceptions werden dem {@link ExceptionHandler} übergeben.
     *
     * @param function {@link Function}
     * @param handler {@link ExceptionHandler}
     * @return {@link Consumer}
     */
    public static <T, R> Function<T, R> wrapFunction(final Function<T, R> function, final ExceptionHandler handler)
    {
        return t -> {
            try
            {
                return function.apply(t);
            }
            catch (Exception ex)
            {
                handler.handle(ex);
            }

            return null;
        };
    }

    /**
     * Erweitert eine {@link Predicate} mit ExceptionHandling.<br>
     * Exceptions werden als {@link RuntimeException} geworfen.
     *
     * @param predicate {@link Predicate}
     * @return {@link Consumer}
     */
    public static <T> Predicate<T> wrapPredicate(final Predicate<T> predicate)
    {
        return wrapPredicate(predicate, THROW_RUNTIME_EXCEPTION_HANDLER);
    }

    /**
     * Erweitert eine {@link Predicate} mit ExceptionHandling.<br>
     * Exceptions werden dem {@link ExceptionHandler} übergeben.
     *
     * @param predicate {@link Predicate}
     * @param handler {@link ExceptionHandler}
     * @return {@link Consumer}
     */
    public static <T> Predicate<T> wrapPredicate(final Predicate<T> predicate, final ExceptionHandler handler)
    {
        return t -> {
            try
            {
                return predicate.test(t);
            }
            catch (Exception ex)
            {
                handler.handle(ex);
            }

            return false;
        };
    }

    /**
     * Erweitert einen {@link Supplier} mit ExceptionHandling.<br>
     * Exceptions werden als {@link RuntimeException} geworfen..
     *
     * @param supplier {@link Supplier}
     * @return {@link Consumer}
     */
    public static <R> Supplier<R> wrapSupplier(final Supplier<R> supplier)
    {
        return wrapSupplier(supplier, THROW_RUNTIME_EXCEPTION_HANDLER);
    }

    /**
     * Erweitert einen {@link Supplier} mit ExceptionHandling.<br>
     * Exceptions werden dem {@link ExceptionHandler} übergeben.
     *
     * @param supplier {@link Supplier}
     * @param handler {@link ExceptionHandler}
     * @return {@link Consumer}
     */
    public static <R> Supplier<R> wrapSupplier(final Supplier<R> supplier, final ExceptionHandler handler)
    {
        return () -> {
            try
            {
                return supplier.get();
            }
            catch (Exception ex)
            {
                handler.handle(ex);
            }

            return null;
        };
    }

    /**
     * Erstellt ein neues {@link LambdaExceptions} Object.
     */
    private LambdaExceptions()
    {
        super();
    }
}
