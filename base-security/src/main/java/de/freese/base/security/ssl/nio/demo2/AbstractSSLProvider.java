package de.freese.base.security.ssl.nio.demo2;

import java.nio.ByteBuffer;
import java.util.concurrent.Executor;

import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLEngineResult;
import javax.net.ssl.SSLEngineResult.HandshakeStatus;
import javax.net.ssl.SSLException;

/**
 * @author Thomas Freese
 */
public abstract class AbstractSSLProvider implements Runnable
{
    /**
     *
     */
    final ByteBuffer clientUnwrap;
    /**
     *
     */
    final ByteBuffer clientWrap;
    /**
     *
     */
    final SSLEngine engine;
    /**
     *
     */
    final Executor ioWorker;
    /**
     *
     */
    final ByteBuffer serverUnwrap;
    /**
     *
     */
    final ByteBuffer serverWrap;
    /**
     *
     */
    final Executor taskWorkers;

    /**
     * Erstellt ein neues {@link AbstractSSLProvider} Object.
     *
     * @param engine {@link SSLEngine}
     * @param capacity int
     * @param ioWorker {@link Executor}
     * @param taskWorkers {@link Executor}
     */
    protected AbstractSSLProvider(final SSLEngine engine, final int capacity, final Executor ioWorker, final Executor taskWorkers)
    {
        super();

        this.clientWrap = ByteBuffer.allocate(capacity);
        this.serverWrap = ByteBuffer.allocate(capacity);
        this.clientUnwrap = ByteBuffer.allocate(capacity);
        this.serverUnwrap = ByteBuffer.allocate(capacity);
        this.clientUnwrap.limit(0);
        this.engine = engine;
        this.ioWorker = ioWorker;
        this.taskWorkers = taskWorkers;
        this.ioWorker.execute(this);
    }

    /**
     * @param data {@link ByteBuffer}
     */
    public void notify(final ByteBuffer data)
    {
        this.ioWorker.execute(() ->
        {
            AbstractSSLProvider.this.clientUnwrap.put(data);
            AbstractSSLProvider.this.run();
        });
    }

    /**
     *
     */
    public abstract void onClosed();

    /**
     * @param ex Exception
     */
    public abstract void onFailure(Exception ex);

    /**
     * @param decrypted {@link ByteBuffer}
     */
    public abstract void onInput(ByteBuffer decrypted);

    /**
     * @param encrypted {@link ByteBuffer}
     */
    public abstract void onOutput(ByteBuffer encrypted);

    /**
     *
     */
    public abstract void onSuccess();

    /**
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run()
    {
        // executes non-blocking tasks on the IO-Worker
        while (isHandShaking())
        {
            // Empty
        }
    }

    /**
     * @param data {@link ByteBuffer}
     */
    public void sendAsync(final ByteBuffer data)
    {
        this.ioWorker.execute(() ->
        {
            this.clientWrap.put(data);

            AbstractSSLProvider.this.run();
        });
    }

    /**
     * @return boolean
     */
    @SuppressWarnings("incomplete-switch")
    private synchronized boolean isHandShaking()
    {
        switch (this.engine.getHandshakeStatus())
        {
            case NOT_HANDSHAKING:
                boolean occupied = false;

            {
                if (this.clientWrap.position() > 0)
                {
                    occupied |= wrap();
                }

                if (this.clientUnwrap.position() > 0)
                {
                    occupied |= unwrap();
                }
            }

            return occupied;

            case NEED_WRAP:
                if (!wrap())
                {
                    return false;
                }

                break;

            case NEED_UNWRAP:
                if (!unwrap())
                {
                    return false;
                }

                break;

            case NEED_TASK:
                final Runnable sslTask = this.engine.getDelegatedTask();

                Runnable wrappedTask = () ->
                {
                    sslTask.run();
                    AbstractSSLProvider.this.ioWorker.execute(AbstractSSLProvider.this);
                };

                this.taskWorkers.execute(wrappedTask);
                return false;

            case FINISHED:
                throw new IllegalStateException("FINISHED");
        }

        return true;
    }

    /**
     * @return boolean
     */
    private boolean unwrap()
    {
        SSLEngineResult unwrapResult;

        try
        {
            this.clientUnwrap.flip();
            unwrapResult = this.engine.unwrap(this.clientUnwrap, this.serverUnwrap);
            this.clientUnwrap.compact();
        }
        catch (SSLException ex)
        {
            onFailure(ex);
            return false;
        }

        switch (unwrapResult.getStatus())
        {
            case OK:
                if (this.serverUnwrap.position() > 0)
                {
                    this.serverUnwrap.flip();
                    onInput(this.serverUnwrap);
                    this.serverUnwrap.compact();
                }

                break;

            case CLOSED:
                onClosed();

                return false;

            case BUFFER_OVERFLOW:
                throw new IllegalStateException("failed to unwrap");

            case BUFFER_UNDERFLOW:
                return false;
        }

        if (unwrapResult.getHandshakeStatus() == HandshakeStatus.FINISHED)
        {
            onSuccess();

            return false;
        }

        return true;
    }

    /**
     * @return boolean
     */
    private boolean wrap()
    {
        SSLEngineResult wrapResult;

        try
        {
            this.clientWrap.flip();
            wrapResult = this.engine.wrap(this.clientWrap, this.serverWrap);
            this.clientWrap.compact();
        }
        catch (SSLException exc)
        {
            onFailure(exc);
            return false;
        }

        switch (wrapResult.getStatus())
        {
            case OK:
                if (this.serverWrap.position() > 0)
                {
                    this.serverWrap.flip();
                    onOutput(this.serverWrap);
                    this.serverWrap.compact();
                }

                break;

            case BUFFER_UNDERFLOW:
                // try again later
                break;

            case BUFFER_OVERFLOW:
                throw new IllegalStateException("failed to wrap");

            case CLOSED:
                onClosed();

                return false;
        }

        return true;
    }
}
