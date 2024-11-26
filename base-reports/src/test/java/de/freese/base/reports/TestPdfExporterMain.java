// Created: 22.09.2006
package de.freese.base.reports;

import java.awt.Color;
import java.awt.Desktop;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import de.freese.base.reports.exporter.AbstractPdfExporter;
import de.freese.base.reports.exporter.Exporter;

/**
 * @author Thomas Freese
 */
public final class TestPdfExporterMain {
    private static final Font PDF_FONT_12_BLACK = FontFactory.getFont(FontFactory.HELVETICA, 12F, Font.NORMAL, Color.BLACK);
    private static final Font PDF_FONT_12_BLACK_BOLD = FontFactory.getFont(FontFactory.HELVETICA, 12F, Font.BOLD, Color.BLACK);

    // BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);

    public static void main(final String[] args) throws Exception {
        final Exporter<List<String>> exporter = new AbstractPdfExporter<>() {
            @Override
            public void export(final Document document, final PdfWriter writer, final List<String> model) {
                // Must be called before opening the Document.
                // secureReadOnly(writer, "test".getBytes(StandardCharsets.UTF_8), null);

                document.open();

                //  A4 Landscape
                document.setPageSize(PageSize.A4.rotate());

                // left, right, top, bottom
                document.setMargins(40, 20, 20, 20);

                //        document.addKeywords(...);
                //        document.addCreator(...);
                //        document.addAuthor(...);
                //        document.addSubject(...);
                //        document.addTitle(...);

                // setFullCompression set PDF-Version to 1.5
                writer.setFullCompression();
                writer.setPdfVersion(PdfWriter.VERSION_1_7);

                document.newPage();

                final float[] columnWidths = {40F, 80F, 40F};

                final PdfPTable table = new PdfPTable(columnWidths);
                table.setTotalWidth(160F);
                table.setLockedWidth(true);

                model.forEach(value -> table.addCell(createCell(value, PDF_FONT_12_BLACK_BOLD)));

                final PdfContentByte contentByte = writer.getDirectContent();
                table.writeSelectedRows(0, -1, getMinX(document) - 0.5F, getMaxY(document) - 20F, contentByte);

                //                getDocument().add(table);

                drawLine(writer, 200, 200, 300, 300, Color.RED);
                drawLine(writer, 200, 250, 300, 350, null);

                drawRectangle(writer, 400, 400, 100, 100, Color.BLUE, Color.MAGENTA);
                drawRectangle(writer, 400, 250, 100, 100, null, null);

                final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                drawTextFooter(document, writer, "Footer: " + LocalDateTime.now().format(formatter), PDF_FONT_12_BLACK);
            }
        };

        final Path filePath = Paths.get(System.getProperty("java.io.tmpdir"), "test.pdf");
        exporter.export(filePath, List.of("1", "2", "3", "", "4", ""));

        final Runnable task = () -> {
            try {
                Desktop.getDesktop().open(filePath.toFile());
            }
            catch (IOException ex) {
                throw new UncheckedIOException(ex);
            }
        };

        task.run();

        // Thread.startVirtualThread(task);
        //
        // ForkJoinPool.commonPool().execute(task);
        //
        // final Thread thread = new Thread(task);
        // thread.setDaemon(true);
        // thread.start();
        //
        // await().pollDelay(Duration.ofSeconds(5)).until(() -> true);
    }

    private static PdfPCell createCell(final String text, final Font font) {
        final PdfPCell cell = new PdfPCell();
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

        final Paragraph paragraph = new Paragraph(text, font);
        cell.setPhrase(paragraph);

        return cell;
    }

    private TestPdfExporterMain() {
        super();
    }
}
