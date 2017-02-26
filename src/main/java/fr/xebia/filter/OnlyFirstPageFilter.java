package fr.xebia.filter;

import java.awt.image.BufferedImage;
import java.util.function.BiPredicate;

public class OnlyFirstPageFilter implements BiPredicate<BufferedImage, Integer> {

    @Override
    public boolean test(BufferedImage img, Integer page) {
        return page == 0;
    }

}
