package cfbastian.fluidsim2d.simulation.sph;

import cfbastian.fluidsim2d.simulation.Particle;

public class SPHParticle extends Particle {
    private final float r;

    public SPHParticle(float x, float y, float dx, float dy, int col, float r) {
        super(x, y, dx, dy, col);
        this.r = r;
    }

    public float getInfluence(float d)
    {
        float d1 = Math.max(r - Math.abs(d), 0f)/r;
        return d1*d1*d1;
    }

    public float getR() {
        return r;
    }
}
