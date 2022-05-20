// Created: 09.04.2020
package de.freese.base.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Thomas Freese
 */
public final class ListUtils
{
    /**
     * Aufsplitten der {@link List} in N Partitionen.<br>
     * Ist die Liste kleiner als N, gibt es pro Element eine SubList.
     *
     * @param values {@link List}
     * @param numberOfPartitions int
     *
     * @return {@link List}
     */
    public static <T> List<List<T>> getPartitions(final List<T> values, final int numberOfPartitions)
    {
        if (values == null || values.isEmpty())
        {
            return Collections.emptyList();
        }

        int partitionCount = Math.min(values.size(), numberOfPartitions);
        int partitionLength = values.size() / partitionCount;

        int[] partitionSizes = new int[partitionCount];
        Arrays.fill(partitionSizes, partitionLength);

        // Die Gesamtgröße der einzelnen Partitionen ggf. anpasssen.
        int sum = partitionCount * partitionLength;

        if (sum > values.size())
        {
            // Gesamtgröße der einzelnen Partitionen ist zu groß.
            // Von hinten Index für Index reduzieren bis es passt.
            int index = partitionCount - 1;

            while (sum > values.size())
            {
                partitionSizes[index]--;

                sum--;
                index--;
            }
        }
        else if (sum < values.size())
        {
            // Gesamtgröße der einzelnen Partitionen ist zu klein.
            // Von vorne Index für Index erhöhen bis es passt.
            int index = 0;

            while (sum < values.size())
            {
                partitionSizes[index]++;

                sum++;
                index++;
            }
        }

        List<List<T>> partitions = new ArrayList<>(partitionCount);
        int fromIndex = 0;

        for (int partitionSize : partitionSizes)
        {
            partitions.add(values.subList(fromIndex, fromIndex + partitionSize));

            fromIndex += partitionSize;
        }

        return partitions;
    }

    public static <T> List<List<T>> getPartitionsByBatches(List<T> values, int batchSize)
    {
        if (values == null || values.isEmpty())
        {
            return Collections.emptyList();
        }

        List<List<T>> batches = new ArrayList<>();
        int fromIndex = 0;

        while (fromIndex < values.size())
        {
            int offset = Math.min(values.size() - fromIndex, batchSize);
            int endIndex = fromIndex + offset;

            batches.add(values.subList(fromIndex, endIndex));

            fromIndex = endIndex;
        }

        return batches;
    }

    /**
     * Großer Nachteil: Es muss über die gesamte Liste iteriert werden und die Reihenfolge der Elemente ist hinüber.
     *
     * @param values {@link List}
     * @param numberOfPartitions int
     *
     * @return {@link List}
     */
    public static <T> List<List<T>> getPartitionsByModulo(final List<T> values, final int numberOfPartitions)
    {
        if (values == null || values.isEmpty())
        {
            return Collections.emptyList();
        }

        Map<Integer, List<T>> partitionMap = new HashMap<>();

        for (int i = 0; i < values.size(); i++)
        {
            T value = values.get(i);
            int indexToUse = i % numberOfPartitions;

            partitionMap.computeIfAbsent(indexToUse, key -> new ArrayList<>()).add(value);
        }

        return new ArrayList<>(partitionMap.values());
    }

    /**
     * Erstellt ein neues {@link ListUtils} Object.
     */
    private ListUtils()
    {
        super();
    }
}
