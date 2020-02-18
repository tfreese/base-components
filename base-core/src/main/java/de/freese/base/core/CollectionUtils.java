/**
 * Created: 30.04.2018
 */

package de.freese.base.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import de.freese.base.core.collection.Partition;

/**
 * Nützliches für Colelctions.
 *
 * @author Thomas Freese
 */
public final class CollectionUtils
{
    /**
     * @param list {@link List}
     * @param batchSize int
     * @return int
     */
    private static <T> int getNumberOfPartitions(final List<T> list, final int batchSize)
    {
        return ((list.size() + batchSize) - 1) / batchSize;
    }

    /**
     * Returns consecutive {@link List#subList(int, int) sublists} of a list, each of the same size (the final list may be smaller). For example, partitioning a
     * list containing {@code [a, b, c, d, e]} with a partition size of 3 yields {@code [[a, b, c], [d, e]]} -- an outer list containing two inner lists of
     * three and two elements, all in the original order.
     * <p>
     * The outer list is unmodifiable, but reflects the latest state of the source list. The inner lists are sublist views of the original list, produced on
     * demand using {@link List#subList(int, int)}, and are subject to all the usual caveats about modification as explained in that API.
     * <p>
     * Adapted from http://code.google.com/p/guava-libraries/
     *
     * @param <T> the element type
     * @param list the list to return consecutive sublists of
     * @param batchSize the desired size of each sublist (the last may be smaller)
     * @return a list of consecutive sublists
     * @throws NullPointerException if list is null
     * @throws IllegalArgumentException if size is not strictly positive
     * @since 4.0
     */
    public static <T> List<List<T>> partitionsByApacheCommons(final List<T> list, final int batchSize)
    {
        return new Partition<>(list, batchSize);
    }

    /**
     * Liefert eine List aus n SubLists.
     *
     * @param list {@link List}
     * @param partitions Anzahl der Partitionen
     * @return {@link List}
     */
    public static <T> List<List<T>> partitionsByCount(final List<T> list, final int partitions)
    {
        Objects.requireNonNull(list, "list required");

        if (partitions <= 0)
        {
            throw new IllegalArgumentException("partitions must be greater than 0");
        }

        List<List<T>> partitionList = new ArrayList<>();

        int size = list.size() / partitions;
        int fromIndex = 0;

        for (int p = 0; p < (partitions - 1); p++)
        {
            int toIndex = fromIndex + size;
            List<T> partition = list.subList(fromIndex, toIndex);
            partitionList.add(partition);

            fromIndex = toIndex;
        }

        List<T> partition = list.subList(fromIndex, list.size());
        partitionList.add(partition);

        return partitionList;
    }

    /**
     * Partitioniert eine Liste in SubLists.<br>
     * Jede SubList besitzt eine max. Länge von "batchSize".<br>
     * Die letzte SubList wird kürzer sein, falls die Gesamtlänge nicht durch batchSize teilbar ist.
     *
     * @param list {@link List}
     * @param batchSize int; Anzahl der Elemente pro Partition
     * @return {@link List}
     */
    public static <T> List<List<T>> partitionsBySize(final List<T> list, final int batchSize)
    {
        Objects.requireNonNull(list, "list required");

        if (batchSize <= 0)
        {
            throw new IllegalArgumentException("batchSize must be greater than 0");
        }

        List<List<T>> partitionList = new ArrayList<>();

        int fromIndex = 0;
        int toIndex = batchSize;

        while (toIndex < list.size())
        {
            List<T> partition = list.subList(fromIndex, toIndex);
            partitionList.add(partition);

            fromIndex = toIndex;
            toIndex = fromIndex + batchSize;
        }

        List<T> partition = list.subList(fromIndex, list.size());
        partitionList.add(partition);

        return partitionList;
    }

    /**
     * Partitioniert eine Liste in SubLists durch Streams.<br>
     * Jede SubList besitzt eine max. Länge von "batchSize".<br>
     * Die letzte SubList wird kürzer sein, falls die Gesamtlänge nicht durch batchSize teilbar ist.
     *
     * @param list {@link List}
     * @param batchSize int; Anzahl der Elemente pro Partition
     * @return {@link List} of {@link List}
     */
    public static <T> List<List<T>> partitionsBySizeStream(final List<T> list, final int batchSize)
    {
        Objects.requireNonNull(list, "list required");

        // @formatter:off
        return IntStream.range(0, getNumberOfPartitions(list, batchSize))
                    .mapToObj(i -> list.subList(i * batchSize, Math.min((i + 1) * batchSize, list.size())))
                    .collect(Collectors.toList());
        // @formatter:on
    }
}
