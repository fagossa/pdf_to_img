package fr.xebia.transform;

import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Optional;
import java.util.function.BiFunction;

import static javax.print.attribute.ResolutionSyntax.DPI;

public class PageToBufferedImage implements BiFunction<PDFRenderer, Integer, Optional<BufferedImage>> {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public Optional<BufferedImage> apply(PDFRenderer pdfRenderer, Integer page) {
        try {
            return Optional.ofNullable(pdfRenderer.renderImageWithDPI(page, DPI, ImageType.RGB));
        } catch (IOException e) {
            logger.error("Error extracting pdf page to buffered image", e);
            return Optional.empty();
        }
    }

}
