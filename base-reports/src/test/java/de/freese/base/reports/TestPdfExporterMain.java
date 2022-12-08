// Created: 22.09.2006
package de.freese.base.reports;

import java.awt.Desktop;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import de.freese.base.core.progress.ProgressCallback;
import de.freese.base.reports.exporter.Exporter;
import de.freese.base.reports.exporter.pdf.AbstractPdfExporter;
import de.freese.base.reports.exporter.pdf.DocumentMetaData;

/**
 * Testklasse des Layouts.
 *
 * @author Thomas Freese
 */
public final class TestPdfExporterMain
{
    private static final Font PDF_FONT_6_PLAIN_BLACK = FontFactory.getFont(FontFactory.HELVETICA, 6, Font.NORMAL, BaseColor.BLACK);

    public static void main(final String[] args) throws Exception
    {
        Exporter<Object> exporter = new AbstractPdfExporter<>()
        {
            @Override
            public void export(final OutputStream outputStream, final ProgressCallback progressCallback, final Object model) throws Exception
            {
                createDocumentAndWriter(outputStream, new DocumentMetaData());
                getDocument().open();

                //                getDocument().newPage();

                float[] columnWidths = {40F, 80F, 40F};

                PdfPTable table = new PdfPTable(columnWidths);
                table.setTotalWidth(160F);
                table.setLockedWidth(true);

                table.addCell(createCell("1"));
                table.addCell(createCell("2"));
                table.addCell(createCell("3"));
                table.addCell(createCell(""));
                table.addCell(createCell("4"));
                table.addCell(createCell(""));

                PdfContentByte contentByte = getWriter().getDirectContent();
                table.writeSelectedRows(0, -1, getMinX() - 0.5F, getMaxY() - 20F, contentByte);

                //                getDocument().add(table);
            }
        };

        Path filePath = Paths.get(System.getProperty("java.io.tmpdir"), "test.pdf");
        exporter.export(filePath, percentage ->
        {
        }, null);

        Desktop.getDesktop().open(filePath.toFile());
    }

    private static PdfPCell createCell(String text)
    {
        PdfPCell cell = new PdfPCell();
        cell.setNoWrap(true);
        cell.setFixedHeight(10F);
        cell.setPadding(0.0F);
        cell.setPaddingTop(0.5F);
        cell.setPaddingLeft(0.5F);
        cell.setPaddingBottom(1.0F);
        cell.setPaddingRight(0.5F);
        cell.setUseBorderPadding(true);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setBorderWidth(0.5F);

        Paragraph paragraph = new Paragraph(text, PDF_FONT_6_PLAIN_BLACK);
        cell.setPhrase(paragraph);

        return cell;
    }

    private TestPdfExporterMain()
    {
        super();
    }
}
