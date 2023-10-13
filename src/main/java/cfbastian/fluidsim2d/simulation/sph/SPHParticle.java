package cfbastian.fluidsim2d.simulation.sph;

import cfbastian.fluidsim2d.simulation.Particle;
import cfbastian.fluidsim2d.simulation.util.Bounds;

public class SPHParticle extends Particle {
    private float r;

    public SPHParticle(float x, float y, float dx, float dy, int col, float r) {
        super(x, y, dx, dy, col);
        this.r = r;
    }

    public float getR() {
        return r;
    }
}
