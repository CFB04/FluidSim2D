package cfbastian.fluidsim2d.simulation.eulerian;

import cfbastian.fluidsim2d.Renderer;
import cfbastian.fluidsim2d.simulation.Gridpoint;
import cfbastian.fluidsim2d.simulation.Renderable;
import cfbastian.fluidsim2d.simulation.util.Bounds;

public class EulerianGrid implements Renderable {
    public Gridpoint[] mGridpoints, uGridpoints, vGridpoints;

    private final Bounds bounds;
    private final Bounds windowBounds;
    private final float res;
    private final int rows, cols;

    public EulerianGrid(Bounds bounds, Bounds windowBounds, int res)
    {
        this.bounds = bounds;
        this.windowBounds = windowBounds;

        this.res = res;

        this.cols = (int) (bounds.getWidth() * res) + 2;
        this.rows = (int) (bounds.getHeight() * res) + 2;

        this.mGridpoints = new Gridpoint[rows * cols];
        this.uGridpoints = new Gridpoint[rows * cols];
        this.vGridpoints = new Gridpoint[rows * cols];

        for (int i = 0; i < mGridpoints.length; i++) {
            int x = i % cols, y = i / cols;
            mGridpoints[i] = new Gridpoint(
                    bounds.getXMin() + (x - 0.5f) / this.res,
                    bounds.getYMin() + (y - 0.5f) / this.res,
                    0xFFAAAAAA);
        }

        for (int i = 0; i < uGridpoints.length; i++) {
            int x = i % cols, y = i / cols;
            uGridpoints[i] = new Gridpoint(
                    bounds.getXMin() + (x) / this.res,
                    bounds.getYMin() + (y - 0.5f) / this.res,
                    0xFFFF4444);
        }

        for (int i = 0; i < vGridpoints.length; i++) {
            int x = i % cols, y = i / cols;
            vGridpoints[i] = new Gridpoint(
                    bounds.getXMin() + (x - 0.5f) / this.res,
                    bounds.getYMin() + (y) / this.res,
                    0xFF4444FF);
        }
    }

    public int selectGridpoint(float x, float y)
    {
        int xi = (int) ((x - bounds.getXMin()) * res);
        int yi = (int) ((y - bounds.getYMin()) * res);
        return xi + yi * cols;
    }

    public int[] selectGridpoints(int i)
    {
        return new int[]{i, i + cols, i + 1, i + cols + 1};
    }

    public int selectMGridpoint(float x, float y)
    {
        return selectGridpoint(x + 0.5f / res, y + 0.5f / res);
    }

    public int selectUGridpoint(float x, float y)
    {
        return selectGridpoint(x, y + 0.5f / res);
    }

    public int selectVGridpoint(float x, float y)
    {
        return selectGridpoint(x + 0.5f / res, y);
    }
    
    @Override
    public void render(Renderer renderer) {
        renderStaggeredGrid(renderer);
    }

    private void renderStaggeredGrid(Renderer renderer)
    {
        for (int i = 0; i < mGridpoints.length; i++) {
            float wScale = windowBounds.getWidth()/bounds.getWidth();
            float hScale = windowBounds.getHeight()/bounds.getHeight();

            renderer.drawDottedRectangle(Math.round((mGridpoints[i].x - 0.5f / res) * wScale + windowBounds.getXMin()), Math.round((mGridpoints[i].y - 0.5f / res) * hScale + windowBounds.getYMin()), Math.round(wScale/ res), Math.round(hScale/ res), 0xFF888888);
            renderer.drawEmptyCircle(Math.round(mGridpoints[i].x * wScale + windowBounds.getXMin()), Math.round(mGridpoints[i].y * hScale + windowBounds.getYMin()), 3, mGridpoints[i].getColor());
            renderer.drawEmptyCircle(Math.round(uGridpoints[i].x * wScale + windowBounds.getXMin()), Math.round(uGridpoints[i].y * hScale + windowBounds.getYMin()), 3, uGridpoints[i].getColor());
            renderer.drawEmptyCircle(Math.round(vGridpoints[i].x * wScale + windowBounds.getXMin()), Math.round(vGridpoints[i].y * hScale + windowBounds.getYMin()), 3, vGridpoints[i].getColor());
        }
    }
}
