// Created: 03.01.2018
package de.freese.base.core.function;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.function.BiConsumer;
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
     * Exceptions werden mit "throws" geworfen.
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
     * Kapselt einen {@link ExceptionalBiConsumer} in einen {@link BiConsumer}.<br>
     * Exceptions werden mit "throws" geworfen.
     *
     * @param exceptionalBiConsumer {@link ExceptionalBiConsumer}
     * @return {@link Supplier}
     */
    public static <T, U, E extends Exception> BiConsumer<T, U> checkedBiConsumer(final ExceptionalBiConsumer<T, U, E> exceptionalBiConsumer)
    {
        return checkedBiConsumer(exceptionalBiConsumer, THROW_RUNTIME_EXCEPTION_HANDLER);
    }

    /**
     * Kapselt einen {@link ExceptionalBiConsumer} in einen {@link BiConsumer}.<br>
     * Exceptions werden dem {@link ExceptionHandler} übergeben.
     *
     * @param exceptionalBiConsumer {@link ExceptionalBiConsumer}
     * @param handler {@link ExceptionHandler}
     * @return {@link Supplier}
     */
    public static <T, U, E extends Exception> BiConsumer<T, U> checkedBiConsumer(final ExceptionalBiConsumer<T, U, E> exceptionalBiConsumer,
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
     * Kapselt einen {@link ExceptionalConsumer} in einen {@link Consumer}.<br>
     * Exceptions werden mit "throws" geworfen.
     *
     * @param exceptionalConsumer {@link ExceptionalConsumer}
     * @return {@link Consumer}
     */
    public static <T, E extends Exception> Consumer<T> checkedConsumer(final ExceptionalConsumer<T, E> exceptionalConsumer)
    {
        return checkedConsumer(exceptionalConsumer, THROW_RUNTIME_EXCEPTION_HANDLER);
    }

    /**
     * Kapselt einen {@link ExceptionalConsumer} in einen {@link Consumer}.<br>
     * Exceptions werden dem {@link ExceptionHandler} übergeben.
     *
     * @param exceptionalConsumer {@link ExceptionalConsumer}
     * @param handler {@link ExceptionHandler}
     * @return {@link Consumer}
     */
    public static <T, E extends Exception> Consumer<T> checkedConsumer(final ExceptionalConsumer<T, E> exceptionalConsumer, final ExceptionHandler handler)
    {
        return i -> {
            try
            {
                exceptionalConsumer.accept(i);
            }
            catch (Exception ex)
            {
                handler.handle(ex);
            }
        };
    }

    /**
     * Kapselt eine {@link ExceptionalFunction} in eine {@link Function}.<br>
     * Exceptions werden mit "throws" geworfen.
     *
     * @param exceptionalFunction {@link ExceptionalFunction}
     * @return {@link Function}
     */
    public static <T, R, E extends Exception> Function<T, R> checkedFunction(final ExceptionalFunction<T, R, E> exceptionalFunction)
    {
        return checkedFunction(exceptionalFunction, THROW_RUNTIME_EXCEPTION_HANDLER);
    }

    /**
     * Kapselt eine {@link ExceptionalFunction} in eine {@link Function}.<br>
     * Exceptions werden dem {@link ExceptionHandler} übergeben.
     *
     * @param exceptionalFunction {@link ExceptionalConsumer}
     * @param handler {@link ExceptionHandler}
     * @return {@link Consumer}
     */
    public static <T, R, E extends Exception> Function<T, R> checkedFunction(final ExceptionalFunction<T, R, E> exceptionalFunction,
                                                                             final ExceptionHandler handler)
    {
        return i -> {
            try
            {
                return exceptionalFunction.apply(i);
            }
            catch (Exception ex)
            {
                handler.handle(ex);
            }

            return null;
        };
    }

    /**
     * Kapselt einen {@link ExceptionalPredicate} in einen {@link Predicate}.<br>
     * Exceptions werden mit "throws" geworfen.
     *
     * @param exceptionalPredicate {@link ExceptionalPredicate}
     * @return {@link Predicate}
     */
    public static <T, E extends Exception> Predicate<T> checkedPredicate(final ExceptionalPredicate<T, E> exceptionalPredicate)
    {
        return checkedPredicate(exceptionalPredicate, THROW_RUNTIME_EXCEPTION_HANDLER);
    }

    /**
     * Kapselt einen {@link ExceptionalPredicate} in einen {@link Predicate}.<br>
     * Exceptions werden dem {@link ExceptionHandler} übergeben.
     *
     * @param exceptionalPredicate {@link ExceptionalPredicate}
     * @param handler {@link ExceptionHandler}
     * @return {@link Predicate}
     */
    public static <T, E extends Exception> Predicate<T> checkedPredicate(final ExceptionalPredicate<T, E> exceptionalPredicate, final ExceptionHandler handler)
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
     * Kapselt einen {@link ExceptionalSupplier} in einen {@link Supplier}.<br>
     * Exceptions werden mit "throws" geworfen.
     *
     * @param exceptionalSupplier {@link ExceptionalSupplier}
     * @return {@link Supplier}
     */
    public static <R, E extends Exception> Supplier<R> checkedSupplier(final ExceptionalSupplier<R, E> exceptionalSupplier)
    {
        return checkedSupplier(exceptionalSupplier, THROW_RUNTIME_EXCEPTION_HANDLER);
    }

    /**
     * Kapselt einen {@link ExceptionalSupplier} in einen {@link Supplier}.<br>
     * Exceptions werden dem {@link ExceptionHandler} übergeben.
     *
     * @param exceptionalSupplier {@link ExceptionalSupplier}
     * @param handler {@link ExceptionHandler}
     * @return {@link Supplier}
     */
    public static <R, E extends Exception> Supplier<R> checkedSupplier(final ExceptionalSupplier<R, E> exceptionalSupplier, final ExceptionHandler handler)
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
     * Exceptions werden mit "throws" geworfen.
     *
     * @param consumer {@link BiConsumer}
     * @return {@link Consumer}
     */
    public static <T, U> BiConsumer<T, U> uncheckedBiConsumer(final BiConsumer<T, U> consumer)
    {
        return uncheckedBiConsumer(consumer, THROW_RUNTIME_EXCEPTION_HANDLER);
    }

    /**
     * Erweitert einen {@link BiConsumer} mit ExceptionHandling.<br>
     * Exceptions werden dem {@link ExceptionHandler} übergeben.
     *
     * @param consumer {@link BiConsumer}
     * @param handler {@link ExceptionHandler}
     * @return {@link BiConsumer}
     */
    public static <T, U> BiConsumer<T, U> uncheckedBiConsumer(final BiConsumer<T, U> consumer, final ExceptionHandler handler)
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
     * Erweitert einen {@link Consumer} mit ExceptionHandling.<br>
     * Exceptions werden mit "throws" geworfen.
     *
     * @param consumer {@link Consumer}
     * @return {@link Consumer}
     */
    public static <T> Consumer<T> uncheckedConsumer(final Consumer<T> consumer)
    {
        return uncheckedConsumer(consumer, THROW_RUNTIME_EXCEPTION_HANDLER);
    }

    /**
     * Erweitert einen {@link Consumer} mit ExceptionHandling.<br>
     * Exceptions werden dem {@link ExceptionHandler} übergeben.
     *
     * @param consumer {@link Consumer}
     * @param handler {@link ExceptionHandler}
     * @return {@link Consumer}
     */
    public static <T> Consumer<T> uncheckedConsumer(final Consumer<T> consumer, final ExceptionHandler handler)
    {
        return i -> {
            try
            {
                consumer.accept(i);
            }
            catch (Exception ex)
            {
                handler.handle(ex);
            }
        };
    }

    /**
     * Erweitert eine {@link Function} mit ExceptionHandling.<br>
     * Exceptions werden mit "throws" geworfen.
     *
     * @param function {@link Function}
     * @return {@link Consumer}
     */
    public static <T, R> Function<T, R> uncheckedFunction(final Function<T, R> function)
    {
        return uncheckedFunction(function, THROW_RUNTIME_EXCEPTION_HANDLER);
    }

    /**
     * Erweitert eine {@link Function} mit ExceptionHandling.<br>
     * Exceptions werden dem {@link ExceptionHandler} übergeben.
     *
     * @param function {@link Function}
     * @param handler {@link ExceptionHandler}
     * @return {@link Consumer}
     */
    public static <T, R> Function<T, R> uncheckedFunction(final Function<T, R> function, final ExceptionHandler handler)
    {
        return i -> {
            try
            {
                return function.apply(i);
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
     * Exceptions werden mit "throws" geworfen.
     *
     * @param predicate {@link Predicate}
     * @return {@link Consumer}
     */
    public static <T> Predicate<T> uncheckedPredicate(final Predicate<T> predicate)
    {
        return uncheckedPredicate(predicate, THROW_RUNTIME_EXCEPTION_HANDLER);
    }

    /**
     * Erweitert eine {@link Predicate} mit ExceptionHandling.<br>
     * Exceptions werden dem {@link ExceptionHandler} übergeben.
     *
     * @param predicate {@link Predicate}
     * @param handler {@link ExceptionHandler}
     * @return {@link Consumer}
     */
    public static <T> Predicate<T> uncheckedPredicate(final Predicate<T> predicate, final ExceptionHandler handler)
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
     * Exceptions werden mit "throws" geworfen.
     *
     * @param supplier {@link Supplier}
     * @return {@link Consumer}
     */
    public static <R> Supplier<R> uncheckedSupplier(final Supplier<R> supplier)
    {
        return uncheckedSupplier(supplier, THROW_RUNTIME_EXCEPTION_HANDLER);
    }

    /**
     * Erweitert einen {@link Supplier} mit ExceptionHandling.<br>
     * Exceptions werden dem {@link ExceptionHandler} übergeben.
     *
     * @param supplier {@link Supplier}
     * @param handler {@link ExceptionHandler}
     * @return {@link Consumer}
     */
    public static <R> Supplier<R> uncheckedSupplier(final Supplier<R> supplier, final ExceptionHandler handler)
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

    // /**
    // * @param args String[]
    // */
    // public static void main(final String[] args)
    // {
    // List<Integer> integers = Arrays.asList(3, 9, 7, 0, 10, 20);
    //
    // // integers.forEach(uncheckedConsumer(i -> System.out.println(50 / i), ArithmeticException.class));
    // integers.forEach(
    // uncheckedConsumer(i -> System.out.println(50 / i), th -> System.err.println("\nException occured : " + th.getMessage())));
    // // integers.forEach(uncheckedConsumer(i -> System.out.println(50 / i)));
    // // integers.forEach(checkedConsumer(i -> System.out.println(50 / i)));
    //
    // integers.stream().map(checkedFunction(i -> (50 / i), th -> System.err.println("\nException occured : " + th.getMessage())))
    // .forEach(System.err::println);
    // }

    // /**
    // * @param consumer {@link Consumer}
    // * @param clazz {@link Class}
    // * @return {@link Consumer}
    // */
    // public static <T, E extends Exception> Consumer<T> uncheckedConsumer(final Consumer<T> consumer, final Class<E> clazz)
    // {
    // return uncheckedConsumer(consumer, ex ->
    // {
    // try
    // {
    // E exCast = clazz.cast(ex);
    // System.err.println("\nException occured : " + exCast.getMessage());
    // }
    // catch (ClassCastException ccEx)
    // {
    // throw ccEx;
    // }
    // catch (RuntimeException rtEx)
    // {
    // throw rtEx;
    // }
    // });
    // }
}
