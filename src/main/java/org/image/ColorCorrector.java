package org.image;

import java.awt.*;
import java.awt.image.BufferedImage;

public class ColorCorrector extends AbstractCorrector implements Runnable {

    static final double[][] GAUSSIAN = new double[][]{
            {1 / 16d, 2 / 16d, 1 / 16d},
            {2 / 16d, 4 / 16d, 2 / 16d},
            {1 / 16d, 2 / 16d, 1 / 16d}
    };
    final int x1, x2;
    final BufferedImage img;

    public ColorCorrector(int x1, int x2, BufferedImage img) {
        this.x1 = x1;
        this.x2 = x2;
        this.img = img;
    }

    public void run() {
        for (int x = x1; x < x2; x++) {
            for (int y = 0; y < img.getHeight(); y++) {

                // bewaring of extremes
                int[][] currentMrx = new int[][]{
                        {
                            img.getRGB(x - 1 >= x1 ? x - 1 : x, y - 1 >= 0 ? y - 1 : y),
                            img.getRGB(x, y - 1 >= 0 ? y - 1 : y),
                            img.getRGB(x + 1 < x2 ? x + 1 : x, y - 1 >= 0 ? y - 1 : y)
                        },
                        {
                            img.getRGB(x - 1 >= x1 ? x - 1 : x, y),
                            img.getRGB(x, y),
                            img.getRGB(x + 1 < x2 ? x + 1 : x, y)
                        },
                        {
                            img.getRGB(x - 1 >= x1 ? x - 1 : x, y + 1 < img.getHeight() ? y + 1 : y),
                            img.getRGB(x, y + 1 < img.getHeight() ? y + 1 : y),
                            img.getRGB(x + 1 < x2 ? x + 1 : x, y + 1 < img.getHeight() ? y + 1 : y)
                        },

                };

                int pixel = (int) filter(currentMrx);
                Color color = new Color(pixel, false);
                int r = color.getRed();
                int g = color.getGreen();
                int b = color.getBlue();
                int rgb = new Color(r, g, b).getRGB();
                image.get().setRGB(x, y, rgb);
            }
        }
    }

    private double filter(int[][] mrx) {
        double res = 0d;

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                int pixel = mrx[i][j];
                Color color = new Color(pixel, false);
                int r = color.getRed();
                int g = color.getGreen();
                int b = color.getBlue();
                double I = (r + g + b) / 3d;
                res += I * GAUSSIAN[i][j];
            }
        }

        return res;
    }
}