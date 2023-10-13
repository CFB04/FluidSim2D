package cfbastian.fluidsim2d.simulation;

public class Particle {
    private float x, y, dx, dy;
    private int color;

    public Particle(float x, float y, float dx, float dy, int color) {
        this.x = x;
        this.y = y;
        this.dx = dx;
        this.dy = dy;
        this.color = color;
    }

    public void update()
    {
        x += dx;
        y += dy;
    }

    public void accelerate(float d2x, float d2y)
    {
        dx += d2x;
        dy += d2y;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getDx() {
        return dx;
    }

    public void setDx(float dx) {
        this.dx = dx;
    }

    public float getDy() {
        return dy;
    }

    public void setDy(float dy) {
        this.dy = dy;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}
