package org.image;

import java.awt.image.BufferedImage;
import java.util.concurrent.atomic.AtomicReference;

public abstract class AbstractCorrector {

    protected static final AtomicReference<BufferedImage> image = new AtomicReference<>();

    public static BufferedImage getImage() {
        return image.get();
    }

    public static void setImage(BufferedImage img) {
        image.set(img);
    }
}
