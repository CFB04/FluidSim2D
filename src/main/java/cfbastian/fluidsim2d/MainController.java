package cfbastian.fluidsim2d;

import cfbastian.fluidsim2d.simulation.Simulation;
import cfbastian.fluidsim2d.simulation.iterativemethods.IterativeSimulation;
import cfbastian.fluidsim2d.simulation.pic.PICSimulation;
import cfbastian.fluidsim2d.simulation.sph.SPHParticle;
import cfbastian.fluidsim2d.simulation.sph.SPHSimulation;
import cfbastian.fluidsim2d.simulation.util.Bounds;
import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.*;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;

public class MainController {
    @FXML
    ImageView imageView;

    @FXML
    Button playButton;
    boolean play = false;

    WritableImage image;
    PixelWriter pixelWriter;
    WritablePixelFormat<IntBuffer> pixelFormat;

    Renderer renderer;
    RenderLoop renderLoop;

    Simulation simulation;

    static int[] pixels = new int[Application.width * Application.height];

    long startTime;

    @FXML
    public void initialize() {
        Arrays.fill(pixels, 0xFF000000);

        renderer = new Renderer();
        renderLoop = new RenderLoop();

//        simulation = new IterativeSimulation(new Bounds(0f, 16f, 0f, 9f), 100000);
//        simulation = new SPHSimulation(
//                new Bounds(0f, 16f, 0f, 9f), 16*9*50*50,
//                new Bounds(4f, 12f, 3f, 7.5f), 16*50);
        int gridRes = 16, pRes = 32;
        simulation = new PICSimulation(
                new Bounds(0f, 16f, 0f, 9f),
                new Bounds(Application.width * 2f / 64f, Application.width * 62f / 64f, Application.height * 2f / 64f, Application.height * 62f / 64f),
                new Bounds(4f, 12f, 2.25f, 6.75f),
                36*pRes*pRes, 8*pRes, 9*gridRes,16*gridRes);
        simulation.init();

        imageView.setFitWidth(Application.width / 2D);
        imageView.setFitHeight(Application.height / 2D);
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

        double lastTime = startTime / 1000000000D;
        @Override
        public void handle(long now) {
            double elapsedTime = (now - startTime)/1000000000D;

            float dt = (float) (elapsedTime - lastTime);
            if(play) simulation.update(dt);

            pixels = renderer.render(simulation);

            pixelWriter.setPixels(0, 0, Application.width, Application.height, pixelFormat, pixels, 0, Application.width);
            imageView.setImage(image);

            lastTime = elapsedTime;

            frames++;

            while(elapsedTime - timer > 1)
            {
                System.out.println(frames);
                timer++;
                frames = 0;
            }
        }
    }

    @FXML
    public void play()
    {
        play = !play;
        playButton.setText(play? "❚❚" : "▶︎");
    }
}