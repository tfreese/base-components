package de.freese.base.reports.importer.excel;

import java.io.Serial;

/**
 * {@link Exception} für die Excel API.
 *
 * @author Thomas Freese
 */
public class ExcelException extends Exception
{
    @Serial
    private static final long serialVersionUID = 4357042787522085265L;

    private final int column;

    private final int row;

    private final String sheet;

    public ExcelException(final String sheet, final int row, final int column, final Throwable cause)
    {
        super(cause);

        this.sheet = sheet;
        this.row = row;
        this.column = column;

        setStackTrace(cause.getStackTrace());
    }

    /**
     * @see java.lang.Throwable#getMessage()
     */
    @Override
    public String getMessage()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Sheet %s, Row=%d, Column=%d", this.sheet, this.row, this.column));
        sb.append("\n");
        sb.append(super.getMessage());

        return sb.toString();
    }
}
