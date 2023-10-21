package cfbastian.fluidsim2d.simulation.heightfield;

import cfbastian.fluidsim2d.Renderer;
import cfbastian.fluidsim2d.simulation.Simulation;
import cfbastian.fluidsim2d.simulation.util.Bounds;

import java.util.Arrays;

public class HeightFieldSimulation extends Simulation {

    Bounds windowBounds;

    private final float res;
    private final int cols;
    private float[] a, v, heights;
    private final float colWidth;
    private final float k;
    private final float startHeight;

    public HeightFieldSimulation(Bounds bounds, Bounds windowBounds, float res, float waveSpeed, float startHeight) {
        super(bounds);
        this.windowBounds = windowBounds;
        this.res = res;
        this.cols = (int) (res * bounds.getWidth());
        this.a = new float[cols];
        this.v = new float[cols];
        this.heights = new float[cols];
        this.colWidth = 1f / res;
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
            float pow = 2f * (2f * i / cols - 1f);
            pow *= pow;
            heights[i] += 1f * ((float) Math.pow(Math.E, -pow) - 0.5f);
            heights[i] = Math.max(heights[i], bounds.getYMin());
            heights[i] = Math.min(heights[i], bounds.getYMax());
        }
    }

    @Override
    public void update(float dt) {
        for (int i = 0; i < cols; i++) {
            int before = i - 1, after = i + 1;
            before = before < 0? before + cols : before;
            after %= cols;
            a[i] = k * ((heights[before] + heights[after]) - 2f * heights[i]);
        }
        for (int i = 0; i < cols; i++) {
            v[i] += dt * a[i];
            heights[i] += v[i] * dt;
            heights[i] = Math.max(heights[i], bounds.getYMin());
            heights[i] = Math.min(heights[i], bounds.getYMax());
        }
    }

    @Override
    public void render(Renderer renderer) {
        float xStart = windowBounds.getXMin(), yStart = windowBounds.getYMin();
        float xScalar = windowBounds.getWidth() / bounds.getWidth(), yScalar = windowBounds.getWidth() / bounds.getWidth();
        for (int i = 0; i < cols; i++) {
            renderer.drawRectangle((int) (xStart + i * colWidth * xScalar), (int) (windowBounds.getYMax() - heights[i] * yScalar), (int) (colWidth * xScalar), (int) (heights[i] * yScalar), 0xFF6666FF);
        }

        renderBox(renderer);
    }

    private void renderBox(Renderer renderer)
    {
        renderer.drawEmptyRectangle((int) windowBounds.getXMin() - 1, (int) windowBounds.getYMin() - 1, (int) windowBounds.getWidth() + 1, (int) windowBounds.getHeight() + 1, 0xFFFFFFFF);
    }
}
