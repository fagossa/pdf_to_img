package fr.xebia;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.tools.imageio.ImageIOUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.StringJoiner;

public class ImageExtractor {

    private static final int DPI = 300;

    private String sourceDirectory;
    private String destinationDirectory;

    private Logger logger = LoggerFactory.getLogger(getClass());

    private ImageExtractor(String sourceDirectory, String destinationDirectory) {
        this.sourceDirectory = sourceDirectory;
        this.destinationDirectory = destinationDirectory;
    }

    public static void main(String... args) throws IOException {
        if (args.length == 2) {
            final String inputDir = args[0];
            final String outputDir = args[1];
            new ImageExtractor(inputDir, outputDir).run();
        } else {
            System.out.println("No import or export directory specified. Usage: java -jar pdf_to_img.jar {input_dir} {output_dir}");
            System.exit(1);
        }
    }

    private boolean isPdfFile(Path path) {
        return path.toString().toLowerCase().endsWith(".pdf");
    }

    private void exportImage(Path pdfFilePath, Path resultPath, boolean onlyFirstPage) {
        String pdfFilename = pdfFilePath.normalize().toString();
        logger.info("Exporting " + pdfFilename);
        try (PDDocument document = PDDocument.load(new File(pdfFilename))) {
            PDFRenderer pdfRenderer = new PDFRenderer(document);
            for (int page = 0; page < document.getNumberOfPages(); ++page) {
                exportContentsOfPage(pdfFilePath, resultPath, pdfRenderer, page);
                if (onlyFirstPage) {
                    break;
                }
            }
        } catch (IOException e) {
            final String message = String.format("Error exporting image %s", pdfFilename);
            logger.error(message, e);
        }
    }

    private void exportContentsOfPage(Path pdfFilePath, Path resultPath, PDFRenderer pdfRenderer, int page) throws IOException {
        BufferedImage bim = pdfRenderer.renderImageWithDPI(page, DPI, ImageType.RGB);
        ImageIOUtil.writeImage(bim, buildPngFileName(pdfFilePath, resultPath, page), DPI);
    }

    private String buildPngFileName(Path pdfFilePath, Path resultPath, int page) {
        final String onlyFileName = pdfFilePath.getFileName().normalize().toString()
                + "-"
                + String.valueOf(page + 1)
                + ".png";
        return new StringJoiner(File.separator)
                .add(resultPath.toAbsolutePath().normalize().toString())
                .add(onlyFileName)
                .toString();
    }

    private void run() throws IOException {
        Files.list(Paths.get(sourceDirectory))
                .filter(Files::isRegularFile)
                .filter(this::isPdfFile)
                .forEach(p -> exportImage(p, Paths.get(destinationDirectory), true));
    }
}
