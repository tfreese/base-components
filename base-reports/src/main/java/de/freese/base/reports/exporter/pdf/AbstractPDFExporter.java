// Created: 11.08.2010
package de.freese.base.reports.exporter.pdf;

import java.awt.Insets;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfWriter;
import de.freese.base.core.progress.ProgressCallback;
import de.freese.base.reports.exporter.AbstractExporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Basisklasse eines PDF-Exporters.
 *
 * @author Thomas Freese
 */
public abstract class AbstractPDFExporter extends AbstractExporter
{
    /**
     *
     */
    private final Logger logger = LoggerFactory.getLogger(getClass());
    /**
     *
     */
    private BaseFont baseFont;
    /**
     *
     */
    private BaseFont baseFontBold;
    /**
     *
     */
    private Document document;
    /**
     *
     */
    private OutputStream outputStreamIntern;
    /**
     *
     */
    private PdfWriter writer;

    /**
     * Schliesst das {@link Document} und den {@link PdfWriter}.
     *
     * @throws Exception Falls was schief geht.
     */
    public void closeDocumentAndWriter() throws Exception
    {
        getDocument().close();
        getWriter().close();

        if (this.outputStreamIntern != null)
        {
            this.outputStreamIntern.close();
        }
    }

    /**
     * Erzeugt das {@link Document} und den {@link PdfWriter} und ruft die {@link #configure(Document, PdfWriter, DocumentMetaData)} Methode auf.
     *
     * @param outputStream {@link OutputStream}
     * @param metaData {@link DocumentMetaData}
     *
     * @throws DocumentException Falls was schief geht.
     * @see #setDocument(Document)
     * @see #setWriter(PdfWriter)
     */
    public final void createDocumentAndWriter(final OutputStream outputStream, final DocumentMetaData metaData) throws DocumentException
    {
        setDocument(new Document());
        setWriter(PdfWriter.getInstance(getDocument(), outputStream));

        getWriter().setPdfVersion(PdfWriter.VERSION_1_5);

        // setFullCompression setzt automatisch die PDF-Version auf 1.5
        getWriter().setFullCompression();

        configure(getDocument(), getWriter(), metaData);
    }

    /**
     * @see de.freese.base.reports.exporter.AbstractExporter#export(java.lang.String, de.freese.base.core.progress.ProgressCallback, java.lang.Object)
     */
    @Override
    public void export(final String fileName, final ProgressCallback progressCallback, final Object model) throws Exception
    {
        this.outputStreamIntern = new BufferedOutputStream(new FileOutputStream(fileName));

        export(this.outputStreamIntern, progressCallback, model);
    }

    /**
     * Setzt bestehendes {@link Document}.
     *
     * @param document {@link Document}
     *
     * @see #createDocumentAndWriter(OutputStream, DocumentMetaData)
     */
    public void setDocument(final Document document)
    {
        this.document = document;
    }

    /**
     * Setzt bestehenden {@link PdfWriter}.
     *
     * @param writer {@link PdfWriter}
     *
     * @see #createDocumentAndWriter(OutputStream, DocumentMetaData)
     */
    public void setWriter(final PdfWriter writer)
    {
        this.writer = writer;
    }

    /**
     * Konfiguriert das Dokument und den Writer.<br>
     * Defaults: A4 Portrait, Margins (40, 20, 20, 20)
     *
     * @param document {@link Document}
     * @param writer {@link PdfWriter}
     * @param metaData {@link DocumentMetaData}
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
     * @param xOffset float
     * @param yOffset float
     * @param text String, muss DatumsFormat enthalten String.format(...)
     * @param datum {@link Date}
     *
     * @throws DocumentException Falls was schief geht.
     * @throws IOException Falls was schief geht.
     */
    protected void createFussZeile(final float xOffset, final float yOffset, final String text, final Date datum) throws DocumentException, IOException
    {
        PdfContentByte contentByte = this.writer.getDirectContent();

        float x = getMaxX() - xOffset;
        float y = getMinY() - yOffset;

        contentByte.beginText();
        contentByte.setFontAndSize(getBaseFont(), 8);
        contentByte.setTextMatrix(x, y);
        contentByte.showText(String.format(text, datum));
        contentByte.endText();
    }

