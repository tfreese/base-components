package de.freese.base.utils;

import java.io.FileOutputStream;
import java.io.OutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfWriter;
import de.freese.base.reports.exporter.pdf.DocumentMetaData;

/**
 * Extrahiert aus einer PDF-Datei frei waehlbare Bereiche von Seiten als neue PDF-Dateien.
 *
 * @author Thomas Freese
 */
public class SplitPDF
{
    /**
     *
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(SplitPDF.class);

    /**
     * Zum Testen
     *
     * @param args String[]
     * @throws Exception Falls was schief geht.
     */
    public static void main(final String[] args) throws Exception
    {
        SplitPDF splitPdf = new SplitPDF("TEST.pdf");

        String[] ranges = new String[]
        {
                "1-1", "2-3", "5-11"
        };

        try (OutputStream os1 = new FileOutputStream("Test_1-1.pdf");
             OutputStream os2 = new FileOutputStream("Test_2-3.pdf");
             OutputStream os3 = new FileOutputStream("Test_5-11.pdf");
             OutputStream os4 = new FileOutputStream("Test_Bundle.pdf"))
        {
            OutputStream[] outputStreams = new OutputStream[]
            {
                    os1, os2, os3
            };

            splitPdf.split(ranges, outputStreams, null);
            splitPdf.split(ranges, os4, null);
        }
    }

    /**
     *
     */
    private byte[] pdfFile;

    /**
     *
     */
    private String pdfFileName;

    /**
     * Creates a new {@link SplitPDF} object.
     *
     * @param pdfFile byte[]
     * @throws IllegalArgumentException Falls was schief geht.
     */
    public SplitPDF(final byte[] pdfFile)
    {
        super();

        if (pdfFile == null)
        {
            throw new IllegalArgumentException("PDF Filename is NULL !!!");
        }

        this.pdfFile = pdfFile;
    }

    /**
     * Creates a new {@link SplitPDF} object.
     *
     * @param pdfFileName String
     * @throws IllegalArgumentException Falls was schief geht.
     */
    public SplitPDF(final String pdfFileName)
    {
        super();

        if (pdfFileName == null)
        {
            throw new IllegalArgumentException("PDF Filename is NULL !!!");
        }

        this.pdfFileName = pdfFileName;
    }

    /**
     * Liefert den PdfReader in Abhaengigkeit der Eingangsparameter.
     *
     * @return {@link PdfReader}
     * @throws Exception Falls was schief geht.
     */
    private PdfReader getPDFReader() throws Exception
    {
        if (this.pdfFileName != null)
        {
            return new PdfReader(this.pdfFileName);
        }
        else if (this.pdfFile != null)
        {
            return new PdfReader(this.pdfFile);
        }

        return null;
    }

