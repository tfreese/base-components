package de.freese.base.core.io;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * OutputStream fuer eine {@link CharSequence}.
 * 
 * @author Thomas Freese
 */
public class StringOutputStream extends ByteArrayOutputStream
{
	/**
	 * Erstellt ein neues {@link StringOutputStream} Object.
	 */
	public StringOutputStream()
	{
		super();
	}

	/**
	 * @param value {@link CharSequence}
	 * @throws IOException Falls was schief geht.
	 */
	public void write(final CharSequence value) throws IOException
	{
		super.write(value.toString().getBytes());
	}
}
