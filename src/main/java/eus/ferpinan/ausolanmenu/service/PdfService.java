package eus.ferpinan.ausolanmenu.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.graphics.PDXObject;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

/**
 * Service responsible for processing PDF documents.
 * <p>
 * This service utilizes the Apache PDFBox library to perform low-level operations
 * on PDF files, specifically focusing on resource extraction.
 * </p>
 */
@Service
@RequiredArgsConstructor
@Log4j2
public class PdfService {

    /**
     * Extracts the first embedded image found on the first page of a PDF document.
     *
     * @param pdfBytes The raw byte array of the PDF document.
     * @return A byte array representing the extracted image in PNG format.
     * @throws IOException If an error occurs during PDF loading or image writing.
     * @throws IllegalStateException If no image is found on the first page of the document.
     */
    public byte[] extractImageFromPdf(byte[] pdfBytes) throws IOException {
        try (PDDocument document = Loader.loadPDF(pdfBytes)) {
            PDPage page = document.getPage(0);
            PDResources resources = page.getResources();

            for (COSName name : resources.getXObjectNames()) {
                PDXObject xObject = resources.getXObject(name);
                if (xObject instanceof PDImageXObject image) {
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    ImageIO.write(image.getImage(), "PNG", outputStream);
                    return outputStream.toByteArray();
                }
            }
        }
        throw new IllegalStateException("No image found in PDF");
    }
}