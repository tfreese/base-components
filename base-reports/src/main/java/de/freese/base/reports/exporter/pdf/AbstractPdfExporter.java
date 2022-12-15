// Created: 11.08.2010
package de.freese.base.reports.exporter.pdf;

import java.awt.Color;
import java.awt.Insets;
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
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfWriter;
import de.freese.base.reports.exporter.AbstractExporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Basisklasse eines PDF-Exporters.
 *
 * @author Thomas Freese
 */
public abstract class AbstractPdfExporter<T> extends AbstractExporter<T>
{
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private Document document;

    private PdfWriter writer;

    /**
     * @see #configure(Document, PdfWriter, DocumentMetaData)
     */
    public final void createDocumentAndWriter(final OutputStream outputStream, final DocumentMetaData metaData) throws DocumentException
    {
        this.document = new Document();
        this.writer = PdfWriter.getInstance(getDocument(), outputStream);

        getWriter().setPdfVersion(PdfWriter.VERSION_1_5);

        // setFullCompression setzt automatisch die PDF-Version auf 1.5
        getWriter().setFullCompression();

        configure(getDocument(), getWriter(), metaData);
    }

    @Override
    public void export(final Path filePath, final BiConsumer<Integer, Integer> progressCallback, final T model) throws Exception
    {
        try (OutputStream outputStream = new BufferedOutputStream(Files.newOutputStream(filePath)))
        {
            export(outputStream, progressCallback, model);

            getDocument().close();
            getWriter().close();
        }
    }

    /**
     * Defaults: A4 Portrait, Margins (40, 20, 20, 20)
     */
    protected void configure(final Document document, final PdfWriter writer, final DocumentMetaData metaData)
    {
        document.setPageSize(metaData.getPageSize());

        document.addKeywords(metaData.getKeywords());
        document.addCreator(metaData.getCreator());
        document.addAuthor(metaData.getAuthor());
        document.addSubject(metaData.getSubject());
        document.addTitle(metaData.getTitle());

        // left, right, top, bottom
        Insets margins = metaData.getMargins();
        document.setMargins(margins.left, margins.right, margins.top, margins.bottom);
    }

    /**
     * @param strokeColor {@link Color}, optional, wenn null wird gesetzte Farbe verwendet.
     */
    protected void drawLine(final float x1, final float y1, final float x2, final float y2, final Color strokeColor)
    {
        PdfContentByte contentByte = getWriter().getDirectContent();
        contentByte.saveState();

        if (strokeColor != null)
        {
            contentByte.setColorStroke(strokeColor);
        }

        contentByte.moveTo(x1, y2);
        contentByte.lineTo(x2, y2);
        contentByte.stroke();
        contentByte.restoreState();
    }

    /**
     * @param fillColor {@link Color}, optional, wenn null wird gesetzte Farbe verwendet.
     * @param borderColor {@link Color}, optional, wenn null wird gesetzte Farbe verwendet.
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
    protected void drawText(final String text, final float x, final float y, final float fontSize, final int align) throws DocumentException, IOException
    {
        drawText(text, x, y, fontSize, align, getDefaultFont().getBaseFont());
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

    protected void drawTextFussZeile(final String text) throws DocumentException, IOException
    {
        drawText(text, getMaxX(), getMinY(), getDefaultFont().getSize(), PdfContentByte.ALIGN_RIGHT);
    }

    protected abstract Font getDefaultFont();

    protected abstract Font getDefaultFontBold();

    protected final Document getDocument()
    {
        return this.document;
    }

    protected final Logger getLogger()
    {
        return this.logger;
    }

    /**
     * Liefert die grösste X Koordinate unter Berücksichtigung des rechten Randes.
     */
    protected final float getMaxX()
    {
        return getDocument().getPageSize().getWidth() - getDocument().rightMargin();
    }

    /**
     * Liefert die grösste Y Koordinate unter Berücksichtigung des oberen Randes.
     */
    protected final float getMaxY()
    {
        return getDocument().getPageSize().getHeight() - getDocument().topMargin();
    }

    /**
     * Liefert die kleinste X Koordinate unter Berücksichtigung des linken Randes.
     */
    protected final float getMinX()
    {
        return getDocument().leftMargin();
    }

    /**
     * Liefert die kleinste Y Koordinate unter Berücksichtigung des unteren Randes.
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
     * Liefert die X Koordinate absolut zum Ursprung (minX).
     */
    protected final float getX(final int offset)
    {
        return getMinX() + offset;
    }

    /**
     * Liefert die X Koordinate relativ zum Ursprung (minX).
     *
     * @param prozent float, 0...100
     */
    protected final float getXRelative(final float prozent)
    {
        return ((getMaxX() - getMinX()) * (prozent / 100F)) + getMinX();
    }

    /**
     * Liefert die Y Koordinate absolut zum Ursprung (minY).
     */
    protected final float getY(final int offset)
    {
        return getMinY() + offset;
    }

    /**
     * Liefert die Y Koordinate relativ zum Ursprung (minY).
     *
     * @param prozent float, 0...100
     */
    protected final float getYRelative(final float prozent)
    {
        return ((getMaxY() - getMinY()) * (prozent / 100F)) + getMinY();
    }

    /**
     * Mit Passwort versehen und Rechte einschränken.
     *
     * @param userPassword String, null = Keine Abfrage beim Öffnen
     */
    protected void secure(final PdfWriter writer, final String userPassword, final String ownerPassword) throws DocumentException
    {
        // UserPassword: null = Keine Abfrage beim Öffnen
        byte[] userPwd = userPassword != null ? userPassword.getBytes(StandardCharsets.UTF_8) : null;

        // OwnerPassword: Abfrage beim Andern
        byte[] ownerPwd = ownerPassword != null ? ownerPassword.getBytes(StandardCharsets.UTF_8) : null;

        writer.setEncryption(userPwd, ownerPwd, PdfWriter.ALLOW_PRINTING | PdfWriter.ALLOW_SCREENREADERS, PdfWriter.ENCRYPTION_AES_128);
    }
}
