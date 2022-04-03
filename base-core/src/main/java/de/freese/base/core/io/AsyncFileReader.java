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
 * {@link CompletableFuture#thenAccept(java.util.function.Consumer)} weiterverarbeitet werden.
 *
 * @param <CH> Typ des ContentHolders
 *
 * @author Oliver Heger
 * @author Thomas Freese
 * @see <a href="https://github.com/oheger/JavaMagReact">https://github.com/oheger/JavaMagReact (JavaMagazin 02/2018 )</a>
 */
public final class AsyncFileReader<CH>
{
    /**
     * Interne Klasse als Attachment der {@link AsynchronousFileChannel#read(ByteBuffer, long, Object, CompletionHandler)} Operation.
     *
     * @param <CH> Typ des ContentHolders
     */
    private static class ReadContext<CH>
    {
        /**
         *
         */
        private final ByteBuffer buffer;
        /**
         *
         */
        private final AsynchronousFileChannel channel;
        /**
         *
         */
        private final CompletableFuture<CH> future;
        /**
         *
         */
        private final CompletionHandler<Integer, ReadContext<CH>> handler;
        /**
         * Aktuelle Lese-Position
         */
        private int position;

        /**
         * Erstellt ein neues {@link de.freese.base.core.io.AsyncFileReader.ReadContext} Object.
         *
         * @param afc {@link AsynchronousFileChannel}
         * @param cf {@link CompletableFuture}
         * @param handler {@link CompletionHandler}
         * @param bufferSize int
         */
        public ReadContext(final AsynchronousFileChannel afc, final CompletableFuture<CH> cf, final CompletionHandler<Integer, ReadContext<CH>> handler,
                           final int bufferSize)
        {
            super();

            this.channel = Objects.requireNonNull(afc, "AsynchronousFileChannel required");
            this.future = Objects.requireNonNull(cf, "CompletableFuture required");
            this.handler = Objects.requireNonNull(handler, "handler required");
            this.buffer = ByteBuffer.allocate(bufferSize);
        }

        /**
         * Schliesst den AsynchronousFileChannel.
         */
        public void close()
        {
            try
            {
                this.channel.close();
            }
            catch (IOException ex)
            {
                throw new UncheckedIOException(ex);
            }
        }

        /**
         * Marks this read operation as failed. The context is also closed.
         *
         * @param ex Falls was schiefgeht.
         */
        public void fail(final Throwable ex)
        {
            this.future.completeExceptionally(ex);
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
    @SuppressWarnings("unchecked")
    private Supplier<CH> contentHolderSupplier;
    /**
     * Nimmt die gelesenen Daten entgegen.
     */
    @SuppressWarnings("unchecked")
    private BiConsumer<CH, byte[]> dataConsumer;
    /**
     * Führt die parallelen Lese-Operationen aus.
     */
    private ExecutorService executorService = ForkJoinPool.commonPool();

    /**
     * Einlesen der Datei.<br>
     * Der Inhalt kann z.B. mit {@link CompletableFuture#thenAccept(java.util.function.Consumer)} weiterverarbeitet werden.
     *
     * @param path {@link Path}; Datei
     *
     * @return {@link CompletableFuture}
     */
    public CompletableFuture<CH> readFile(final Path path)
    {
        Objects.requireNonNull(path, "path required");

        if (Files.isDirectory(path) || !Files.isReadable(path))
        {
            throw new IllegalArgumentException("path is a directory or not readable: " + path);
        }

        CompletableFuture<CH> future = new CompletableFuture<>();

        CompletionHandler<Integer, ReadContext<CH>> handler = createHandler(getContentHolderSupplier(), getDataConsumer());

        try
        {
            // AsynchronousFileChannel channel = AsynchronousFileChannel.open(path, StandardOpenOption.READ);
            AsynchronousFileChannel channel = AsynchronousFileChannel.open(path, new HashSet<>(Arrays.asList(StandardOpenOption.READ)), getExecutorService());
            ReadContext<CH> context = new ReadContext<>(channel, future, handler, getByteBufferSize());

            readBlock(context);
        }
        catch (IOException ex)
        {
            future.completeExceptionally(ex);
        }

        return future;
    }

    /**
     * Blockgröße pro Lese-Operation.
     *
     * @param byteBufferSize int
     */
    public void setByteBufferSize(final int byteBufferSize)
    {
        if (byteBufferSize < 0)
        {
            throw new IllegalArgumentException("byteBufferSize < 0: " + byteBufferSize);
        }

        this.byteBufferSize = byteBufferSize;
    }

    /**
     * Erzeugt das Objekt, um die gelesenen Daten aufzunehmen.
     *
     * @param contentHolderSupplier {@link Supplier}
     */
    public void setContentHolderSupplier(final Supplier<CH> contentHolderSupplier)
    {
        this.contentHolderSupplier = Objects.requireNonNull(contentHolderSupplier, "contentHolderSupplier required");
    }

    /**
     * Nimmt die gelesenen Daten entgegen.
     *
     * @param dataConsumer {@link BiConsumer}
     */
    public void setDataConsumer(final BiConsumer<CH, byte[]> dataConsumer)
    {
        this.dataConsumer = Objects.requireNonNull(dataConsumer, "dataConsumer required");
    }

    /**
     * Führt die parallelen Lese-Operationen aus.
     *
     * @param executorService {@link ExecutorService}
     */
    public void setExecutorService(final ExecutorService executorService)
    {
        this.executorService = Objects.requireNonNull(executorService, "executorService required");
    }

    /**
     * Blockgröße pro Lese-Operation.<br>
     * Default: 1024
     *
     * @return int
     */
    protected int getByteBufferSize()
    {
        return this.byteBufferSize;
    }

    /**
     * Erzeugt das Objekt, um die gelesenen Daten aufzunehmen.<br>
     * Default: new StringBuilder(4096);
     *
     * @return {@link Supplier}
     */
    protected Supplier<CH> getContentHolderSupplier()
    {
        return this.contentHolderSupplier;
    }

    /**
     * Nimmt die gelesenen Daten entgegen.<br>
     * Default: StringBuilder.append(new String(data));
     *
     * @return {@link BiConsumer}
     */
    protected BiConsumer<CH, byte[]> getDataConsumer()
    {
        return this.dataConsumer;
    }

    /**
     * Führt die parallelen Lese-Operationen aus.<br>
     * Default: ForkJoinPool.commonPool()
     *
     * @return {@link ExecutorService}
     */
    protected ExecutorService getExecutorService()
    {
        return this.executorService;
    }

    /**
     * Erzeugt den Handler für die {@link AsynchronousFileChannel#read(ByteBuffer, long, Object, CompletionHandler)} Operation.
     *
     * @param contentHolderSupplier Object; erzeugt das Objekt um die gelesenen Daten aufzunehmen.
     * @param dataConsumer {@link BiConsumer}; Nimmt die gelesenen Daten entgegen.
     *
     * @return {@link CompletionHandler}
     */
    private CompletionHandler<Integer, ReadContext<CH>> createHandler(final Supplier<CH> contentHolderSupplier, final BiConsumer<CH, byte[]> dataConsumer)
    {
        CH contextHolder = contentHolderSupplier.get();

        return new CompletionHandler<>()
        {
            /**
             * @see java.nio.channels.CompletionHandler#completed(java.lang.Object, java.lang.Object)
             */
            @Override
            public void completed(final Integer count, final ReadContext<CH> context)
            {
                if (count < 0)
                {
                    // Dateiende erreicht.
                    context.close();
                    context.future.complete(contextHolder);
                }
                else
                {
                    context.buffer.flip();

                    // byte[] data = context.buffer.array(); // Funktioniert nicht.
                    byte[] data = new byte[count];
                    context.buffer.get(data);

                    dataConsumer.accept(contextHolder, data);

                    context.position += count;

                    readBlock(context);
                }
            }

            /**
             * @see java.nio.channels.CompletionHandler#failed(java.lang.Throwable, java.lang.Object)
             */
            @Override
            public void failed(final Throwable exc, final ReadContext<CH> context)
            {
                context.fail(exc);
            }
        };
    }

    /**
     * Liest den nächsten Datenblock.
     *
     * @param context {@link ReadContext}
     */
    private void readBlock(final ReadContext<CH> context)
    {
        context.buffer.clear();
        context.channel.read(context.buffer, context.position, context, context.handler);
    }
}
