package cfbastian.fluidsim2d.simulation.pic;

import cfbastian.fluidsim2d.Renderer;
import cfbastian.fluidsim2d.simulation.Gridpoint;
import cfbastian.fluidsim2d.simulation.Particle;
import cfbastian.fluidsim2d.simulation.Renderable;
import cfbastian.fluidsim2d.simulation.util.Bounds;

public class PICGrid implements Renderable {
    public Gridpoint[] mGridpoints, uGridpoints, vGridpoints;
    public CellType[] cellTypes;

    private final Bounds bounds;
    private final Bounds windowBounds;

    public final int rows, cols;
    public final float res;

    public PICGrid(Bounds bounds, Bounds windowBounds, int rows, int cols, float res)
    {
        this.bounds = bounds;
        this.windowBounds = windowBounds;

        this.res = res;

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

        this.cellTypes = new CellType[rows * cols];
        for (int i = 0; i < cols; i++) {
            cellTypes[i] = CellType.WALL;
            cellTypes[i + (rows - 1) * cols] = CellType.WALL;
        }
        for (int i = 1; i < rows; i++) {
            cellTypes[i * cols] = CellType.WALL;
            cellTypes[i * cols - 1] = CellType.WALL;
        }
    }

    public void init()
    {
        // Set initial velocities
        for (int i = 0; i < mGridpoints.length; i++) {
            mGridpoints[i].reset();
            uGridpoints[i].reset();
            vGridpoints[i].reset();

//            uGridpoints[i].setV(3f * (mGridpoints[i].y - bounds.getCenterY()));
//            vGridpoints[i].setV(-3f * (mGridpoints[i].x - bounds.getCenterX()));
//            uGridpoints[i].incV(1f);
//            vGridpoints[i].incV(5f);
//            float x1 = mGridpoints[i].x - bounds.getCenterX(), y1 = mGridpoints[i].y - bounds.getCenterY();
//            float mag = (float) Math.sqrt(x1*x1 + y1*y1);
//            mag = mag == 0f? 1f : mag;
////            mag = 1f;
//            vGridpoints[i].setV(-10f * y1/mag);
//            uGridpoints[i].setV(-10f * x1/mag);
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

        for (int x = 1; x < cols - 1; x++) {
            for (int y = 1; y < rows - 1; y++) {
                cellTypes[x + y * cols] = PICGrid.CellType.AIR;
            }
        }
    }

    private int selectGridpoint(float x, float y)
    {
        int xi = (int) ((x - bounds.getXMin()) * res);
        int yi = (int) ((y - bounds.getYMin()) * res);
        return xi + yi * cols;
    }

    private int selectCell(float x, float y)
    {
        int xi = (int) ((x - bounds.getXMin()) * res);
        int yi = (int) ((y - bounds.getYMin()) * res);
        if(xi == cols - 1) xi--;
        if(yi == rows - 1) yi--;
        return xi + yi * cols;
    }

    public int selectCell(Particle p)
    {
        return selectCell(p.x + 1f / res, p.y + 1f / res);
    }

    public int[] selectCells(int i)
    {
        return new int[]{i + 1, i + cols, i - 1, i - cols};
    }

    public int selectMGridpoint(PICParticle p)
    {
        return selectGridpoint(p.x + 0.5f / res, p.y + 0.5f / res);
    }

    public int selectUGridpoint(PICParticle p)
    {
        return selectGridpoint(p.x, p.y + 0.5f / res);
    }

    public int selectVGridpoint(PICParticle p)
    {
        return selectGridpoint(p.x + 0.5f / res, p.y);
    }

    public int[] selectGridpoints(int i)
    {
        return new int[]{i, i + cols, i + 1, i + cols + 1};
    }

    public int[] selectCellVelocities(int cell)
    {
        return new int[]{cell, cell, cell - 1, cell - cols};
    }

    @Override
    public void render(Renderer renderer)
    {
        renderCells(renderer);
//        renderStaggeredGrid(renderer);
    }

    private void renderCells(Renderer renderer)
    {
        for (int i = 0; i < mGridpoints.length; i++) {
            float wScale = windowBounds.getWidth()/bounds.getWidth();
            float hScale = windowBounds.getHeight()/bounds.getHeight();

            int color = getCellColor(i);
            renderer.drawRectangle((int) ((mGridpoints[i].x - 0.5f / res) * wScale + windowBounds.getXMin()), (int) ((mGridpoints[i].y + 0.5f / res) * hScale + windowBounds.getYMin()), (int) (wScale/ res), (int) (hScale/ res), color);
        }
    }

    private void renderStaggeredGrid(Renderer renderer)
    {
        for (int i = 0; i < mGridpoints.length; i++) {
            float wScale = windowBounds.getWidth()/bounds.getWidth();
            float hScale = windowBounds.getHeight()/bounds.getHeight();

            renderer.drawDottedRectangle((int) ((mGridpoints[i].x - 0.5f / res) * wScale + windowBounds.getXMin()), (int) ((mGridpoints[i].y + 0.5f / res) * hScale + windowBounds.getYMin()), (int) (wScale/ res), (int) (hScale/ res), 0xFF888888);
            renderer.drawEmptyCircle((int) (mGridpoints[i].x * wScale + windowBounds.getXMin()), (int) (mGridpoints[i].y * hScale + windowBounds.getYMin()), 3, mGridpoints[i].getColor());
            renderer.drawEmptyCircle((int) (uGridpoints[i].x * wScale + windowBounds.getXMin()), (int) (uGridpoints[i].y * hScale + windowBounds.getYMin()), 3, uGridpoints[i].getColor());
            renderer.drawEmptyCircle((int) (vGridpoints[i].x * wScale + windowBounds.getXMin()), (int) (vGridpoints[i].y * hScale + windowBounds.getYMin()), 3, vGridpoints[i].getColor());
        }
    }

    private int getCellColor(int i)
    {
        int color = 0xFF000000;
        if(cellTypes[i] == CellType.WATER) color = 0xFF2277BB;
        if(cellTypes[i] == CellType.AIR) color = 0xFF000000;
        if(cellTypes[i] == CellType.WALL) color = 0xFFAAAAAA;
        return color;
    }

    public enum CellType
    {
        WATER(1), AIR(1), WALL(0);

        final float s;

        CellType(float s) {
            this.s = s;
        }
    }
}
