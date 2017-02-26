package fr.xebia.export;

import org.apache.pdfbox.tools.imageio.ImageIOUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.StringJoiner;

import static javax.print.attribute.ResolutionSyntax.DPI;

public class BufferedImageExporter implements FileExporter<BufferedImage> {

    private Logger logger = LoggerFactory.getLogger(getClass());

    public void apply(BufferedImage bim, Path pdfFilePath, Path resultPath, int page) {
        final String filename = buildPngFileName(pdfFilePath, resultPath, page);
        try {
            ImageIOUtil.writeImage(bim, filename, DPI);
        } catch (IOException e) {
            logger.error("Error exporting image", e);
        }
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
}
