package cfbastian.fluidsim2d.simulation;

public class Gridpoint {
    protected float x, y;
    protected float v, w;
    protected int color;

    public Gridpoint(float x, float y, int color) {
        this.x = x;
        this.y = y;
        this.color = color;
    }

    public void reset()
    {
        v = 0f;
        w = 0f;
    }

    public float getV() {
        return v;
    }

    public void setV(float v) {
        this.v = v;
    }

    public void incV(float dv)
    {
        v += dv;
    }

    public float getW() {
        return w;
    }

    public void setW(float w) {
        this.w = w;
    }

    public void incW(float dw)
    {
        w += dw;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}
