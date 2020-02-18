/**
 * Created: 16.09.2016
 */

package de.freese.base.core.collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.Spliterator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import de.freese.base.core.collection.stream.spliterator.GenericSplitableSpliterator;
import de.freese.base.core.collection.stream.spliterator.SplitableArraySpliterator;
import de.freese.base.core.collection.stream.spliterator.SplitableListSpliterator;

/**
 * TestCase für die {@link Spliterator} Implementierungen.<br>
 * Die Spliteratoren müssen in jedem TestCase neu erzeugt werden.<br>
 * Dies wird mit dem {@link Supplier} Interface gemacht.
 *
 * @author Thomas Freese
 */
@TestMethodOrder(MethodOrderer.Alphanumeric.class)
public class TestStreamSpliterator
{
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
        Supplier<Spliterator<Integer>> supplierArray = () -> new SplitableArraySpliterator<>(INTEGERS_SUPPLIER.get());
        Supplier<Spliterator<Integer>> supplierList = () -> new SplitableListSpliterator<>(Arrays.asList(INTEGERS_SUPPLIER.get()));
        Supplier<Spliterator<Integer>> supplierGenericArray = () -> new GenericSplitableSpliterator<>(INTEGERS_SUPPLIER.get());
        Supplier<Spliterator<Integer>> supplierGenericList = () -> new GenericSplitableSpliterator<>(Arrays.asList(INTEGERS_SUPPLIER.get()));

        SPLITTERATORS.add(Arguments.of(SplitableArraySpliterator.class.getSimpleName(), supplierArray));
        SPLITTERATORS.add(Arguments.of(SplitableListSpliterator.class.getSimpleName(), supplierList));
        SPLITTERATORS.add(Arguments.of(GenericSplitableSpliterator.class.getSimpleName() + "-Array", supplierGenericArray));
        SPLITTERATORS.add(Arguments.of(GenericSplitableSpliterator.class.getSimpleName() + "-List", supplierGenericList));
    }

    /**
     * @return {@link Stream}
     */
    static Stream<Arguments> getSplitterators()
    {
        return SPLITTERATORS.stream();
    }

    /**
     * Erstellt ein neues {@link TestStreamSpliterator} Object.
     */
    public TestStreamSpliterator()
    {
        super();
    }

    /**
     * @param spliteratorName String
     * @param spliterator {@link Supplier}
     */
    @ParameterizedTest(name = "{0}")
    @MethodSource("getSplitterators")
    public void test010Summe(final String spliteratorName, final Supplier<Spliterator<Integer>> spliterator)
    {
        assertEquals(55, StreamSupport.stream(spliterator.get(), false).mapToInt(n -> n).sum());
        assertEquals(55, StreamSupport.stream(spliterator.get(), true).parallel().mapToInt(n -> n).sum());
    }

    /**
     * @param spliteratorName String
     * @param spliterator {@link Supplier}
     */
    @ParameterizedTest(name = "{0}")
    @MethodSource("getSplitterators")
    public void test020ToString(final String spliteratorName, final Supplier<Spliterator<Integer>> spliterator)
    {
        Function<Integer, String> intToString = (n) -> Integer.toString(n);

        assertEquals("1,2,3,4,5,6,7,8,9,10", StreamSupport.stream(spliterator.get(), false).map(intToString).collect(Collectors.joining(",")));
        assertEquals("1,2,3,4,5,6,7,8,9,10", StreamSupport.stream(spliterator.get(), true).parallel().map(intToString).collect(Collectors.joining(",")));
    }

    /**
     * @param spliteratorName String
     * @param spliterator {@link Supplier}
     */
    @ParameterizedTest(name = "{0}")
    @MethodSource("getSplitterators")
    public void test030Stats(final String spliteratorName, final Supplier<Spliterator<Integer>> spliterator)
    {
        IntSummaryStatistics stats = StreamSupport.stream(spliterator.get(), false).mapToInt(n -> n).summaryStatistics();
        assertEquals(10, stats.getCount());
        assertEquals(55, stats.getSum());
        assertEquals(1, stats.getMin());
        assertEquals(10, stats.getMax());
        assertEquals(5.5D, stats.getAverage(), 0);

        stats = StreamSupport.stream(spliterator.get(), true).parallel().mapToInt(n -> n).summaryStatistics();
        assertEquals(10, stats.getCount());
        assertEquals(55, stats.getSum());
        assertEquals(1, stats.getMin());
        assertEquals(10, stats.getMax());
        assertEquals(5.5D, stats.getAverage(), 0);
    }
}