    /**
     * Malt eine Linie mit optionaler Angabe der Farbe.
     *
     * @param x1 float
     * @param y1 float
     * @param x2 float
     * @param y2 float
     * @param strokeColor {@link BaseColor}, optional, wenn null wird gesetzte Farbe verwendet.
     */
    protected void drawLine(final float x1, final float y1, final float x2, final float y2, final BaseColor strokeColor)
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
     * Malt ein Rechteck mit optionaler Angabe der Farben.
     *
     * @param x float
     * @param y float
     * @param width float
     * @param height float
     * @param fillColor {@link BaseColor}, optional, wenn null wird gesetzte Farbe verwendet.
     * @param borderColor {@link BaseColor}, optional, wenn null wird gesetzte Farbe verwendet.
     */
    protected void drawRectangle(final float x, final float y, final float width, final float height, final BaseColor fillColor, final BaseColor borderColor)
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
     * Malt den Text mit der vorher gesetzten Farbe und dem Default-Font.
     *
     * @param text String
     * @param x float
     * @param y float
     * @param fontSize int
     * @param align int, @see {@link PdfContentByte#ALIGN_LEFT} ...
     *
     * @throws IOException Falls was schief geht.
     * @throws DocumentException Falls was schief geht.
     */
    protected void drawText(final String text, final float x, final float y, final int fontSize, final int align) throws DocumentException, IOException
    {
        drawText(text, x, y, fontSize, align, getBaseFont());
    }

    /**
     * Malt den Text mit der vorher gesetzten Farbe.
     *
     * @param text String
     * @param x float
     * @param y float
     * @param fontSize int
     * @param align int, @see {@link PdfContentByte#ALIGN_LEFT} ...
     * @param font {@link BaseFont}
     */
    protected void drawText(final String text, final float x, final float y, final int fontSize, final int align, final BaseFont font)
    {
        PdfContentByte contentByte = getWriter().getDirectContent();
        contentByte.beginText();
        contentByte.setFontAndSize(font, fontSize);
        contentByte.showTextAligned(align, text, x, y, 0);
        contentByte.endText();
    }

    /**
     * Liefert den {@link BaseFont}
     *
     * @return {@link BaseFont}
     *
     * @throws IOException Falls was schief geht.
     * @throws DocumentException Falls was schief geht.
     */
    protected BaseFont getBaseFont() throws DocumentException, IOException
    {
        if (this.baseFont == null)
        {
            this.baseFont = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
        }

        return this.baseFont;
    }

    /**
     * Liefert den Bold {@link BaseFont}
     *
     * @return {@link BaseFont}
     *
     * @throws IOException Falls was schief geht.
     * @throws DocumentException Falls was schief geht.
     */
    protected BaseFont getBaseFontBold() throws DocumentException, IOException
    {
        if (this.baseFontBold == null)
        {
            this.baseFontBold = BaseFont.createFont(BaseFont.HELVETICA_BOLD, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
        }

        return this.baseFontBold;
    }

    /**
     * @return {@link Document}
     */
    protected final Document getDocument()
    {
        return this.document;
    }

    /**
     * @return {@link Logger}
     */
    protected final Logger getLogger()
    {
        return this.logger;
    }

    /**
     * Liefert die grösste X Koordinate unter Berücksichtigung des rechten Randes.
     *
     * @return float
     */
    protected final float getMaxX()
    {
        return getDocument().getPageSize().getWidth() - getDocument().rightMargin();
    }

    /**
     * Liefert die grösste Y Koordinate unter Berücksichtigung des oberen Randes.
     *
     * @return float
     */
    protected final float getMaxY()
    {
        return getDocument().getPageSize().getHeight() - getDocument().topMargin();
    }

    /**
     * Liefert die kleinste X Koordinate unter Berücksichtigung des linken Randes.
     *
     * @return float
     */
    protected final float getMinX()
    {
        return getDocument().leftMargin();
    }

    /**
     * Liefert die kleinste Y Koordinate unter Berücksichtigung des unteren Randes.
     *
     * @return float
     */
    protected final float getMinY()
    {
        return getDocument().bottomMargin();
    }

    /**
     * @return {@link PdfWriter}
     */
    protected final PdfWriter getWriter()
    {
        return this.writer;
    }

    /**
     * Liefert die X Koordinate absolut zum Ursprung (minX).
     *
     * @param offset int
     *
     * @return float
     */
    protected final float getX(final int offset)
    {
        return getMinX() + offset;
    }

    /**
     * Liefert die X Koordinate relativ zum Ursprung (minX).
     *
     * @param prozent float, 0...100
     *
     * @return float
     */
    protected final float getXRelative(final float prozent)
    {
        return ((getMaxX() - getMinX()) * (prozent / 100)) + getMinX();
    }

    /**
     * Liefert die Y Koordinate absolut zum Ursprung (minY).
     *
     * @param offset int
     *
     * @return float
     */
    protected final float getY(final int offset)
    {
        return getMinY() + offset;
    }

    /**
     * Liefert die Y Koordinate relativ zum Ursprung (minY).
     *
     * @param prozent float, 0...100
     *
     * @return float
     */
    protected final float getYRelative(final float prozent)
    {
        return ((getMaxY() - getMinY()) * (prozent / 100)) + getMinY();
    }

    /**
     * Mit Passwort versehen und Rechte einschränken.
     *
     * @param writer {@link PdfWriter}
     * @param userPassword String, null = Keine Abfrage beim Öffnen
     * @param ownerPassword String, Abfrage beim Andern
     *
     * @throws DocumentException Falls was schief geht.
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
