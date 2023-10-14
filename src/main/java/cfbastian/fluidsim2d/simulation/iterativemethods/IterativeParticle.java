package cfbastian.fluidsim2d.simulation.iterativemethods;

import cfbastian.fluidsim2d.simulation.Particle;

public class IterativeParticle extends Particle {

    public IterativeParticle(float x, float y, float dx, float dy, int color) {
        super(x, y, dx, dy, color);
    }

    public void updateEulerian(float dt)
    {
        dx = getDx(x, y, dt);
        dy = getDy(x, y, dt);
        update();
    };

    public void updateRK2(float dt)
    {
        float k1x = getDx(x, y, dt);
        float k1y = getDy(x, y, dt);
        float k2y = getDy(x + k1x, y + k1y, dt);
        float k2x = getDx(x + k1x, y + k1y, dt);

        dx = 0.5f * (k1x + k2x);
        dy = 0.5f * (k1y + k2y);
        update();
    }

    public void updateLeapFrog()
    {

    }

    public float getDx(float x, float y, float dt)
    {
        float r = (float) Math.sqrt(x*x + y*y);
        return y / r * 4f * dt;
    }

    public float getDy(float x, float y, float dt)
    {
        float r = (float) Math.sqrt(x*x + y*y);
        return -x / r * 4f * dt;
    }
}
