package de.freese.base.core.io;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

import de.freese.base.core.model.builder.GenericBuilder;
import org.junit.jupiter.api.Test;

/**
 * @author Thomas Freese
 */
class TestAsyncFileReader
{
    /**
     * @throws Exception Falls was schiefgeht
     */
    @Test
    void testAsyncFileReader() throws Exception
    {
        Supplier<StringBuilder> contentHolderSupplier = () -> new StringBuilder(4096);
        BiConsumer<StringBuilder, byte[]> dataConsumer = (sb, data) ->
        {
            System.out.printf("[%s] Read completed%n", Thread.currentThread().getName());
            sb.append(new String(data, StandardCharsets.UTF_8));
        };

        //        AsyncFileReader<StringBuilder> reader = new AsyncFileReader<>();
        //        reader.setContentHolderSupplier(contentHolderSupplier);
        //        reader.setDataConsumer(dataConsumer);
        //        reader.setByteBufferSize(1024);
        //        reader.setExecutorService(ForkJoinPool.commonPool());

        //@formatter:off
        AsyncFileReader<StringBuilder> reader = GenericBuilder.
                of(AsyncFileReader<StringBuilder>::new)
                .with(AsyncFileReader::setByteBufferSize, 1024)
                .with(r -> r.setContentHolderSupplier(contentHolderSupplier))
                .with(r -> r.setDataConsumer(dataConsumer))
                .build()
                ;
        //@formatter:on

        Path path = Paths.get(System.getProperty("user.dir"), "pom.xml");
        System.out.printf("Reading file: %s%n", path);

        // 2x parallel auslesen.
        long startTime1 = System.currentTimeMillis();
        CompletableFuture<StringBuilder> future1 = reader.readFile(path);

        long startTime2 = System.currentTimeMillis();
        CompletableFuture<StringBuilder> future2 = reader.readFile(path);

        System.out.println("Read in progress...");

        BiConsumer<Long, CharSequence> printer = (startTime, cs) ->
        {
            long duration = System.currentTimeMillis() - startTime;

            System.out.printf("[%s] Read %d bytes in %d ms.%n", Thread.currentThread().getName(), cs.length(), duration);
        };

        future1.thenAcceptAsync(cs -> printer.accept(startTime1, cs));
        future2.thenAccept(cs -> printer.accept(startTime2, cs));

        // Dient nur dazu, damit das Programm nicht vorzeitig beendet wird
        TimeUnit.MILLISECONDS.sleep(100);

        assertTrue(true);
    }
}
