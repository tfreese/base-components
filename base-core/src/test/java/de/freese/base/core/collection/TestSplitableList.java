/**
 * Created: 16.09.2016
 */

package de.freese.base.core.collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.StringJoiner;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import de.freese.base.core.collection.stream.DefaultSplitableArray;
import de.freese.base.core.collection.stream.DefaultSplitableList;
import de.freese.base.core.collection.stream.GenericSplitableList;
import de.freese.base.core.collection.stream.SplitableList;

/**
 * TestCase für die {@link SplitableList} Implementierungen.<br>
 * Für TestCases zum Ändern der Daten wird ein Supplier für die Listen werwendet.
 *
 * @author Thomas Freese
 */
@TestMethodOrder(MethodOrderer.Alphanumeric.class)
class TestSplitableList
{
    /**
     *
     */
    private static final Function<Integer, String> FNC_INT_TO_STRING = (n) -> Integer.toString(n);

    /**
     * Summe 55
     */
    private static final Supplier<Integer[]> INTEGERS_SUPPLIER = () -> new Integer[]
    {
            1, 2, 3, 4, 5, 6, 7, 8, 9, 10
    };

    /**
     *
     */
    private static final List<Arguments> SPLITTERATORS = new ArrayList<>();

    /**
     *
     */
    @BeforeAll
    static void beforeAll()
    {
        Supplier<SplitableList<Integer>> supplierArray = () -> new DefaultSplitableArray<>(INTEGERS_SUPPLIER.get());
        Supplier<SplitableList<Integer>> supplierList = () -> new DefaultSplitableList<>(Arrays.asList(INTEGERS_SUPPLIER.get()));
        Supplier<SplitableList<Integer>> supplierGenericArray = () -> new GenericSplitableList<>(INTEGERS_SUPPLIER.get());
        Supplier<SplitableList<Integer>> supplierGenericList = () -> new GenericSplitableList<>(Arrays.asList(INTEGERS_SUPPLIER.get()));

        SPLITTERATORS.add(Arguments.of(DefaultSplitableArray.class.getSimpleName(), supplierArray));
        SPLITTERATORS.add(Arguments.of(DefaultSplitableList.class.getSimpleName(), supplierList));
        SPLITTERATORS.add(Arguments.of(DefaultSplitableList.class.getSimpleName() + "-Array", supplierGenericArray));
        SPLITTERATORS.add(Arguments.of(DefaultSplitableList.class.getSimpleName() + "-List", supplierGenericList));
    }

    /**
     * @return {@link Stream}
     */
    static Stream<Arguments> getSplitterators()
    {
        return SPLITTERATORS.stream();
    }

    /**
     * @param splitableListName String
     * @param splitableListSupplier {@link Supplier}
     */
    @ParameterizedTest(name = "{0}")
    @MethodSource("getSplitterators")
    void test010List(final String splitableListName, final Supplier<SplitableList<Integer>> splitableListSupplier)
    {
        SplitableList<Integer> list = splitableListSupplier.get();

        assertEquals(10, list.size());

        for (int i = 0; i < list.size(); i++)
        {
            assertEquals(i + 1, list.get(i).intValue());
        }
    }

    /**
     * @param splitableListName String
     * @param splitableListSupplier {@link Supplier}
     */
    @ParameterizedTest(name = "{0}")
    @MethodSource("getSplitterators")
    void test020Iterator(final String splitableListName, final Supplier<SplitableList<Integer>> splitableListSupplier)
    {
        SplitableList<Integer> list = splitableListSupplier.get();

        final StringJoiner joiner1 = new StringJoiner(",");
        list.forEach(i -> joiner1.add(Integer.toString(i)));
        assertEquals("1,2,3,4,5,6,7,8,9,10", joiner1.toString());

        StringJoiner joiner2 = new StringJoiner(",");

        for (Integer integer : list)
        {
            joiner2.add(Integer.toString(integer));
        }

        assertEquals("1,2,3,4,5,6,7,8,9,10", joiner2.toString());
    }

    /**
     * @param splitableListName String
     * @param splitableListSupplier {@link Supplier}
     */
    @ParameterizedTest(name = "{0}")
    @MethodSource("getSplitterators")
    void test030Stream(final String splitableListName, final Supplier<SplitableList<Integer>> splitableListSupplier)
    {
        SplitableList<Integer> list = splitableListSupplier.get();

        assertEquals("1,2,3,4,5,6,7,8,9,10", list.stream().map(FNC_INT_TO_STRING).collect(Collectors.joining(",")));
        assertEquals("1,2,3,4,5,6,7,8,9,10", list.parallelStream().map(FNC_INT_TO_STRING).collect(Collectors.joining(",")));
    }

