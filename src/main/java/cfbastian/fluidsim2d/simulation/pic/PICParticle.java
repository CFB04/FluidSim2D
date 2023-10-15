package cfbastian.fluidsim2d.simulation.pic;

import cfbastian.fluidsim2d.simulation.Particle;
import cfbastian.fluidsim2d.simulation.util.Bounds;

public class PICParticle extends Particle {
    private float r, m;

    public PICParticle(float x, float y, float dx, float dy, int col, float r, float m) {
        super(x, y, dx, dy, col);
        this.r = r;
        this.m = m;
    }

    public float getInfluence(float d)
    {
        float d1 = Math.max(r - Math.abs(d), 0f)/r;
        return d1*d1*d1;
    }

    public float getR() {
        return r;
    }

    public float getM() {
        return m;
    }

    public void incDx(float v)
    {
        dx += v;
    }

    public void incDy(float v)
    {
        dy += v;
    }

    public void update(Bounds bounds)
    {
        update();
        x = Math.max(x, bounds.getxMin());
        x = Math.min(x, bounds.getxMax());
        y = Math.max(y, bounds.getyMin());
        y = Math.min(y, bounds.getyMax());
    }
}
