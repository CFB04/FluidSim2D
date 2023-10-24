package cfbastian.fluidsim2d;

import cfbastian.fluidsim2d.simulation.Simulation;
import cfbastian.fluidsim2d.simulation.eulerian.EulerianSimulation;
import cfbastian.fluidsim2d.simulation.heightfield.HeightFieldSimulation;
import cfbastian.fluidsim2d.simulation.iterativemethods.IterativeSimulation;
import cfbastian.fluidsim2d.simulation.pic.PICSimulation;
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
            if(play) {
                int steps = 1;
                for (int i = 0; i < steps; i++) {
                    simulation.update(dt / (float) steps);
                }
            }
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
                System.out.print(frames + "\t");
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
        simulation = new PICSimulation(
                new Bounds(0f, 16f, 0f, 9f),
                new Bounds(2, Application.width - 2, 2, Application.height - 2),
                new Bounds(0.1f, 4.1f, 0.1f, 8.1f),
                8);
//        simulation = new HeightFieldSimulation(
//                new Bounds(0f, 16f/4f, 0f, 9f/4f),
//                new Bounds(160, Application.width - 160, 90, Application.height - 90),
//                64, 1f, 1f);
//        simulation = new EulerianSimulation(
//                new Bounds(0f, 16f, 0f, 9f),
//                new Bounds(160, Application.width - 160, 90, Application.height - 90),
//                1);
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