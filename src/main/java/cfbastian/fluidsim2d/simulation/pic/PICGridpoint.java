package cfbastian.fluidsim2d.simulation.pic;

import cfbastian.fluidsim2d.simulation.Gridpoint;

public class PICGridpoint extends Gridpoint {
    protected float dRhoX, dRhoY, m; //momentum and mass

    public PICGridpoint(float x, float y, float dRhoX, float dRhoY, int color) {
        super(x, y, color);
        this.dRhoX = dRhoX;
        this.dRhoY = dRhoY;
        this.m = 0f;
    }

    public float getdRhoX() {
        return dRhoX;
    }

    public void setdRhoX(float dRhoX) {
        this.dRhoX = dRhoX;
    }

    public float getdRhoY() {
        return dRhoY;
    }

    public void setdRhoY(float dRhoY) {
        this.dRhoY = dRhoY;
    }

    public float getM() {
        return m;
    }

    public void setM(float m) {
        this.m = m;
    }
}
