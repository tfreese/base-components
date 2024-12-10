// Created: 07.01.2018
package de.freese.base.core.io;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

/**
 * Einlesen einer Datei über ein {@link AsynchronousFileChannel}.<br>
 * Der Inhalt wird einem {@link CompletableFuture} übergeben und kann z.B. mit<br>
 * {@link CompletableFuture#thenAccept(java.util.function.Consumer)} weiterverarbeitet werden.<br>
 * <a href="https://github.com/oheger/JavaMagReact">https://github.com/oheger/JavaMagReact (JavaMagazin 02/2018 )</a>
 *
 * @param <CH> Typ des ContentHolders
 *
 * @author Oliver Heger
 * @author Thomas Freese
 */
public final class AsyncFileReader<CH> {
    /**
     * Interne Klasse als Attachment der {@link AsynchronousFileChannel#read(ByteBuffer, long, Object, CompletionHandler)} Operation.
     */
    private static class ReadContext<CH> {
        private final ByteBuffer buffer;
        private final AsynchronousFileChannel channel;
        private final CompletableFuture<CH> future;
        private final CompletionHandler<Integer, ReadContext<CH>> handler;

        private int position;

        ReadContext(final AsynchronousFileChannel afc, final CompletableFuture<CH> cf, final CompletionHandler<Integer, ReadContext<CH>> handler, final int bufferSize) {
            super();

            this.channel = Objects.requireNonNull(afc, "AsynchronousFileChannel required");
            this.future = Objects.requireNonNull(cf, "CompletableFuture required");
            this.handler = Objects.requireNonNull(handler, "handler required");
            this.buffer = ByteBuffer.allocate(bufferSize);
        }

        public void close() {
            try {
                channel.close();
            }
            catch (IOException ex) {
                throw new UncheckedIOException(ex);
            }
        }

        /**
         * Marks this read operation as failed.<br/>
         * The context is also closed.
         */
        public void fail(final Throwable ex) {
            future.completeExceptionally(ex);
            close();
        }
    }

    /**
     * Blockgröße pro Lese-Operation.
     */
    private int byteBufferSize = 8192;
    /**
     * Erzeugt das Objekt, um die gelesenen Daten aufzunehmen.
     */
    private Supplier<CH> contentHolderSupplier;
    /**
     * Nimmt die gelesenen Daten entgegen.
     */
    private BiConsumer<CH, byte[]> dataConsumer;
    /**
     * Führt die parallelen Lese-Operationen aus.
     */
    private ExecutorService executorService = ForkJoinPool.commonPool();

    /**
     * Einlesen der Datei.<br>
     * Der Inhalt kann z.B. mit {@link CompletableFuture#thenAccept(java.util.function.Consumer)} weiterverarbeitet werden.
     */
    public CompletableFuture<CH> readFile(final Path path) {
        Objects.requireNonNull(path, "path required");

        if (Files.isDirectory(path) || !Files.isReadable(path)) {
            throw new IllegalArgumentException("path is a directory or not readable: " + path);
        }

        final CompletableFuture<CH> future = new CompletableFuture<>();

        final CompletionHandler<Integer, ReadContext<CH>> handler = createHandler(getContentHolderSupplier(), getDataConsumer());

        try {
            // final AsynchronousFileChannel channel = AsynchronousFileChannel.open(path, StandardOpenOption.READ);
            final AsynchronousFileChannel channel = AsynchronousFileChannel.open(path, new HashSet<>(Arrays.asList(StandardOpenOption.READ)), getExecutorService());
            final ReadContext<CH> context = new ReadContext<>(channel, future, handler, getByteBufferSize());

            readBlock(context);
        }
        catch (IOException ex) {
            future.completeExceptionally(ex);
        }

        return future;
    }

    /**
     * Blockgröße pro Lese-Operation.
     */
    public void setByteBufferSize(final int byteBufferSize) {
        if (byteBufferSize < 0) {
            throw new IllegalArgumentException("byteBufferSize < 0: " + byteBufferSize);
        }

        this.byteBufferSize = byteBufferSize;
    }

    /**
     * Erzeugt das Objekt, um die gelesenen Daten aufzunehmen.
     */
    public void setContentHolderSupplier(final Supplier<CH> contentHolderSupplier) {
        this.contentHolderSupplier = Objects.requireNonNull(contentHolderSupplier, "contentHolderSupplier required");
    }

    /**
     * Nimmt die gelesenen Daten entgegen.
     */
    public void setDataConsumer(final BiConsumer<CH, byte[]> dataConsumer) {
        this.dataConsumer = Objects.requireNonNull(dataConsumer, "dataConsumer required");
    }

    /**
     * Führt die parallelen Lese-Operationen aus.
     */
    public void setExecutorService(final ExecutorService executorService) {
        this.executorService = Objects.requireNonNull(executorService, "executorService required");
    }

    /**
     * Erzeugt den Handler für die {@link AsynchronousFileChannel#read(ByteBuffer, long, Object, CompletionHandler)} Operation.
     *
     * @param contentHolderSupplier Object; erzeugt das Objekt um die gelesenen Daten aufzunehmen.
     * @param dataConsumer {@link BiConsumer}; Nimmt die gelesenen Daten entgegen.
     */
    private CompletionHandler<Integer, ReadContext<CH>> createHandler(final Supplier<CH> contentHolderSupplier, final BiConsumer<CH, byte[]> dataConsumer) {
        final CH contextHolder = contentHolderSupplier.get();

        return new CompletionHandler<>() {
            @Override
            public void completed(final Integer count, final ReadContext<CH> context) {
                if (count < 0) {
                    // Dateiende erreicht.
                    context.close();
                    context.future.complete(contextHolder);
                }
                else {
                    context.buffer.flip();

                    // byte[] data = context.buffer.array(); // Funktioniert nicht.
                    final byte[] data = new byte[count];
                    context.buffer.get(data);

                    dataConsumer.accept(contextHolder, data);

                    context.position += count;

                    readBlock(context);
                }
            }

            @Override
            public void failed(final Throwable exc, final ReadContext<CH> context) {
                context.fail(exc);
            }
        };
    }

    /**
     * Blockgröße pro Lese-Operation.<br>
     * Default: 1024
     */
    private int getByteBufferSize() {
        return byteBufferSize;
    }

    /**
     * Erzeugt das Objekt, um die gelesenen Daten aufzunehmen.<br>
     * Default: new StringBuilder(4096);
     */
    private Supplier<CH> getContentHolderSupplier() {
        return contentHolderSupplier;
    }

    /**
     * Nimmt die gelesenen Daten entgegen.<br>
     * Default: StringBuilder.append(new String(data));
     */
    private BiConsumer<CH, byte[]> getDataConsumer() {
        return dataConsumer;
    }

    /**
     * Führt die parallelen Lese-Operationen aus.<br>
     * Default: ForkJoinPool.commonPool()
     */
    private ExecutorService getExecutorService() {
        return executorService;
    }

    /**
     * Liest den nächsten Datenblock.
     */
    private void readBlock(final ReadContext<CH> context) {
        context.buffer.clear();
        context.channel.read(context.buffer, context.position, context, context.handler);
    }
}
