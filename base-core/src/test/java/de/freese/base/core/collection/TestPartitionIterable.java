// Created: 16.11.22
package de.freese.base.core.collection;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * @author Thomas Freese
 */
class TestPartitionIterable {
    @Test
    void testPartitionCount() {
        // null
        PartitionIterable<Integer> partitionIterable = PartitionIterable.ofPartitionCount(null, 2);
        List<List<Integer>> partitions = partitionIterable.getPartitions();

        assertEquals(0, partitions.size());

        // 0
        partitionIterable = PartitionIterable.ofPartitionCount(Collections.emptyList(), 2);
        partitions = partitionIterable.getPartitions();

        assertEquals(0, partitions.size());

        // 1
        partitionIterable = PartitionIterable.ofPartitionCount(List.of(0), 2);
        partitions = partitionIterable.getPartitions();

        assertEquals(1, partitions.size());
        assertEquals(1, partitions.get(0).size());
        assertEquals("[0]", partitions.get(0).toString());

        // 2
        partitionIterable = PartitionIterable.ofPartitionCount(List.of(0, 1), 2);
        partitions = partitionIterable.getPartitions();

        assertEquals(2, partitions.size());
        assertEquals(1, partitions.get(0).size());
        assertEquals(1, partitions.get(1).size());
        assertEquals("[0]", partitions.get(0).toString());
        assertEquals("[1]", partitions.get(1).toString());

        // 3
        partitionIterable = PartitionIterable.ofPartitionCount(List.of(0, 1, 2), 2);
        partitions = partitionIterable.getPartitions();

        assertEquals(2, partitions.size());
        assertEquals(2, partitions.get(0).size());
        assertEquals(1, partitions.get(1).size());
        assertEquals("[0, 1]", partitions.get(0).toString());
        assertEquals("[2]", partitions.get(1).toString());

        // 4
        partitionIterable = PartitionIterable.ofPartitionCount(List.of(0, 1, 2, 3), 2);
        partitions = partitionIterable.getPartitions();

        assertEquals(2, partitions.size());
        assertEquals(2, partitions.get(0).size());
        assertEquals(2, partitions.get(1).size());
        assertEquals("[0, 1]", partitions.get(0).toString());
        assertEquals("[2, 3]", partitions.get(1).toString());

        // 5
        partitionIterable = PartitionIterable.ofPartitionCount(List.of(0, 1, 2, 3, 4), 2);
        partitions = partitionIterable.getPartitions();

        assertEquals(2, partitions.size());
        assertEquals(3, partitions.get(0).size());
        assertEquals(2, partitions.get(1).size());
        assertEquals("[0, 1, 2]", partitions.get(0).toString());
        assertEquals("[3, 4]", partitions.get(1).toString());

        // 6
        partitionIterable = PartitionIterable.ofPartitionCount(List.of(0, 1, 2, 3, 4, 5), 2);
        partitions = partitionIterable.getPartitions();

        assertEquals(2, partitions.size());
        assertEquals(3, partitions.get(0).size());
        assertEquals(3, partitions.get(1).size());
        assertEquals("[0, 1, 2]", partitions.get(0).toString());
        assertEquals("[3, 4, 5]", partitions.get(1).toString());
    }

