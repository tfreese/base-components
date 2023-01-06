// Created: 11.08.2010
package de.freese.base.reports.exporter.pdf;

import java.awt.Color;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.BiConsumer;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfWriter;
import de.freese.base.reports.exporter.AbstractExporter;

/**
 * @author Thomas Freese
 */
public abstract class AbstractPdfExporter<T> extends AbstractExporter<T>
{
    private Document document;

    private PdfWriter writer;

    @Override
    public void export(final Path filePath, final T model) throws Exception
    {
        try (OutputStream outputStream = new BufferedOutputStream(Files.newOutputStream(filePath)))
        {
            export(outputStream, model);

            getDocument().close();
            getWriter().close();
        }
    }

    protected void createDocumentAndWriter(final OutputStream outputStream, BiConsumer<Document, PdfWriter> configurator) throws DocumentException
    {
        this.document = new Document();
        this.writer = PdfWriter.getInstance(this.document, outputStream);

        if (configurator != null)
        {
            configurator.accept(this.document, this.writer);
        }
        else
        {
            //  Defaults: A4 Portrait, Margins (40, 20, 20, 20)

            // Landscape: PageSize.A4.rotate()
            this.document.setPageSize(PageSize.A4);

            // left, right, top, bottom
            this.document.setMargins(40, 20, 20, 20);

            //        this.document.addKeywords(...);
            //        this.document.addCreator(...);
            //        this.document.addAuthor(...);
            //        this.document.addSubject(...);
            //        this.document.addTitle(...);

            // setFullCompression set PDF-Version to 1.5
            this.writer.setFullCompression();
            this.writer.setPdfVersion(PdfWriter.VERSION_1_7);
        }

        getDocument().open();
    }

    /**
     * @param strokeColor {@link Color}, optional, if null, previous Color is used.
     */
    protected void drawLine(final float x1, final float y1, final float x2, final float y2, final Color strokeColor)
    {
        PdfContentByte contentByte = getWriter().getDirectContent();
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
     * @param fillColor {@link Color}, optional, if null, previous Color is used.
     * @param borderColor {@link Color}, optional, if null, previous Color is used.
     */
    protected void drawRectangle(final float x, final float y, final float width, final float height, final Color fillColor, final Color borderColor)
    {
        PdfContentByte contentByte = getWriter().getDirectContent();
        contentByte.saveState();

        if (fillColor != null)
        {
            contentByte.setColorFill(fillColor);
        }

        if (borderColor != null)
        {
            contentByte.setColorStroke(borderColor);
            contentByte.rectangle(x, y, width, height);
            contentByte.stroke();
        }

        contentByte.rectangle(x, y, width, height);

        if (fillColor != null)
        {
            contentByte.fill();
        }

        contentByte.restoreState();
    }

    /**
     * @param align int, @see {@link PdfContentByte#ALIGN_LEFT} ...
     */
    protected void drawText(final String text, final float x, final float y, final float fontSize, final int align, final BaseFont baseFont)
    {
        PdfContentByte contentByte = getWriter().getDirectContent();
        contentByte.beginText();
        contentByte.setFontAndSize(baseFont, fontSize);
        contentByte.showTextAligned(align, text, x, y, 0);
        //        contentByte.setTextMatrix(x, y);
        //        contentByte.showText(text);
        contentByte.endText();
    }

    protected void drawTextFooter(final String text, final Font font) throws DocumentException, IOException
    {
        drawText(text, getMaxX(), getMinY(), font.getSize(), PdfContentByte.ALIGN_RIGHT, font.getBaseFont());
    }

    protected final Document getDocument()
    {
        return this.document;
    }

    /**
     * Returns the max. X Coordinate consider the right Margin.
     */
    protected final float getMaxX()
    {
        return getDocument().getPageSize().getWidth() - getDocument().rightMargin();
    }

    /**
     * Returns the max. Y Coordinate consider the upper Margin.
     */
    protected final float getMaxY()
    {
        return getDocument().getPageSize().getHeight() - getDocument().topMargin();
    }

    /**
     * Returns the min. X Coordinate consider the left Margin.
     */
    protected final float getMinX()
    {
        return getDocument().leftMargin();
    }

    /**
     * Returns the max. Y Coordinate consider the bottom Margin.
     */
    protected final float getMinY()
    {
        return getDocument().bottomMargin();
    }

    protected final PdfWriter getWriter()
    {
        return this.writer;
    }

    /**
     * Returns the X Coordinate relativ to the origin (minX).
     */
    protected final float getX(final int offset)
    {
        return getMinX() + offset;
    }

    /**
     * Returns the X Coordinate relativ to the origin (minX).
     *
     * @param prozent float, 0...100
     */
    protected final float getXRelative(final float prozent)
    {
        return ((getMaxX() - getMinX()) * (prozent / 100F)) + getMinX();
    }

    /**
     * Returns the Y Coordinate relativ to the origin (minY).
     */
    protected final float getY(final int offset)
    {
        return getMinY() + offset;
    }

    /**
     * Returns the Y Coordinate relativ to the origin (minY).
     *
     * @param prozent float, 0...100
     */
    protected final float getYRelative(final float prozent)
    {
        return ((getMaxY() - getMinY()) * (prozent / 100F)) + getMinY();
    }

    /**
     * Secure with Password and limit rights.
     *
     * @param userPassword String, null = No Question during opening
     * @param ownerPassword String, null = No Question during Changes
     */
    protected void secure(final PdfWriter writer, final String userPassword, final String ownerPassword) throws DocumentException
    {
        byte[] userPwd = userPassword != null ? userPassword.getBytes(StandardCharsets.UTF_8) : null;
        byte[] ownerPwd = ownerPassword != null ? ownerPassword.getBytes(StandardCharsets.UTF_8) : null;

        writer.setEncryption(userPwd, ownerPwd, PdfWriter.ALLOW_PRINTING | PdfWriter.ALLOW_SCREENREADERS, PdfWriter.ENCRYPTION_AES_128);
    }
}
