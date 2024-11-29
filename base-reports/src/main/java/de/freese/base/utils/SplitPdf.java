package de.freese.base.utils;

import java.io.OutputStream;
import java.util.Objects;

import com.lowagie.text.Document;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfImportedPage;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Extrahiert aus einer PDF-Datei frei wählbare Bereiche von Seiten als neue PDF-Dateien.
 *
 * @author Thomas Freese
 */
public class SplitPdf {
    private static final Logger LOGGER = LoggerFactory.getLogger(SplitPdf.class);

    private byte[] pdfFile;
    private String pdfFileName;

    public SplitPdf(final byte[] pdfFile) {
        super();

        this.pdfFile = Objects.requireNonNull(pdfFile, "pdfFile required");
    }

    public SplitPdf(final String pdfFileName) {
        super();

        this.pdfFileName = Objects.requireNonNull(pdfFileName, "pdfFileName required");
    }

    /**
     * Extrahiert aus einer PDF-Datei frei wählbare Bereiche von Seiten als neues PDF-Dokument.
     *
     * @param ranges StringArray mit dem Format 2-4, 5-7 ...
     */
    public void split(final String[] ranges, final OutputStream outStream) throws Exception {
        if (ranges == null || outStream == null) {
            throw new IllegalArgumentException("Parameter is NULL !!!");
        }

        // OriginalPDF
        final PdfReader pdfReader = getPDFReader();
        final int pages = pdfReader.getNumberOfPages();
        LOGGER.info("There are {} pages in the original file.", pages);

        // Neues Dokument erzeugen.
        final Document newDoc = new Document(pdfReader.getPageSizeWithRotation(1));

        final PdfWriter newPdfWriter = PdfWriter.getInstance(newDoc, outStream);
        newPdfWriter.setFullCompression();

        // newDoc.addTitle(...);
        // newDoc.addSubject(...);
        // newDoc.addKeywords(...);
        // newDoc.addCreator(...);
        // newDoc.addAuthor(...);

        newDoc.open();

        // Durch das RangeArray gehen.
        for (String range : ranges) {
            // Range in konkrete Zahlen wandeln.
            final String[] splits = range.split("-");

            final int startPage = Integer.parseInt(splits[0]);
            final int endPage = Integer.parseInt(splits[1]);

            if (startPage > pages || endPage > pages) {
                LOGGER.error("Start-/Endpage {} reaches total page size {}, skip splitting.", range, pages);

                continue;
            }

            LOGGER.info("Splitting Range {}", range);

            // Inhalt des Originals holen.
            final PdfContentByte pdfContentByte = newPdfWriter.getDirectContent();

            for (int i = startPage; i <= endPage; i++) {
                // Seitengrösse des Originals setzen.
                newDoc.setPageSize(pdfReader.getPageSizeWithRotation(i));

                // Neue Seite einfügen.
                newDoc.newPage();

                // Seite des Originals importieren.
                final PdfImportedPage pdfimportedpage = newPdfWriter.getImportedPage(pdfReader, i);

                // Seitenausrichtung des Originals einlesen.
                final int rotation = pdfReader.getPageRotation(i);

                // Seiteninhalt des Originals importieren.
                if (rotation == 90 || rotation == 270) {
                    pdfContentByte.addTemplate(pdfimportedpage, 0.0F, -1F, 1.0F, 0.0F, 0.0F, pdfReader.getPageSizeWithRotation(i).getHeight());
                }
                else {
                    pdfContentByte.addTemplate(pdfimportedpage, 1.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F);
                }
            }
        }

        // Neues Dokument schliessen.
        newDoc.close();
        newPdfWriter.close();
        outStream.close();
        LOGGER.info("New PDF-File created.");
    }

    /**
     * Extrahiert aus einer PDF-Datei frei wählbare Bereiche von Seiten als neue PDF-Dokumente.
     *
     * @param ranges StringArray mit dem Format 2-4, 5-7 ...
     */
    public void split(final String[] ranges, final OutputStream[] outStreams) throws Exception {
        if (ranges == null || outStreams == null) {
            throw new IllegalArgumentException("Parameter is NULL !!!");
        }

        if (ranges.length != outStreams.length) {
            throw new IllegalArgumentException("Different Array Length !!!");
        }

        // OriginalPDF
        final PdfReader pdfReader = getPDFReader();
        final int pages = pdfReader.getNumberOfPages();
        LOGGER.info("There are {} pages in the original file.", pages);

        // Durch das RangeArray gehen.
        for (int r = 0; r < ranges.length; r++) {
            final String range = ranges[r];

            // Range in konkrete Zahlen wandeln.
            final String[] splits = range.split("-");

            final int startPage = Integer.parseInt(splits[0]);
            final int endPage = Integer.parseInt(splits[1]);

            if (startPage > pages || endPage > pages) {
                LOGGER.error("Start-/Endpage {} reaches total page size {}, skip splitting.", ranges[r], pages);

                continue;
            }

            LOGGER.info("Splitting Range {}", range);

            // Neues Dokument erzeugen.
            final Document newDoc = new Document(pdfReader.getPageSizeWithRotation(1));

            final PdfWriter newPdfWriter = PdfWriter.getInstance(newDoc, outStreams[r]);
            newPdfWriter.setFullCompression();

            //            newDoc.addTitle(...);
            //            newDoc.addSubject(...);
            //            newDoc.addKeywords(...);
            //            newDoc.addCreator(...);
            //            newDoc.addAuthor(...);

            newDoc.open();

            // Inhalt des Originals holen.
            final PdfContentByte pdfContentByte = newPdfWriter.getDirectContent();

            for (int i = startPage; i <= endPage; i++) {
                // Seitengrösse des Originals setzen.
                newDoc.setPageSize(pdfReader.getPageSizeWithRotation(i));

                // Neue Seite einfügen.
                newDoc.newPage();

                // Seite des Originals importieren.
                final PdfImportedPage pdfimportedpage = newPdfWriter.getImportedPage(pdfReader, i);

                // Seitenausrichtung des Originals einlesen.
                final int rotation = pdfReader.getPageRotation(i);

                // Seiteninhalt des Originals importieren
                if (rotation == 90 || rotation == 270) {
                    pdfContentByte.addTemplate(pdfimportedpage, 0.0F, -1F, 1.0F, 0.0F, 0.0F, pdfReader.getPageSizeWithRotation(i).getHeight());
                }
                else {
                    pdfContentByte.addTemplate(pdfimportedpage, 1.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F);
                }
            }

            // Neues Dokument schliessen.
            newDoc.close();
            newPdfWriter.close();
            outStreams[r].close();
            LOGGER.info("New PDF-File created");
        }
    }

    private PdfReader getPDFReader() throws Exception {
        if (pdfFileName != null) {
            return new PdfReader(pdfFileName);
        }
        else if (pdfFile != null) {
            return new PdfReader(pdfFile);
        }

        throw new IllegalStateException("pdfFileName or pdfFile required");
    }
}
