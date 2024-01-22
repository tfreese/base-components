package de.freese.base.core.io;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

import org.junit.jupiter.api.Test;

import de.freese.base.core.model.builder.GenericBuilder;

/**
 * @author Thomas Freese
 */
class TestAsyncFileReader {
    @Test
    void testAsyncFileReader() throws Exception {
        final Supplier<StringBuilder> contentHolderSupplier = () -> new StringBuilder(4096);
        final BiConsumer<StringBuilder, byte[]> dataConsumer = (sb, data) -> {
            System.out.printf("[%s] Read completed%n", Thread.currentThread().getName());
            sb.append(new String(data, StandardCharsets.UTF_8));
        };

        //        final AsyncFileReader<StringBuilder> reader = new AsyncFileReader<>();
        //        reader.setContentHolderSupplier(contentHolderSupplier);
        //        reader.setDataConsumer(dataConsumer);
        //        reader.setByteBufferSize(1024);
        //        reader.setExecutorService(ForkJoinPool.commonPool());

        //@formatter:off
        final AsyncFileReader<StringBuilder> reader = GenericBuilder.
                of(AsyncFileReader<StringBuilder>::new)
                .with(AsyncFileReader::setByteBufferSize, 1024)
                .with(r -> r.setContentHolderSupplier(contentHolderSupplier))
                .with(r -> r.setDataConsumer(dataConsumer))
                .build()
                ;
        //@formatter:on

        final Path path = Paths.get(System.getProperty("user.dir"), "pom.xml");
        System.out.printf("Reading file: %s%n", path);

        // 2x parallel auslesen.
        final long startTime1 = System.currentTimeMillis();
        final CompletableFuture<StringBuilder> future1 = reader.readFile(path);

        final long startTime2 = System.currentTimeMillis();
        final CompletableFuture<StringBuilder> future2 = reader.readFile(path);

        System.out.println("Read in progress...");

        final BiConsumer<Long, CharSequence> printer = (startTime, cs) -> {
            final long duration = System.currentTimeMillis() - startTime;

            System.out.printf("[%s] Read %d bytes in %d ms.%n", Thread.currentThread().getName(), cs.length(), duration);
        };

        future1.thenAcceptAsync(cs -> printer.accept(startTime1, cs));
        future2.thenAccept(cs -> printer.accept(startTime2, cs));

        // Dient nur dazu, damit das Programm nicht vorzeitig beendet wird
        await().pollDelay(Duration.ofMillis(100)).until(() -> true);

        assertTrue(true);
    }
}