    /**
     * @param splitableListName String
     * @param splitableListSupplier {@link Supplier}
     */
    @ParameterizedTest(name = "{0}")
    @MethodSource("getSplitterators")
    void test040Stats(final String splitableListName, final Supplier<SplitableList<Integer>> splitableListSupplier)
    {
        SplitableList<Integer> list = splitableListSupplier.get();

        IntSummaryStatistics stats = list.stream().mapToInt(n -> n).summaryStatistics();
        assertEquals(10, stats.getCount());
        assertEquals(55, stats.getSum());
        assertEquals(1, stats.getMin());
        assertEquals(10, stats.getMax());
        assertEquals(5.5D, stats.getAverage(), 0);

        stats = list.parallelStream().mapToInt(n -> n).summaryStatistics();
        assertEquals(10, stats.getCount());
        assertEquals(55, stats.getSum());
        assertEquals(1, stats.getMin());
        assertEquals(10, stats.getMax());
        assertEquals(5.5D, stats.getAverage(), 0);
    }

    /**
     * @param splitableListName String
     * @param splitableListSupplier {@link Supplier}
     */
    @ParameterizedTest(name = "{0}")
    @MethodSource("getSplitterators")
    void test050SubList(final String splitableListName, final Supplier<SplitableList<Integer>> splitableListSupplier)
    {
        SplitableList<Integer> list = splitableListSupplier.get();

        // Erste Hälfte
        SplitableList<Integer> subList1 = list.subList(0, 4);
        assertEquals(5, subList1.size());

        for (int i = 0; i < subList1.size(); i++)
        {
            assertEquals(i + 1, subList1.get(i).intValue());
        }

        StringJoiner joiner = new StringJoiner(",");

        for (Integer integer : subList1)
        {
            joiner.add(Integer.toString(integer));
        }

        assertEquals("1,2,3,4,5", joiner.toString());
        assertEquals("1,2,3,4,5", subList1.stream().map(FNC_INT_TO_STRING).collect(Collectors.joining(",")));
        assertEquals("1,2,3,4,5", subList1.parallelStream().map(FNC_INT_TO_STRING).collect(Collectors.joining(",")));

        // Zweite Hälfte
        SplitableList<Integer> subList2 = list.subList(5, 9);
        assertEquals(5, subList2.size());

        for (int i = 0; i < subList2.size(); i++)
        {
            assertEquals(i + 6, subList2.get(i).intValue());
        }

        joiner = new StringJoiner(",");

        for (Integer integer : subList2)
        {
            joiner.add(Integer.toString(integer));
        }

        assertEquals("6,7,8,9,10", joiner.toString());
        assertEquals("6,7,8,9,10", subList2.stream().map(FNC_INT_TO_STRING).collect(Collectors.joining(",")));
        assertEquals("6,7,8,9,10", subList2.parallelStream().map(FNC_INT_TO_STRING).collect(Collectors.joining(",")));
    }

    /**
     * @param splitableListName String
     * @param splitableListSupplier {@link Supplier}
     */
    @ParameterizedTest(name = "{0}")
    @MethodSource("getSplitterators")
    void test060Set(final String splitableListName, final Supplier<SplitableList<Integer>> splitableListSupplier)
    {
        SplitableList<Integer> list = splitableListSupplier.get();

        for (int i = 0; i < list.size(); i++)
        {
            list.set(i, i + 10);
        }

        for (int i = 0; i < list.size(); i++)
        {
            assertEquals(i + 10, list.get(i).intValue());
        }

        // Erste Hälfte
        SplitableList<Integer> subList1 = list.subList(0, 4);
        assertEquals("10,11,12,13,14", subList1.stream().map(FNC_INT_TO_STRING).collect(Collectors.joining(",")));

        for (int i = 0; i < subList1.size(); i++)
        {
            assertEquals(i + 10, subList1.get(i).intValue());
        }

        subList1.set(0, 99);
        subList1.set(4, 99);
        assertEquals("99,11,12,13,99", subList1.stream().map(FNC_INT_TO_STRING).collect(Collectors.joining(",")));

        // Zweite Hälfte
        SplitableList<Integer> subList2 = list.subList(5, 9);
        assertEquals("15,16,17,18,19", subList2.stream().map(FNC_INT_TO_STRING).collect(Collectors.joining(",")));

        for (int i = 0; i < subList2.size(); i++)
        {
            assertEquals(i + 15, subList2.get(i).intValue());
        }

        subList2.set(0, 99);
        subList2.set(4, 99);
        assertEquals("99,16,17,18,99", subList2.stream().map(FNC_INT_TO_STRING).collect(Collectors.joining(",")));
    }
}
