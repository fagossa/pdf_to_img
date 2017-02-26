package fr.xebia;

import fr.xebia.export.BufferedImageExporter;
import fr.xebia.filter.ImageFrequencyFilter;
import fr.xebia.img.ImageExtractor;
import fr.xebia.transform.PageToBufferedImage;

import java.io.IOException;

public class Executor {

    public static void main(String... args) throws IOException {
        if (args.length == 2) {
            final String inputDir = args[0];
            final String outputDir = args[1];
            new ImageExtractor(inputDir, outputDir)
                    .runWith(
                            new PageToBufferedImage(),
                            new BufferedImageExporter(),
                            new ImageFrequencyFilter());
        } else {
            System.out.println("No import or export directory specified. Usage: java -jar pdf_to_img.jar {input_dir} {output_dir}");
            System.exit(1);
        }
    }
}
