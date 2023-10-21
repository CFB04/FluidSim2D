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

import java.nio.IntBuffer;
import java.util.Arrays;

public class MainController {
    @FXML
    ImageView imageView;

    @FXML
    Button playButton;
    boolean play = false;

    @FXML
    Button stepButton;
    boolean step = false;

    @FXML
    Button resetButton;

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

        createSimulation();

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
            if(step) {
                simulation.update(dt);
                step = false;
            }

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

    private void createSimulation()
    {
        //        simulation = new IterativeSimulation(new Bounds(0f, 16f, 0f, 9f), 100000);
//        simulation = new SPHSimulation(
//                new Bounds(0f, 16f, 0f, 9f), 16*9*50*50,
//                new Bounds(4f, 12f, 3f, 7.5f), 16*50);
        int gridRes = 4, pRes = 16;
        simulation = new PICSimulation(
                new Bounds(0f, 16f, 0f, 9f),
                new Bounds(Application.width * 8f / 64f, Application.width * 56f / 64f, Application.height * 8f / 64f, Application.height * 56f / 64f),
                new Bounds(1f, 5f, 0.5f, 6.5f),
                4*6*pRes*pRes, 4*pRes, 9*gridRes,16*gridRes);
    }

    @FXML
    public void play()
    {
        play = !play;
        playButton.setText(play? "❚❚" : "▶︎");
    }

    @FXML
    public void step()
    {
        if(!play) step = true;
    }

    @FXML
    public void reset()
    {
        createSimulation();
    }
}