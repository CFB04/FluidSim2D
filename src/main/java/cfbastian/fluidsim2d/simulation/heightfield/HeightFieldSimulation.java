package cfbastian.fluidsim2d.simulation.heightfield;

import cfbastian.fluidsim2d.Renderer;
import cfbastian.fluidsim2d.simulation.Simulation;
import cfbastian.fluidsim2d.simulation.util.Bounds;

import java.util.Arrays;

public class HeightFieldSimulation extends Simulation {

    Bounds windowBounds;

    private final int cols;
    private float[] a, v, heights;
    private final float colWidth;
    private final float k;
    private final float startHeight;

    public HeightFieldSimulation(Bounds bounds, Bounds windowBounds, float res, float waveSpeed, float startHeight) {
        super(bounds);
        this.windowBounds = windowBounds;
        this.cols = (int) (res * bounds.getWidth());
        this.a = new float[cols];
        this.v = new float[cols];
        this.heights = new float[cols];
        this.colWidth = bounds.getWidth() / cols;
        // waveSpeed * dt < colWidth (CFL condition)
        this.k = waveSpeed * waveSpeed / (colWidth * colWidth);
        this.startHeight = startHeight;

        init();
    }

    @Override
    public void init()
    {
        for (int i = 0; i < cols; i++){
            heights[i] = startHeight;
            a[i] = 0f;
            v[i] = 0f;
        }
        for (int i = 0; i < cols; i++) {
//            float pow = 2f * (2f * i / cols - 1f);
//            pow *= pow;
//            heights[i] += 1.5f * ((float) Math.pow(Math.E, -pow) - 0.5f);
//            float pow = 10f * (2f * i / cols - 1f);
//            pow *= pow;
//            heights[i] += 0.3f * (float) Math.pow(Math.E, -pow);
//            heights[i] += (i / (float) cols - 0.5f);
            heights[i] += 0.2f * Math.max(1f - Math.abs(32f * (i - cols/2f) / cols), 0f);
            heights[i] = Math.max(heights[i], bounds.getYMin());
            heights[i] = Math.min(heights[i], bounds.getYMax());
        }
//        heights[(int) (cols * 1f / 3f)-1] -= 0.05f;
//        heights[(int) (cols * 1f / 3f)] -= 0.1f;
//        heights[(int) (cols * 1f / 3f)+1] -= 0.05f;
//        heights[(int) (cols * 2f / 3f)-1] += 0.05f;
//        heights[(int) (cols * 2f / 3f)] += 0.1f;
//        heights[(int) (cols * 2f / 3f)+1] += 0.05f;
    }

    @Override
    public void update(float dt) {
        for (int i = 0; i < cols; i++) {
            float before = 0f, after = 0f, c = 0f;
            if(i - 1 >= 0) {
                before = heights[i - 1];
                c++;
            }
            if(i + 1 < cols) {
                after = heights[i + 1];
                c++;
            }
            a[i] = k * (before + after - c * heights[i]);
        }
        for (int i = 0; i < cols; i++) {
            v[i] *= 0.999f;
            v[i] += dt * a[i];
            heights[i] += v[i] * dt;
            heights[i] = Math.max(heights[i], bounds.getYMin());
            heights[i] = Math.min(heights[i], bounds.getYMax());
        }
    }

    @Override
    public void render(Renderer renderer) {
        float dispColWidth = bounds.getWidth() / (cols - 1f);
        float xStart = windowBounds.getXMin(), yStart = windowBounds.getYMin();
        float xScalar = windowBounds.getWidth() / bounds.getWidth(), yScalar = windowBounds.getWidth() / bounds.getWidth();

        for (int i = 0; i < cols - 1; i++) {
            renderer.drawTrapezoid((int) (xStart + i * dispColWidth * xScalar), (int) yStart, (int) (dispColWidth * xScalar), (int) (heights[i] * yScalar), (int) (heights[i + 1] * yScalar), 0xFF6666FF);
        }

        renderBox(renderer);
    }

    private void renderBox(Renderer renderer)
    {
        renderer.drawEmptyRectangle((int) windowBounds.getXMin() - 1, (int) windowBounds.getYMin() - 1, (int) windowBounds.getWidth() + 1, (int) windowBounds.getHeight() + 1, 0xFFFFFFFF);
    }
}
