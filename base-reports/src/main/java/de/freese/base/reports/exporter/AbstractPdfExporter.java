// Created: 11.08.2010
package de.freese.base.reports.exporter;

import java.awt.Color;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfWriter;

/**
 * @author Thomas Freese
 */
public abstract class AbstractPdfExporter<T> extends AbstractExporter<T>
{
    @Override
    public void export(final OutputStream outputStream, final T model) throws Exception
    {
        Document document = new Document();
        PdfWriter writer = PdfWriter.getInstance(document, outputStream);

        export(document, writer, model);

        document.close();
        writer.close();
    }

    public abstract void export(final Document document, PdfWriter writer, final T model) throws Exception;

    /**
     * @param strokeColor {@link Color}, optional, if null, default Color is used.
     */
    protected void drawLine(final PdfWriter writer, final float x1, final float y1, final float x2, final float y2, final Color strokeColor)
    {
        PdfContentByte contentByte = writer.getDirectContent();
        contentByte.saveState();

        if (strokeColor != null)
        {
            contentByte.setColorStroke(strokeColor);
        }

        contentByte.moveTo(x1, y1);
        contentByte.lineTo(x2, y2);
        contentByte.stroke();

        contentByte.restoreState();
    }

    /**
     * @param fillColor {@link Color}, optional, if null, default Color is used.
     * @param borderColor {@link Color}, optional, if null, default Color is used.
     */
    protected void drawRectangle(final PdfWriter writer, final float x, final float y, final float width, final float height, final Color fillColor, final Color borderColor)
    {
        PdfContentByte contentByte = writer.getDirectContent();
        contentByte.saveState();

        if (fillColor != null)
        {
            contentByte.setColorFill(fillColor);
        }

        if (borderColor != null)
        {
            contentByte.setColorStroke(borderColor);
        }

        contentByte.rectangle(x, y, width, height);
        contentByte.fillStroke();
        //        contentByte.fill();
        //        contentByte.stroke();

        contentByte.restoreState();
    }

    /**
     * @param align int, @see {@link PdfContentByte#ALIGN_LEFT} ...
     */
    protected void drawText(final PdfWriter writer, final String text, final float x, final float y, final float fontSize, final int align, final BaseFont baseFont)
    {
        PdfContentByte contentByte = writer.getDirectContent();
        contentByte.beginText();
        contentByte.setFontAndSize(baseFont, fontSize);
        contentByte.showTextAligned(align, text, x, y, 0);
        //        contentByte.setTextMatrix(x, y);
        //        contentByte.showText(text);
        contentByte.endText();
    }

    protected void drawTextFooter(final Document document, final PdfWriter writer, final String text, final Font font) throws DocumentException, IOException
    {
        drawText(writer, text, getMaxX(document), getMinY(document), font.getSize(), PdfContentByte.ALIGN_RIGHT, font.getBaseFont());
    }

    /**
     * Returns the max. X Coordinate consider the right Margin.
     */
    protected final float getMaxX(final Document document)
    {
        return document.getPageSize().getWidth() - document.rightMargin();
    }

    /**
     * Returns the max. Y Coordinate consider the upper Margin.
     */
    protected final float getMaxY(final Document document)
    {
        return document.getPageSize().getHeight() - document.topMargin();
    }

    /**
     * Returns the min. X Coordinate consider the left Margin.
     */
    protected final float getMinX(final Document document)
    {
        return document.leftMargin();
    }

    /**
     * Returns the max. Y Coordinate consider the bottom Margin.
     */
    protected final float getMinY(final Document document)
    {
        return document.bottomMargin();
    }

    /**
     * Returns the X Coordinate relativ to the origin (minX).
     */
    protected final float getX(final Document document, final int offset)
    {
        return getMinX(document) + offset;
    }

    /**
     * Returns the X Coordinate relativ to the origin (minX).
     *
     * @param prozent float, 0...100
     */
    protected final float getXRelative(final Document document, final float prozent)
    {
        return ((getMaxX(document) - getMinX(document)) * (prozent / 100F)) + getMinX(document);
    }

    /**
     * Returns the Y Coordinate relativ to the origin (minY).
     */
    protected final float getY(final Document document, final int offset)
    {
        return getMinY(document) + offset;
    }

    /**
     * Returns the Y Coordinate relativ to the origin (minY).
     *
     * @param prozent float, 0...100
     */
    protected final float getYRelative(final Document document, final float prozent)
    {
        return ((getMaxY(document) - getMinY(document)) * (prozent / 100F)) + getMinY(document);
    }

    /**
     * Secure with Password and limit rights.<br>
     * Must called before opening the Document.
     *
     * @param userPassword String, null = No Question during opening
     * @param ownerPassword String, null = No Question during changes
     */
    protected void secure(final PdfWriter writer, final String userPassword, final String ownerPassword) throws DocumentException
    {
        byte[] userPwd = userPassword != null ? userPassword.getBytes(StandardCharsets.UTF_8) : null;
        byte[] ownerPwd = ownerPassword != null ? ownerPassword.getBytes(StandardCharsets.UTF_8) : null;

        writer.setEncryption(userPwd, ownerPwd, PdfWriter.ALLOW_PRINTING | PdfWriter.ALLOW_SCREENREADERS, PdfWriter.ENCRYPTION_AES_128);
    }
}
