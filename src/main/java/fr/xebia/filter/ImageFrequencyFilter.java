package fr.xebia.filter;

import fr.xebia.export.BufferedImageExporter;
import org.jtransforms.fft.DoubleFFT_2D;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ImageFrequencyFilter implements TriPredicate<BufferedImage, Path, Integer> {

    @Override
    public boolean test(BufferedImage image, Path pdfFilePath, Integer page) {
        double[][] brightness = calculateFFT(image, buildBrightnessFrom(image));
        exportFrequencyToDisk(image, pdfFilePath, brightness, page);
        return true;
    }

    private double[][] calculateFFT(BufferedImage image, double[][] brightness) {
        new DoubleFFT_2D(image.getHeight(), image.getWidth()).realForwardFull(brightness);
        return brightness;
    }

    private double[][] buildBrightnessFrom(BufferedImage image) {
        //width * 2, because DoubleFFT_2D needs 2x more space - for Real and Imaginary parts of complex numbers
        double[][] brightness = new double[image.getHeight()][image.getWidth() * 2];

        //convert colored image to grayscale (brightness of each pixel)
        for (int y = 0; y < image.getHeight(); y++) {
            //raster.getDataElements(0, y, image.getWidth(), 1, dataElements);
            for (int x = 0; x < image.getWidth(); x++) {
                final int rgb = image.getRGB(x, y);
                //notice x and y swapped - it's JTransforms format of arrays
                brightness[y][x] = brightnessRGB(rgb);
            }
        }
        return brightness;
    }

    private void exportFrequencyToDisk(BufferedImage image, Path _pdfFilePath, double[][] brightness, int page) {
        final Path freqPath = Paths.get(_pdfFilePath.normalize().toString() + "-freq.png");
        final BufferedImage frequency = exportFrequency(brightness, image);
        final Path resultPath = Paths.get("results");
        new BufferedImageExporter().apply(frequency, freqPath, resultPath, page);
    }

    //do FT (not FFT, because FFT is only* for images with width and height being 2**N)
    //DoubleFFT_2D writes data to the same array - to brightness
    private BufferedImage exportFrequency(double[][] brightness, BufferedImage image) {
        BufferedImage fd = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                //we calculate complex number vector length (sqrt(Re**2 + Im**2)). But these lengths are to big to
                //fit in 0 - 255 scale of colors. So I divide it on 223. Instead of "223", you may want to choose
                //another factor, wich would make you frequency domain look best
                int power = (int) (Math.sqrt(Math.pow(brightness[y][2 * x], 2) + Math.pow(brightness[y][2 * x + 1], 2)) / 223);
                power = power > 255 ? 255 : power;
                //draw a grayscale color on image "fd"
                fd.setRGB(x, y, new Color(power, power, power).getRGB());
            }
        }
        return fd;
    }

    private int brightnessRGB(int rgb) {
        int r = (rgb >> 16) & 0xff;
        int g = (rgb >> 8) & 0xff;
        int b = rgb & 0xff;
        return (r + g + b) / 3;
    }

}