    /**
     * Extrahiert aus einer PDF-Datei frei waehlbare Bereiche von Seiten als neues PDF-Dokument.
     *
     * @param ranges StringArray mit dem Format 2-4, 5-7 ...
     * @param outStream {@link OutputStream}
     * @param metaData {@link DocumentMetaData} (optional)
     * @throws Exception Falls was schief geht
     * @throws IllegalArgumentException Bei ungueltigen Parametern
     */
    public void split(final String[] ranges, final OutputStream outStream, final DocumentMetaData metaData) throws Exception
    {
        if ((ranges == null) || (outStream == null))
        {
            throw new IllegalArgumentException("Parameter is NULL !!!");
        }

        // OriginalPDF
        PdfReader pdfReader = getPDFReader();
        int pages = pdfReader.getNumberOfPages();
        LOGGER.info("There are {} pages in the original file.", pages);

        // Neues Dokument erzeugen.
        Document newDoc = new Document(pdfReader.getPageSizeWithRotation(1));

        PdfWriter newPdfWriter = PdfWriter.getInstance(newDoc, outStream);
        newPdfWriter.setFullCompression();

        if (metaData != null)
        {
            newDoc.addTitle(metaData.getTitle());
            newDoc.addSubject(metaData.getSubject());
            newDoc.addKeywords(metaData.getKeywords());
            newDoc.addCreator(metaData.getCreator());
            newDoc.addAuthor(metaData.getAuthor());
        }

        newDoc.open();

        // Durch das RangeArray gehen.
        for (String range : ranges)
        {
            // Range in konkrete Zahlen wandeln.
            String[] splits = range.split("[-]");

            int startPage = Integer.parseInt(splits[0]);
            int endPage = Integer.parseInt(splits[1]);

            if ((startPage > pages) || (endPage > pages))
            {
                LOGGER.error("Start-/Endpage {} reaches total Pagesize {}, skip splitting.", range, pages);

                continue;
            }

            LOGGER.info("Splitting Range {}", range);

            // Inhalt des Originals holen.
            PdfContentByte pdfContentByte = newPdfWriter.getDirectContent();

            for (int i = startPage; i <= endPage; i++)
            {
                // Seitengroesse des Originals setzen.
                newDoc.setPageSize(pdfReader.getPageSizeWithRotation(i));

                // Neue Seite einfuegen.
                newDoc.newPage();

                // Seite des Originals importieren.
                PdfImportedPage pdfimportedpage = newPdfWriter.getImportedPage(pdfReader, i);

                // Seitenausrichtung des Originals einlesen.
                int rotation = pdfReader.getPageRotation(i);

                // Seiteninhalt des Originals importieren.
                if ((rotation == 90) || (rotation == 270))
                {
                    pdfContentByte.addTemplate(pdfimportedpage, 0.0F, -1F, 1.0F, 0.0F, 0.0F, pdfReader.getPageSizeWithRotation(i).getHeight());
                }
                else
                {
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
     * Extrahiert aus einer PDF-Datei frei waehlbare Bereiche von Seiten als neue PDF-Dokumente.
     *
     * @param ranges StringArray mit dem Format 2-4, 5-7 ...
     * @param outStreams OutputStream[] fuer jede erzeuge Datei eines Range-Bereiches
     * @param metaData {@link DocumentMetaData}[] (optional)
     * @throws Exception Falls was schief geht
     * @throws IllegalArgumentException Bei ungueltigen Parametern
     */
    public void split(final String[] ranges, final OutputStream[] outStreams, final DocumentMetaData[] metaData) throws Exception
    {
        if ((ranges == null) || (outStreams == null))
        {
            throw new IllegalArgumentException("Parameter is NULL !!!");
        }

        if (ranges.length != outStreams.length)
        {
            throw new IllegalArgumentException("Different Array Length !!!");
        }

        // OriginalPDF
        PdfReader pdfReader = getPDFReader();
        int pages = pdfReader.getNumberOfPages();
        LOGGER.info("There are {} pages in the original file.", pages);

        // Durch das RangeArray gehen.
        for (int r = 0; r < ranges.length; r++)
        {
            String range = ranges[r];

            // Range in konkrete Zahlen wandeln.
            String[] splits = range.split("[-]");

            int startPage = Integer.parseInt(splits[0]);
            int endPage = Integer.parseInt(splits[1]);

            if ((startPage > pages) || (endPage > pages))
            {
                LOGGER.error("Start-/Endpage {} reaches total Pagesize {}, skip splitting.", ranges[r], pages);

                continue;
            }

            LOGGER.info("Splitting Range {}", range);

            // Neues Dokument erzeugen.
            Document newDoc = new Document(pdfReader.getPageSizeWithRotation(1));

            PdfWriter newPdfWriter = PdfWriter.getInstance(newDoc, outStreams[r]);
            newPdfWriter.setFullCompression();

            if (metaData[r] != null)
            {
                newDoc.addTitle(metaData[r].getTitle());
                newDoc.addSubject(metaData[r].getSubject());
                newDoc.addKeywords(metaData[r].getKeywords());
                newDoc.addCreator(metaData[r].getCreator());
                newDoc.addAuthor(metaData[r].getAuthor());
            }

            newDoc.open();

            // Inhalt des Originals holen.
            PdfContentByte pdfContentByte = newPdfWriter.getDirectContent();

            for (int i = startPage; i <= endPage; i++)
            {
                // Seitengroesse des Originals setzen.
                newDoc.setPageSize(pdfReader.getPageSizeWithRotation(i));

                // Neue Seite einfuegen.
                newDoc.newPage();

                // Seite des Originals importieren.
                PdfImportedPage pdfimportedpage = newPdfWriter.getImportedPage(pdfReader, i);

                // Seitenausrichtung des Originals einlesen.
                int rotation = pdfReader.getPageRotation(i);

                // Seiteninhalt des Originals importieren
                if ((rotation == 90) || (rotation == 270))
                {
                    pdfContentByte.addTemplate(pdfimportedpage, 0.0F, -1F, 1.0F, 0.0F, 0.0F, pdfReader.getPageSizeWithRotation(i).getHeight());
                }
                else
                {
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
}
