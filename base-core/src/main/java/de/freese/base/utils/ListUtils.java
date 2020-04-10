/**
 * Created: 09.04.2020
 */

package de.freese.base.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import de.freese.base.core.collection.Partition;

/**
 * @author Thomas Freese
 */
public final class ListUtils
{
    /**
     * @param list {@link List}
     * @return boolean
     */
    public static boolean isEmpty(final List<?> list)
    {
        return CollectionUtils.isEmpty(list);
    }

    /**
     * @param list {@link List}
     * @return boolean
     */
    public static boolean isNotEmpty(final List<?> list)
    {
        return CollectionUtils.isNotEmpty(list);
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
     * @param sizeOfPartition the desired size of each sublist (the last may be smaller)
     * @return a list of consecutive sublists
     * @throws NullPointerException if list is null
     * @throws IllegalArgumentException if size is not strictly positive
     * @since 4.0
     */
    public static <T> List<List<T>> partitionsByApacheCommons(final List<T> list, final int sizeOfPartition)
    {
        return new Partition<>(list, sizeOfPartition);
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

    // /**
    // * @param list {@link List}
    // * @param sizeOfPartition int
    // * @return {@link List}
    // */
    // public static <T> List<List<T>> partitionsByGuava(final List<T> list, final int sizeOfPartition)
    // {
    // return Lists.partition(list, sizeOfPartition);
    // }

    /**
     * Partitioniert eine Liste in SubLists.<br>
     * Jede SubList besitzt eine max. Länge von "sizeOfPartition".<br>
     * Die letzte SubList wird kürzer sein, falls die Gesamtlänge nicht durch sizeOfPartition teilbar ist.
     *
     * @param list {@link List}
     * @param sizeOfPartition int; Anzahl der Elemente pro Partition
     * @return {@link List}
     */
    public static <T> List<List<T>> partitionsBySize(final List<T> list, final int sizeOfPartition)
    {
        Objects.requireNonNull(list, "list required");

        if (sizeOfPartition <= 0)
        {
            throw new IllegalArgumentException("sizeOfPartition must be greater than 0");
        }

        List<List<T>> partitionList = new ArrayList<>();

        int fromIndex = 0;
        int toIndex = sizeOfPartition;

        while (toIndex < list.size())
        {
            List<T> partition = list.subList(fromIndex, toIndex);
            partitionList.add(partition);

            fromIndex = toIndex;
            toIndex = fromIndex + sizeOfPartition;
        }

        List<T> partition = list.subList(fromIndex, list.size());
        partitionList.add(partition);

        return partitionList;
    }

    /**
     * Partitioniert eine Liste in SubLists durch Streams.<br>
     * Jede SubList besitzt eine max. Länge von "sizeOfPartition".<br>
     * Die letzte SubList wird kürzer sein, falls die Gesamtlänge nicht durch sizeOfPartition teilbar ist.
     *
     * @param list {@link List}
     * @param sizeOfPartition int; Anzahl der Elemente pro Partition
     * @return {@link List} of {@link List}
     */
    public static <T> List<List<T>> partitionsBySizeStream(final List<T> list, final int sizeOfPartition)
    {
        Objects.requireNonNull(list, "list required");

        // @formatter:off
        return IntStream.range(0, CollectionUtils.getNumberOfPartitions(list, sizeOfPartition))
                    .mapToObj(i -> list.subList(i * sizeOfPartition, Math.min((i + 1) * sizeOfPartition, list.size())))
                    .collect(Collectors.toList());
        // @formatter:on
    }

    /**
     * Erstellt ein neues {@link ListUtils} Object.
     */
    private ListUtils()
    {
        super();
    }
}
