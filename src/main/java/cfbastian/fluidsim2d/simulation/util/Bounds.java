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

    public float getxMin() {
        return xMin;
    }

    public void setxMin(float xMin) {
        this.xMin = xMin;
    }

    public float getxMax() {
        return xMax;
    }

    public void setxMax(float xMax) {
        this.xMax = xMax;
    }

    public float getyMin() {
        return yMin;
    }

    public void setyMin(float yMin) {
        this.yMin = yMin;
    }

    public float getyMax() {
        return yMax;
    }

    public void setyMax(float yMax) {
        this.yMax = yMax;
    }
}