    @Test
    void testPartitionCountModulo() {
        // null
        PartitionIterable<Integer> partitionIterable = PartitionIterable.ofPartitionCountModulo(null, 2);
        List<List<Integer>> partitions = partitionIterable.getPartitions();

        assertEquals(0, partitions.size());

        // 0
        partitionIterable = PartitionIterable.ofPartitionCountModulo(Collections.emptyList(), 2);
        partitions = partitionIterable.getPartitions();

        assertEquals(0, partitions.size());

        // 1
        partitionIterable = PartitionIterable.ofPartitionCountModulo(List.of(0), 2);
        partitions = partitionIterable.getPartitions();

        assertEquals(1, partitions.size());
        assertEquals(1, partitions.get(0).size());
        assertEquals("[0]", partitions.get(0).toString());

        // 2
        partitionIterable = PartitionIterable.ofPartitionCountModulo(List.of(0, 1), 2);
        partitions = partitionIterable.getPartitions();

        assertEquals(2, partitions.size());
        assertEquals(1, partitions.get(0).size());
        assertEquals(1, partitions.get(1).size());
        assertEquals("[0]", partitions.get(0).toString());
        assertEquals("[1]", partitions.get(1).toString());

        // 3
        partitionIterable = PartitionIterable.ofPartitionCountModulo(List.of(0, 1, 2), 2);
        partitions = partitionIterable.getPartitions();

        assertEquals(2, partitions.size());
        assertEquals(2, partitions.get(0).size());
        assertEquals(1, partitions.get(1).size());
        assertEquals("[0, 2]", partitions.get(0).toString());
        assertEquals("[1]", partitions.get(1).toString());

        // 4
        partitionIterable = PartitionIterable.ofPartitionCountModulo(List.of(0, 1, 2, 3), 2);
        partitions = partitionIterable.getPartitions();

        assertEquals(2, partitions.size());
        assertEquals(2, partitions.get(0).size());
        assertEquals(2, partitions.get(1).size());
        assertEquals("[0, 2]", partitions.get(0).toString());
        assertEquals("[1, 3]", partitions.get(1).toString());

        // 5
        partitionIterable = PartitionIterable.ofPartitionCountModulo(List.of(0, 1, 2, 3, 4), 2);
        partitions = partitionIterable.getPartitions();

        assertEquals(2, partitions.size());
        assertEquals(3, partitions.get(0).size());
        assertEquals(2, partitions.get(1).size());
        assertEquals("[0, 2, 4]", partitions.get(0).toString());
        assertEquals("[1, 3]", partitions.get(1).toString());

        // 6
        partitionIterable = PartitionIterable.ofPartitionCountModulo(List.of(0, 1, 2, 3, 4, 5), 2);
        partitions = partitionIterable.getPartitions();

        assertEquals(2, partitions.size());
        assertEquals(3, partitions.get(0).size());
        assertEquals(3, partitions.get(1).size());
        assertEquals("[0, 2, 4]", partitions.get(0).toString());
        assertEquals("[1, 3, 5]", partitions.get(1).toString());
    }

    @Test
    void testPartitionLength() {
        // null
        PartitionIterable<Integer> partitionIterable = PartitionIterable.ofPartitionLength(null, 2);
        List<List<Integer>> partitions = partitionIterable.getPartitions();

        assertEquals(0, partitions.size());

        // 0
        partitionIterable = PartitionIterable.ofPartitionLength(Collections.emptyList(), 2);
        partitions = partitionIterable.getPartitions();

        assertEquals(0, partitions.size());

        // 1
        partitionIterable = PartitionIterable.ofPartitionLength(List.of(0), 2);
        partitions = partitionIterable.getPartitions();

        assertEquals(1, partitions.size());
        assertEquals(1, partitions.get(0).size());
        assertEquals("[0]", partitions.get(0).toString());

        // 2
        partitionIterable = PartitionIterable.ofPartitionLength(List.of(0, 1), 2);
        partitions = partitionIterable.getPartitions();

        assertEquals(1, partitions.size());
        assertEquals(2, partitions.get(0).size());
        assertEquals("[0, 1]", partitions.get(0).toString());

        // 3
        partitionIterable = PartitionIterable.ofPartitionLength(List.of(0, 1, 2), 2);
        partitions = partitionIterable.getPartitions();

        assertEquals(2, partitions.size());
        assertEquals(2, partitions.get(0).size());
        assertEquals(1, partitions.get(1).size());
        assertEquals("[0, 1]", partitions.get(0).toString());
        assertEquals("[2]", partitions.get(1).toString());

        // 4
        partitionIterable = PartitionIterable.ofPartitionLength(List.of(0, 1, 2, 3), 2);
        partitions = partitionIterable.getPartitions();

        assertEquals(2, partitions.size());
        assertEquals(2, partitions.get(0).size());
        assertEquals(2, partitions.get(1).size());
        assertEquals("[0, 1]", partitions.get(0).toString());
        assertEquals("[2, 3]", partitions.get(1).toString());

        // 5
        partitionIterable = PartitionIterable.ofPartitionLength(List.of(0, 1, 2, 3, 4), 2);
        partitions = partitionIterable.getPartitions();

        assertEquals(3, partitions.size());
        assertEquals(2, partitions.get(0).size());
        assertEquals(2, partitions.get(1).size());
        assertEquals(1, partitions.get(2).size());
        assertEquals("[0, 1]", partitions.get(0).toString());
        assertEquals("[2, 3]", partitions.get(1).toString());
        assertEquals("[4]", partitions.get(2).toString());

        // 6
        partitionIterable = PartitionIterable.ofPartitionLength(List.of(0, 1, 2, 3, 4, 5), 2);
        partitions = partitionIterable.getPartitions();

        assertEquals(3, partitions.size());
        assertEquals(2, partitions.get(0).size());
        assertEquals(2, partitions.get(1).size());
        assertEquals(2, partitions.get(2).size());
        assertEquals("[0, 1]", partitions.get(0).toString());
        assertEquals("[2, 3]", partitions.get(1).toString());
        assertEquals("[4, 5]", partitions.get(2).toString());
    }
}
