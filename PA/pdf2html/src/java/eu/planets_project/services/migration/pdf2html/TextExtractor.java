package eu.planets_project.services.migration.pdf2html;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.pdfbox.exceptions.CryptographyException;
import org.pdfbox.exceptions.InvalidPasswordException;
import org.pdfbox.pdmodel.PDDocument;
import org.pdfbox.util.PDFTextStripperByArea;

public class TextExtractor {

    public static String getText(InputStream in) throws IOException, CryptographyException, InvalidPasswordException {
        PDDocument doc = PDDocument.load(in);
        String res = "";
        try {
            res = getText(doc);
        } finally {
            doc.close();
        }
        return res;
    }

    public static String getText(PDDocument document) throws CryptographyException, IOException, InvalidPasswordException {
        if( document.isEncrypted() ) {
            document.decrypt("");
        }

        PDFTextStripperByArea stripper = new PDFTextStripperByArea();
        stripper.setSortByPosition( true );


        return stripper.getText(document);
    }

}
