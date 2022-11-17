// Created: 16.11.22
package de.freese.base.core.collection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.stream.StreamSupport;

/**
 * @author Thomas Freese
 */
public final class PartitionIterable<T> implements Iterable<List<T>>
{
    /**
     * Partitioniert die Collection anhand der gewünschten Anzahl von Partitionen.<br>
     * Die Reihenfolge der Elemente bleibt erhalten.<br>
     * Die letzte Partition kann kleiner als die anderen sein.<br>
     * Je nach Länge der Collection kann die effektive Anzahl an Partitionen kleiner als gefordert sein.
     */
    public static <T> PartitionIterable<T> ofPartitionCount(Collection<T> origin, int partitionCount)
    {
        final List<T> originList;

        if (origin instanceof List<T> list)
        {
            originList = list;
        }
        else
        {
            originList = origin == null ? Collections.emptyList() : new ArrayList<>(origin);
        }

        return new PartitionIterable<>(getPartitionsByCount(originList, partitionCount));
    }

    /**
     * Partitioniert die Collection anhand der gewünschten Anzahl von Partitionen.<br>
     * Die Reihenfolge der Elemente geht verloren.<br>
     * Die letzte Partition kann kleiner als die anderen sein.<br>
     * Je nach Länge der Collection kann die effektive Anzahl an Partitionen kleiner als gefordert sein.
     */
    public static <T> PartitionIterable<T> ofPartitionCountModulo(Collection<T> origin, int partitionCount)
    {
        final List<T> originList;

        if (origin instanceof List<T> list)
        {
            originList = list;
        }
        else
        {
            originList = origin == null ? Collections.emptyList() : new ArrayList<>(origin);
        }

        return new PartitionIterable<>(getPartitionsByCountModulo(originList, partitionCount));
    }

    /**
     * Partitioniert die Collection anhand der gewünschten Länge einer Partition.<br>
     * Die Reihenfolge der Elemente bleibt erhalten.<br>
     * Die letzte Partition kann kleiner als die anderen sein.
     */
    public static <T> PartitionIterable<T> ofPartitionLength(Collection<T> origin, int partitionLength)
    {
        final List<T> originList;

        if (origin instanceof List<T> list)
        {
            originList = list;
        }
        else
        {
            originList = origin == null ? Collections.emptyList() : new ArrayList<>(origin);
        }

        return new PartitionIterable<>(getPartitionsByLength(originList, partitionLength));
    }

    /**
     * Partitioniert die Collection anhand der gewünschten Anzahl von Partitionen.<br>
     * Die Reihenfolge der Elemente bleibt erhalten.<br>
     * Die letzte Partition kann kleiner als die anderen sein.<br>
     * Je nach Länge der Collection kann die effektive Anzahl an Partitionen kleiner als gefordert sein.
     */
    private static <T> List<List<T>> getPartitionsByCount(final List<T> origin, final int partitionCount)
    {
        if ((origin == null) || origin.isEmpty())
        {
            return Collections.emptyList();
        }

        int effectiveCount = Math.min(origin.size(), partitionCount);
        int effectiveLength = origin.size() / effectiveCount;

        int[] partitionSizes = new int[effectiveCount];
        Arrays.fill(partitionSizes, effectiveLength);

        // Die Gesamtgröße der einzelnen Partitionen ggf. anpassen.
        int sum = effectiveCount * effectiveLength;

        if (sum > origin.size())
        {
            // Gesamtgröße der einzelnen Partitionen ist zu groß.
            // Von hinten Index für Index reduzieren bis es passt.
            int index = effectiveCount - 1;

            while (sum > origin.size())
            {
                partitionSizes[index]--;

                sum--;
                index--;
            }
        }
        else if (sum < origin.size())
        {
            // Gesamtgröße der einzelnen Partitionen ist zu klein.
            // Von vorne Index für Index erhöhen bis es passt.
            int index = 0;

            while (sum < origin.size())
            {
                partitionSizes[index]++;

                sum++;
                index++;
            }
        }

        List<List<T>> partitions = new ArrayList<>(effectiveCount);
        int fromIndex = 0;

        for (int partitionSize : partitionSizes)
        {
            partitions.add(origin.subList(fromIndex, fromIndex + partitionSize));

            fromIndex += partitionSize;
        }

        return partitions;
    }

    /**
     * Partitioniert die Collection anhand der gewünschten Anzahl von Partitionen.<br>
     * Die Reihenfolge der Elemente geht verloren.<br>
     * Die letzte Partition kann kleiner als die anderen sein.<br>
     * Je nach Länge der Collection kann die effektive Anzahl an Partitionen kleiner als gefordert sein.
     */
    private static <T> List<List<T>> getPartitionsByCountModulo(final List<T> origin, final int partitionCount)
    {
        if ((origin == null) || origin.isEmpty())
        {
            return Collections.emptyList();
        }

        Map<Integer, List<T>> partitionMap = new HashMap<>();

        for (int i = 0; i < origin.size(); i++)
        {
            T value = origin.get(i);
            int indexToUse = i % partitionCount;

            partitionMap.computeIfAbsent(indexToUse, key -> new ArrayList<>()).add(value);
        }

        return new ArrayList<>(partitionMap.values());
    }

    /**
     * Partitioniert die Collection anhand der gewünschten Länge einer Partition.<br>
     * Die Reihenfolge der Elemente bleibt erhalten.<br>
     * Die letzte Partition kann kleiner als die anderen sein.
     */
    private static <T> List<List<T>> getPartitionsByLength(List<T> origin, int partitionLength)
    {
        if ((origin == null) || origin.isEmpty())
        {
            return Collections.emptyList();
        }

        List<List<T>> batches = new ArrayList<>();
        int fromIndex = 0;

        while (fromIndex < origin.size())
        {
            int offset = Math.min(origin.size() - fromIndex, partitionLength);
            int endIndex = fromIndex + offset;

            batches.add(origin.subList(fromIndex, endIndex));

            fromIndex = endIndex;
        }

        return batches;
    }

    private final List<List<T>> partitions;

    private PartitionIterable(List<List<T>> partitions)
    {
        super();

        this.partitions = Objects.requireNonNull(partitions, "partitions required");
    }

    public List<List<T>> getPartitions()
    {
        //        return partitions;
        return StreamSupport.stream(spliterator(), false).toList();
    }

    @Override
    public Iterator<List<T>> iterator()
    {
        return new Iterator<>()
        {
            private int index = 0;

            @Override
            public boolean hasNext()
            {
                return index < partitions.size();
            }

            @Override
            public List<T> next()
            {
                if (!hasNext())
                {
                    throw new NoSuchElementException();
                }

                List<T> partition = partitions.get(index);

                index++;

                return partition;
            }
        };
    }
}
