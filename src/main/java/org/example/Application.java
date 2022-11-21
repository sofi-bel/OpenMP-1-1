package org.example;

import org.image.AbstractCorrector;
import org.image.ColorCorrector;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static java.lang.System.currentTimeMillis;

public class Application {

    public static void main(String[] args) throws IOException {
        int availableProcessors = Runtime.getRuntime().availableProcessors();
        int iter = 10;

        for (int i = 0; i < iter; i++) {
            System.out.println(i + ":");

            for (int n = 0, argsLength = args.length; n < argsLength; n++) {
                File file = new File(args[n]);
                BufferedImage img = ImageIO.read(file);
                int xWidth = img.getWidth();
                int yHeight = img.getHeight();
                int step = xWidth / availableProcessors;
                ExecutorService fixedThreadPool = Executors.newFixedThreadPool(availableProcessors);
                List<Future<?>> futureList = new ArrayList<>();
                AbstractCorrector.setImage(new BufferedImage(xWidth, yHeight, 1));
                long start = currentTimeMillis();

                for (int i1 = 0; i1 < availableProcessors; i1++) {
                    int remains = (i1 == availableProcessors - 1) ? xWidth % availableProcessors : 0;
                    futureList.add(fixedThreadPool.submit
                            (
                                    new ColorCorrector(i1 * step, (i1 + 1) * step + remains, img)
                            )
                    );
                }

                for (Future<?> future : futureList) {
                    try {
                        future.get();
                    } catch (InterruptedException | ExecutionException e) {
                        System.err.println(e.getMessage());
                    }
                }

                ImageIO.write(AbstractCorrector.getImage(), "jpg", new File("/Users/sofi/IntelliJProjects/OpenMP-1-1/src/main/resources/" + n + "8" + ".jpg"));
                long time = currentTimeMillis() - start;
                System.out.print("time 8 (" + n + "): " + time + ", ");

                try {
                    start = currentTimeMillis();
                    fixedThreadPool.submit(new ColorCorrector(0, xWidth, img)).get();
                    ImageIO.write(AbstractCorrector.getImage(), "jpg", new File("/Users/sofi/IntelliJProjects/OpenMP-1-1/src/main/resources/" + n + "1" + ".jpg"));
                    time = currentTimeMillis() - start;
                    System.out.println("time 1 (" + n + "): " + time + ".");
                } catch (InterruptedException | ExecutionException e) {
                    System.err.println(e.getMessage());
                } finally {
                    fixedThreadPool.shutdown();
                }
            }
        }
    }
}
