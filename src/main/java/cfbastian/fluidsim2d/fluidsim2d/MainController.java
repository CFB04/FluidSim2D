package cfbastian.fluidsim2d.fluidsim2d;

import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.image.WritablePixelFormat;

import java.nio.IntBuffer;
import java.util.Arrays;

public class MainController {
    @FXML
    ImageView imageView;

    WritableImage image;
    PixelWriter pixelWriter;
    WritablePixelFormat<IntBuffer> pixelFormat;

    Renderer renderer;
    RenderLoop renderLoop;

    static int[] pixels = new int[Application.width * Application.height];

    long startTime;

    @FXML
    public void initialize() {
        Arrays.fill(pixels, 0xFF000000);

        renderer = new Renderer();
        renderLoop = new RenderLoop();

        imageView.setFitWidth(Application.width);
        imageView.setFitHeight(Application.height);
        image = new WritableImage(Application.width, Application.height);
        pixelWriter = image.getPixelWriter();
        pixelFormat = WritablePixelFormat.getIntArgbInstance();
        pixelWriter.setPixels(0, 0, Application.width, Application.height, pixelFormat, pixels, 0, Application.width);
        imageView.setImage(image);

        renderer.init();

        startTime = System.nanoTime();
        renderLoop.start();
    }

    private class RenderLoop extends AnimationTimer {

        double timer;
        int frames;

        @Override
        public void handle(long now) {

            double elapsedTime = (now - startTime)/1000000000D;

            pixels = renderer.render();

            pixelWriter.setPixels(0, 0, Application.width, Application.height, pixelFormat, pixels, 0, Application.width);
            imageView.setImage(image);

            frames++;

            while(elapsedTime - timer > 1)
            {
                System.out.println(frames);
                timer++;
                frames = 0;
            }
        }
    }
}