package cfbastian.fluidsim2d.simulation.pic;

import cfbastian.fluidsim2d.simulation.Gridpoint;

public class PICGridpoint extends Gridpoint {
    protected float vX, vY, m; //momentum and mass

    public PICGridpoint(float x, float y, float vX, float vY, int color) {
        super(x, y, color);
        this.vX = vX;
        this.vY = vY;
        this.m = 0f;
    }

    public float getVX() {
        return vX;
    }

    public void setVX(float vX) {
        this.vX = vX;
    }

    public float getVY() {
        return vY;
    }

    public void setVY(float vY) {
        this.vY = vY;
    }

    public void incM(float dm)
    {
        m += dm;
    }

    public void incVX(float dVX)
    {
        vX += dVX;
    }

    public void incVY(float dVY)
    {
        vY += dVY;
    }

    public float getM() {
        return m;
    }

    public void setM(float m) {
        this.m = m;
    }

    public void reset()
    {
        vX = 0f;
        vY = 0f;
        m = 0f;
    }
}
