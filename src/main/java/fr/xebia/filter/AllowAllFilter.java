package fr.xebia.filter;

import java.awt.image.BufferedImage;
import java.util.function.BiPredicate;

public class AllowAllFilter implements BiPredicate<BufferedImage, Integer> {

    @Override
    public boolean test(BufferedImage img, Integer page) {
        return true;
    }

}
