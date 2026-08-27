package de.freese.base.utils;

import java.io.FileOutputStream;
import java.io.OutputStream;

/**
 * @author Thomas Freese
 * @since 13.11.2022
 */
public final class SplitPdfMain {
    static void main() throws Exception {
        final SplitPdf splitPdf = new SplitPdf("TEST.pdf");

        final String[] ranges = new String[]{"1-1", "2-3", "5-11"};

        try (OutputStream os1 = new FileOutputStream("Test_1-1.pdf");
             OutputStream os2 = new FileOutputStream("Test_2-3.pdf");
             OutputStream os3 = new FileOutputStream("Test_5-11.pdf");
             OutputStream os4 = new FileOutputStream("Test_Bundle.pdf")) {
            final OutputStream[] outputStreams = new OutputStream[]{os1, os2, os3};

            splitPdf.split(ranges, outputStreams);
            splitPdf.split(ranges, os4);
        }
    }

    private SplitPdfMain() {
        super();
    }
}
