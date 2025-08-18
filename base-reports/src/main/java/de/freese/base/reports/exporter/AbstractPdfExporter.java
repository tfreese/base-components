// Created: 11.08.2010
package de.freese.base.reports.exporter;

import java.awt.Color;
import java.io.OutputStream;

import org.openpdf.text.Document;
import org.openpdf.text.DocumentException;
import org.openpdf.text.Font;
import org.openpdf.text.pdf.BaseFont;
import org.openpdf.text.pdf.PdfContentByte;
import org.openpdf.text.pdf.PdfWriter;

/**
 * @author Thomas Freese
 */
public abstract class AbstractPdfExporter<T> extends AbstractExporter<T> {
    /**
     * Returns the max. X Coordinate consider the right Margin.
     */
    protected static float getMaxX(final Document document) {
        return document.getPageSize().getWidth() - document.rightMargin();
    }

    /**
     * Returns the max. Y Coordinate consider the upper Margin.
     */
    protected static float getMaxY(final Document document) {
        return document.getPageSize().getHeight() - document.topMargin();
    }

    /**
     * Returns the min. X Coordinate consider the left Margin.
     */
    protected static float getMinX(final Document document) {
        return document.leftMargin();
    }

    /**
     * Returns the max. Y Coordinate consider the bottom Margin.
     */
    protected static float getMinY(final Document document) {
        return document.bottomMargin();
    }

    /**
     * Returns the X Coordinate relativ to the origin (minX).
     */
    protected static float getX(final Document document, final int offset) {
        return getMinX(document) + offset;
    }

    /**
     * Returns the X Coordinate relativ to the origin (minX).
     *
     * @param prozent float, 0...100
     */
    protected static float getXRelative(final Document document, final float prozent) {
        return ((getMaxX(document) - getMinX(document)) * (prozent / 100F)) + getMinX(document);
    }

    /**
     * Returns the Y Coordinate relativ to the origin (minY).
     */
    protected static float getY(final Document document, final int offset) {
        return getMinY(document) + offset;
    }

    /**
     * Returns the Y Coordinate relativ to the origin (minY).
     *
     * @param prozent float, 0...100
     */
    protected static float getYRelative(final Document document, final float prozent) {
        return ((getMaxY(document) - getMinY(document)) * (prozent / 100F)) + getMinY(document);
    }

    @Override
    public void export(final OutputStream outputStream, final T model) throws Exception {
        final Document document = new Document();
        final PdfWriter writer = PdfWriter.getInstance(document, outputStream);

        export(document, writer, model);

        document.close();
        writer.close();
    }

    public abstract void export(Document document, PdfWriter writer, T model) throws Exception;

    /**
     * @param strokeColor {@link Color}, optional, if null, default Color is used.
     */
    protected void drawLine(final PdfWriter writer, final float x1, final float y1, final float x2, final float y2, final Color strokeColor) {
        final PdfContentByte contentByte = writer.getDirectContent();
        contentByte.saveState();

        if (strokeColor != null) {
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
    protected void drawRectangle(final PdfWriter writer, final float x, final float y, final float width, final float height, final Color fillColor, final Color borderColor) {
        final PdfContentByte contentByte = writer.getDirectContent();
        contentByte.saveState();

        if (fillColor != null) {
            contentByte.setColorFill(fillColor);
        }

        if (borderColor != null) {
            contentByte.setColorStroke(borderColor);
        }

        contentByte.rectangle(x, y, width, height);
        contentByte.fillStroke();
        // contentByte.fill();
        // contentByte.stroke();

        contentByte.restoreState();
    }

    /**
     * @param align int,  {@link PdfContentByte#ALIGN_LEFT} ...
     */
    protected void drawText(final PdfWriter writer, final String text, final float x, final float y, final float fontSize, final int align, final BaseFont baseFont) {
        final PdfContentByte contentByte = writer.getDirectContent();
        contentByte.beginText();
        contentByte.setFontAndSize(baseFont, fontSize);
        contentByte.showTextAligned(align, text, x, y, 0F);
        // contentByte.setTextMatrix(x, y);
        // contentByte.showText(text);
        contentByte.endText();
    }

    protected void drawTextFooter(final Document document, final PdfWriter writer, final String text, final Font font) throws DocumentException {
        drawText(writer, text, getMaxX(document), getMinY(document), font.getSize(), PdfContentByte.ALIGN_RIGHT, font.getBaseFont());
    }

    /**
     * Secure with Password and limit rights.<br>
     * Must be called before opening the Document.
     *
     * @param userPassword byte[], null = No Question during opening
     * @param ownerPassword byte[], null = No Question during changes
     */
    protected void secure(final PdfWriter writer, final byte[] userPassword, final byte[] ownerPassword, final int permissions) throws DocumentException {
        writer.setEncryption(userPassword, ownerPassword, permissions, PdfWriter.ENCRYPTION_AES_128);
    }

    /**
     * Secure with Password and read only rights (PdfWriter.ALLOW_PRINTING | PdfWriter.ALLOW_SCREENREADERS).<br>
     * Must be called before opening the Document.
     *
     * @param userPassword byte[], null = No Question during opening
     * @param ownerPassword byte[], null = No Question during changes
     */
    protected void secureReadOnly(final PdfWriter writer, final byte[] userPassword, final byte[] ownerPassword) throws DocumentException {
        secure(writer, userPassword, ownerPassword, PdfWriter.ALLOW_PRINTING | PdfWriter.ALLOW_SCREENREADERS);
    }
}
