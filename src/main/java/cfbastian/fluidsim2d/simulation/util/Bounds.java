package cfbastian.fluidsim2d.simulation.util;

public class Bounds {

    float xMin, xMax, yMin, yMax;

    public Bounds(float xMin, float xMax, float yMin, float yMax) {
        this.xMin = Math.min(xMin, xMax);
        this.xMax = Math.max(xMin, xMax);
        this.yMin = Math.min(yMin, yMax);
        this.yMax = Math.max(yMin, yMax);
    }

    public float getWidth()
    {
        return xMax - xMin;
    }

    public float getHeight()
    {
        return yMax - yMin;
    }

    public float getCenterX()
    {
        return xMin + getWidth()/2f;
    }

    public float getCenterY()
    {
        return yMin + getHeight()/2f;
    }

    public float getXMin() {
        return xMin;
    }

    public void setXMin(float xMin) {
        this.xMin = xMin;
    }

    public float getXMax() {
        return xMax;
    }

    public void setXMax(float xMax) {
        this.xMax = xMax;
    }

    public float getYMin() {
        return yMin;
    }

    public void setYMin(float yMin) {
        this.yMin = yMin;
    }

    public float getYMax() {
        return yMax;
    }

    public void setYMax(float yMax) {
        this.yMax = yMax;
    }
}
