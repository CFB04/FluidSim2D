package cfbastian.fluidsim2d.simulation.pic;

import cfbastian.fluidsim2d.simulation.util.Bounds;

public class PICGrid {
    public PICGridpoint[] gridpoints;
    Bounds bounds;

    int rows, cols;

    private float invGridStepWidth, invGridStepHeight;

    public PICGrid(Bounds bounds, int rows, int cols) {
        this.bounds = bounds;
        this.rows = rows;
        this.cols = cols;

        this.invGridStepWidth = (cols - 1f) / bounds.getWidth();
        this.invGridStepHeight = (rows - 1f) / bounds.getHeight();

        this.gridpoints = new PICGridpoint[rows * cols];
        for (int i = 0; i < gridpoints.length; i++) {
            int x = i % cols, y = i / cols;
            gridpoints[i] = new PICGridpoint(
                    bounds.getxMin() + x / invGridStepWidth,
                    bounds.getyMin() + y / invGridStepHeight,
                    0f, 0f, 0xFFAAAAAA);
        }
    }

    public void init()
    {
        // Set initial velocities
        for (int i = 0; i < gridpoints.length; i++) {
            gridpoints[i].reset();
//            gridpoints[i].setVY(1f);
            gridpoints[i].setVX(-2f * (gridpoints[i].getY() - bounds.getCenterY()));
            gridpoints[i].setVY(2f * (gridpoints[i].getX() - bounds.getCenterX()));
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
        for (int i = 0; i < gridpoints.length; i++) gridpoints[i].reset();
    }

    public int selectGridpoint(float x, float y)
    {
        int xi = (int) ((x - bounds.getxMin()) * invGridStepWidth);
        int yi = (int) ((y - bounds.getyMin()) * invGridStepHeight);
        xi -= x >= bounds.getxMax()? 1 : 0;
        yi -= y >= bounds.getyMax()? 1 : 0;
        return xi + yi * cols;
    }

    public int selectGridpoint(PICParticle p)
    {
        return selectGridpoint(p.getX(), p.getY());
    }

    public int[] selectGridpoints(PICParticle p)
    {
        int[] g = new int[4];
        g[0] = selectGridpoint(p);
        g[1] = g[0] + cols;
        g[2] = g[0] + 1;
        g[3] = g[0] + cols + 1;
        return g;
    }
}
