// Created: 22.09.2006
package de.freese.base.reports;

import java.awt.Color;
import java.awt.Desktop;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.function.BiConsumer;

import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
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
    private static final Font PDF_FONT_12_BOLD_BLACK = FontFactory.getFont(FontFactory.HELVETICA, 12F, Font.BOLD, Color.BLACK);

    private static final Font PDF_FONT_12_PLAIN_BLACK = FontFactory.getFont(FontFactory.HELVETICA, 12F, Font.NORMAL, Color.BLACK);

    // BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);

    public static void main(final String[] args) throws Exception
    {
        Exporter<Void> exporter = new AbstractPdfExporter<>()
        {
            @Override
            public void export(final OutputStream outputStream, final BiConsumer<Long, Long> progressCallback, final Void model) throws Exception
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

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                drawTextFussZeile("Fusszeile: " + LocalDateTime.now().format(formatter));
            }

            @Override
            protected Font getDefaultFont()
            {
                return PDF_FONT_12_PLAIN_BLACK;
            }

            @Override
            protected Font getDefaultFontBold()
            {
                return PDF_FONT_12_BOLD_BLACK;
            }
        };

        final Path filePath = Paths.get(System.getProperty("java.io.tmpdir"), "test.pdf");
        exporter.export(filePath, ProgressCallback.EMPTY, null);

        Runnable task = () ->
        {
            try
            {
                Desktop.getDesktop().open(filePath.toFile());
            }
            catch (IOException ex)
            {
                ex.printStackTrace();
            }
        };

        task.run();

        //        Thread.startVirtualThread(task);
        //
        //        ForkJoinPool.commonPool().execute(task);
        //
        //        Thread thread = new Thread(task);
        //        thread.setDaemon(true);
        //        thread.start();
        //
        //        TimeUnit.SECONDS.sleep(5);
    }

    private static PdfPCell createCell(String text)
    {
        PdfPCell cell = new PdfPCell();
        cell.setNoWrap(true);
        cell.setFixedHeight(20F);
        cell.setPadding(0.0F);
        cell.setPaddingTop(0.5F);
        cell.setPaddingLeft(0.5F);
        cell.setPaddingBottom(1.0F);
        cell.setPaddingRight(0.5F);
        cell.setUseBorderPadding(true);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setBorderWidth(0.5F);

        Paragraph paragraph = new Paragraph(text, PDF_FONT_12_PLAIN_BLACK);
        cell.setPhrase(paragraph);

        return cell;
    }

    private TestPdfExporterMain()
    {
        super();
    }
}
