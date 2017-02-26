package fr.xebia.img;

import fr.xebia.export.FileExporter;
import fr.xebia.filter.TriPredicate;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;


public class ImageExtractor {

    private String sourceDirectory;
    private String destinationDirectory;

    private Logger logger = LoggerFactory.getLogger(getClass());

    public ImageExtractor(String sourceDirectory, String destinationDirectory) {
        this.sourceDirectory = sourceDirectory;
        this.destinationDirectory = destinationDirectory;
    }

    private boolean isPdfFile(Path path) {
        return path.toString().toLowerCase().endsWith(".pdf");
    }

    private void exportImage(Path pdfFilePath,
                             Path resultPath,
                             BiFunction<PDFRenderer, Integer, Optional<BufferedImage>> transformer,
                             TriPredicate<BufferedImage, Path, Integer> fileExporterFilter,
                             FileExporter<BufferedImage> exporter) {
        String pdfFilename = pdfFilePath.normalize().toString();
        logger.info("Exporting " + pdfFilename);
        try (PDDocument document = PDDocument.load(new File(pdfFilename))) {
            PDFRenderer pdfRenderer = new PDFRenderer(document);
            for (int page = 0; page < document.getNumberOfPages(); ++page) {
                final int actualPage = page;
                transformer
                        .apply(pdfRenderer, page)
                        .filter(bufferedImage -> fileExporterFilter.test(bufferedImage, pdfFilePath, actualPage))
                        .ifPresent(bufferedImage -> exporter.apply(bufferedImage, pdfFilePath, resultPath, actualPage));
            }
        } catch (IOException e) {
            final String message = String.format("Error exporting image %s", pdfFilename);
            logger.error(message, e);
        }
    }

    public void runWith(BiFunction<PDFRenderer, Integer, Optional<BufferedImage>> transformer,
                        FileExporter<BufferedImage> fileExporter,
                        TriPredicate<BufferedImage, Path, Integer> fileExporterFilter) throws IOException {
        Files.list(Paths.get(sourceDirectory))
                .filter(Files::isRegularFile)
                .filter(this::isPdfFile)
                .forEach(p -> exportImage(p, Paths.get(destinationDirectory), transformer, fileExporterFilter, fileExporter));
    }
}
