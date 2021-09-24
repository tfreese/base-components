// Created: 09.04.2020
package de.freese.base.utils;

import java.util.ArrayList;
import java.util.Arrays;
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
    public static List<List<String>> getPartitions(final List<String> values, final int numberOfPartitions)
    {
        int partitionCount = Math.min(values.size(), numberOfPartitions);
        int partitionLength = values.size() / partitionCount;

        int[] partitionSizes = new int[partitionCount];
        Arrays.fill(partitionSizes, partitionLength);

        int sum = partitionCount * partitionLength;

        // Länge der einzelnen Partitionen ist zu groß.
        // Von hinten Index für Index reduzieren bis es passt.
        int index = partitionCount - 1;

        while (sum > values.size())
        {
            partitionSizes[index]--;

            sum--;
            index--;
        }

        // Länge der einzelnen Partitionen ist zu klein.
        // Von vorne Index für Index erhöhen bis es passt.
        index = 0;

        while (sum < values.size())
        {
            partitionSizes[index]++;

            sum++;
            index++;
        }

        List<List<String>> partitions = new ArrayList<>(partitionCount);
        int fromIndex = 0;

        for (int partitionSize : partitionSizes)
        {
            partitions.add(values.subList(fromIndex, fromIndex + partitionSize));

            fromIndex += partitionSize;
        }

        return partitions;
    }

    /**
     * @param list {@link List}
     *
     * @return boolean
     */
    public static boolean isEmpty(final List<?> list)
    {
        return CollectionUtils.isEmpty(list);
    }

    /**
     * @param list {@link List}
     *
     * @return boolean
     */
    public static boolean isNotEmpty(final List<?> list)
    {
        return CollectionUtils.isNotEmpty(list);
    }

    /**
     * Erstellt ein neues {@link ListUtils} Object.
     */
    private ListUtils()
    {
        super();
    }

    /**
     * Großer Nachteil: Es muss über die gesamte Liste iteriert werden und die Reihenfolge der Elemente ist hinüber.
     *
     * @param values {@link List}
     * @param numberOfPartitions int
     *
     * @return {@link List}
     */
    List<List<String>> getPartitionsByModulo(final List<String> values, final int numberOfPartitions)
    {
        Map<Integer, List<String>> partitionMap = new HashMap<>();

        for (int i = 0; i < values.size(); i++)
        {
            String value = values.get(i);
            int indexToUse = i % numberOfPartitions;

            partitionMap.computeIfAbsent(indexToUse, key -> new ArrayList<>()).add(value);
        }

        return new ArrayList<>(partitionMap.values());
    }
}
