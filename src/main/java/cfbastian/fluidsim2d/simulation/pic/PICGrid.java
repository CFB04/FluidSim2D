package cfbastian.fluidsim2d.simulation.pic;

import cfbastian.fluidsim2d.Renderer;
import cfbastian.fluidsim2d.simulation.Gridpoint;
import cfbastian.fluidsim2d.simulation.Renderable;
import cfbastian.fluidsim2d.simulation.util.Bounds;

public class PICGrid implements Renderable {
    public Gridpoint[] mGridpoints, uGridpoints, vGridpoints;

    private final Bounds bounds;
    private final Bounds windowBounds;

    private final int rows, cols;

    private final float invGridCellWidth, invGridCellHeight;

    public PICGrid(Bounds bounds, Bounds windowBounds, int rows, int cols) {
        this.bounds = bounds;
        this.windowBounds = windowBounds;

        this.invGridCellWidth = cols / bounds.getWidth();
        this.invGridCellHeight = rows / bounds.getHeight();

        rows += 2;
        cols += 2;

        this.rows = rows;
        this.cols = cols;

        this.mGridpoints = new Gridpoint[rows * cols];
        this.uGridpoints = new Gridpoint[rows * cols];
        this.vGridpoints = new Gridpoint[rows * cols];

        for (int i = 0; i < mGridpoints.length; i++) {
            int x = i % cols, y = i / cols;
            mGridpoints[i] = new Gridpoint(
                    bounds.getxMin() + (x - 0.5f) / invGridCellWidth,
                    bounds.getyMin() + (y - 0.5f) / invGridCellHeight,
                    0xFFAAAAAA);
        }

        for (int i = 0; i < uGridpoints.length; i++) {
            int x = i % cols, y = i / cols;
            uGridpoints[i] = new Gridpoint(
                    bounds.getxMin() + (x) / invGridCellWidth,
                    bounds.getyMin() + (y - 0.5f) / invGridCellHeight,
                    0xFFFF4444);
        }

        for (int i = 0; i < uGridpoints.length; i++) {
            int x = i % cols, y = i / cols;
            vGridpoints[i] = new Gridpoint(
                    bounds.getxMin() + (x - 0.5f) / invGridCellWidth,
                    bounds.getyMin() + (y) / invGridCellHeight,
                    0xFF4444FF);
        }
    }

    public void init()
    {
        // Set initial velocities
        for (int i = 0; i < mGridpoints.length; i++) {
            mGridpoints[i].reset();
            uGridpoints[i].reset();
            vGridpoints[i].reset();
//            gridpoints[i].setVY(1f);
            uGridpoints[i].setV(-2f * (mGridpoints[i].getY() - bounds.getCenterY()));
            vGridpoints[i].setV(2f * (mGridpoints[i].getX() - bounds.getCenterX()));
//            uGridpoints[i].setV(0f);
//            vGridpoints[i].setV(10f);
//            mGridpoints[i].setVY(0.2f);
//            float x = gridpoints[i].getX() - bounds.getCenterX(), y = gridpoints[i].getY() - bounds.getCenterY();
//            float mag = (float) Math.sqrt(x*x + y*y);
//            mag = mag == 0f? 1f : mag;
//            mag = 1f;
//            gridpoints[i].setVX(x/mag);
//            gridpoints[i].setVY(y/mag);
        }
    }

    public void reset()
    {
        for (int i = 0; i < mGridpoints.length; i++)
        {
            mGridpoints[i].reset();
            uGridpoints[i].reset();
            vGridpoints[i].reset();
        }
    }

    public int selectGridpoint(float x, float y)
    {
        int xi = (int) ((x - bounds.getxMin()) * invGridCellWidth);
        int yi = (int) ((y - bounds.getyMin()) * invGridCellHeight);
        return xi + yi * cols;
    }

    public int selectMGridpoint(PICParticle p)
    {
        return selectGridpoint(p.getX() + 0.5f / invGridCellWidth, p.getY() + 0.5f / invGridCellHeight);
    }

    public int selectUGridpoint(PICParticle p)
    {
        return selectGridpoint(p.getX() + 1f * 0.5f / invGridCellWidth, p.getY() + 0.5f / invGridCellHeight);
    }

    public int selectVGridpoint(PICParticle p)
    {
        return selectGridpoint(p.getX() + 0.5f / invGridCellWidth, p.getY() + 1f * 0.5f / invGridCellHeight);
    }

    public int[] selectGridpoints(int i)
    {
        int[] g = new int[4];
        g[0] = i;
        g[1] = g[0] + cols;
        g[2] = g[0] + 1;
        g[3] = g[0] + cols + 1;
        return g;
    }

    public float getInvGridCellWidth() {
        return invGridCellWidth;
    }

    public float getInvGridCellHeight() {
        return invGridCellHeight;
    }

    @Override
    public void render(Renderer renderer) {
        for (int i = 0; i < mGridpoints.length; i++) {
            float wScale = windowBounds.getWidth()/bounds.getWidth();
            float hScale = windowBounds.getHeight()/bounds.getHeight();

            renderer.drawDottedRectangle((int) ((mGridpoints[i].getX() - 0.5f / invGridCellWidth) * wScale + windowBounds.getxMin()), (int) (windowBounds.getHeight() - (mGridpoints[i].getY() + 0.5f / invGridCellHeight) * hScale + windowBounds.getyMin()), (int) (wScale/ invGridCellWidth), (int) (hScale/ invGridCellHeight), 0xFF888888);
            renderer.drawEmptyCircle((int) (mGridpoints[i].getX() * wScale + windowBounds.getxMin()), (int) (windowBounds.getHeight() - mGridpoints[i].getY() * hScale + windowBounds.getyMin()), 2, mGridpoints[i].getColor());
            renderer.drawEmptyCircle((int) (uGridpoints[i].getX() * wScale + windowBounds.getxMin()), (int) (windowBounds.getHeight() - uGridpoints[i].getY() * hScale + windowBounds.getyMin()), 2, uGridpoints[i].getColor());
            renderer.drawEmptyCircle((int) (vGridpoints[i].getX() * wScale + windowBounds.getxMin()), (int) (windowBounds.getHeight() - vGridpoints[i].getY() * hScale + windowBounds.getyMin()), 2, vGridpoints[i].getColor());
        }
    }
}
