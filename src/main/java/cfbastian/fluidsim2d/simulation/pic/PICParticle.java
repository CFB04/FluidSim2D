package cfbastian.fluidsim2d.simulation.pic;

import cfbastian.fluidsim2d.simulation.Particle;
import cfbastian.fluidsim2d.simulation.util.Bounds;

public class PICParticle extends Particle {
    public float m;

    public PICParticle(float x, float y, float dx, float dy, int col, float m) {
        super(x, y, dx, dy, col);
        this.m = m;
    }

    public void update(Bounds bounds, float dt)
    {
        x += dx * dt;
        y += dy * dt;
//        update();
        x = Math.max(x, bounds.getXMin());
        x = Math.min(x, bounds.getXMax());
        y = Math.max(y, bounds.getYMin());
        y = Math.min(y, bounds.getYMax());
    }
}
