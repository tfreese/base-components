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

import org.openpdf.text.Document;
import org.openpdf.text.Element;
import org.openpdf.text.Font;
import org.openpdf.text.FontFactory;
import org.openpdf.text.PageSize;
import org.openpdf.text.Phrase;
import org.openpdf.text.pdf.PdfContentByte;
import org.openpdf.text.pdf.PdfDate;
import org.openpdf.text.pdf.PdfPCell;
import org.openpdf.text.pdf.PdfPTable;
import org.openpdf.text.pdf.PdfPageEventHelper;
import org.openpdf.text.pdf.PdfWriter;

import de.freese.base.reports.exporter.AbstractPdfExporter;
import de.freese.base.reports.exporter.Exporter;

/**
 * @author Thomas Freese
 */
public final class PdfExporterDemo {
    private static final Font PDF_FONT_12_BLACK = FontFactory.getFont(FontFactory.HELVETICA, 12F, Font.NORMAL, Color.BLACK);
    private static final Font PDF_FONT_12_BLACK_BOLD = FontFactory.getFont(FontFactory.HELVETICA, 12F, Font.BOLD, Color.BLACK);

    // BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);

    static void main() throws Exception {
        final Exporter<List<String>> exporter = new AbstractPdfExporter<>() {
            @Override
            public void export(final Document document, final PdfWriter writer, final List<String> model) {
                // Must be called before opening the Document.
                // secureReadOnly(writer, "test".getBytes(StandardCharsets.UTF_8), null);

                //  A4 Landscape
                document.setPageSize(PageSize.A4.rotate());

                // left, right, top, bottom
                document.setMargins(40F, 20F, 20F, 20F);

                // setFullCompression set PDF-Version to 1.5
                writer.setFullCompression();
                writer.setPdfVersion(PdfWriter.VERSION_2_0);

                // MetaData
                document.addTitle("PDF-Demo");
                document.addSubject("My PDF-Demo");
                document.addKeywords("My, PDF, Demo");
                document.addAuthor("It's me");
                document.addCreator("Myself");
                document.addHeader("header-name", "header-value");
                document.addCreationDate(new PdfDate());

                writer.setPageEvent(new PdfPageEventHelper() {
                    private int pageNumber;

                    @Override
                    public void onEndPage(final PdfWriter writer, final Document document) {
                        // final Paragraph paragraph = new Paragraph("Header", PDF_FONT_12_BLACK);
                        // document.add(paragraph);
                        final Phrase phrase = new Phrase("Header", PDF_FONT_12_BLACK);
                        document.add(phrase);

                        drawTextHeader(document, writer, "Page " + document.getPageNumber(), PDF_FONT_12_BLACK);

                        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                        final String footer = "Footer: %s - %d - %d".formatted(LocalDateTime.now().format(formatter), document.getPageNumber(), pageNumber);
                        drawTextFooter(document, writer, footer, PDF_FONT_12_BLACK);
                    }

                    @Override
                    public void onStartPage(final PdfWriter writer, final Document document) {
                        pageNumber += 1;
                    }
                });

                document.open();

                document.newPage();

                final float[] columnWidths = {40F, 80F, 40F};

                final PdfPTable table = new PdfPTable(columnWidths);
                table.setTotalWidth(160F);
                table.setLockedWidth(true);

                model.forEach(value -> table.addCell(createCell(value, PDF_FONT_12_BLACK_BOLD)));

                final PdfContentByte contentByte = writer.getDirectContent();
                table.writeSelectedRows(0, -1, getMinX(document) - 0.5F, getMaxY(document) - 20F, contentByte);

                // document.add(table);

                drawLine(writer, 200F, 200F, 300F, 300F, Color.RED);
                drawLine(writer, 200F, 250F, 300F, 350F, null);

                drawRectangle(writer, 400F, 400F, 100F, 100F, Color.BLUE, Color.MAGENTA);
                drawRectangle(writer, 400F, 250F, 100F, 100F, null, null);
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

        final Phrase phrase = new Phrase(text, font);
        cell.setPhrase(phrase);

        return cell;
    }

    private PdfExporterDemo() {
        super();
    }
}
