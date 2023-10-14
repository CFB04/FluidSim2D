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

    public void updateRK3(float dt)
    {
        float k1x = getDx(x, y, dt);
        float k1y = getDy(x, y, dt);
        float k2y = getDy(x + 0.5f * k1x, y + 0.5f * k1y, dt);
        float k2x = getDx(x + 0.5f * k1x, y + 0.5f * k1y, dt);
        float k_1y = getDy(x + k1x, y + k1y, dt);
        float k_1x = getDx(x + k1x, y + k1y, dt);
        float k3y = getDy(x + k_1x, y + k_1y, dt);
        float k3x = getDx(x + k_1x, y + k_1y, dt);

        dx = 1f/6f * (k1x + 4*k2x + k3x);
        dy = 1f/6f * (k1y + 4*k2y + k3y);
        update();
    }

    public void updateRK4(float dt)
    {
        float k1x = getDx(x, y, dt);
        float k1y = getDy(x, y, dt);
        float k2y = getDy(x + 0.5f * k1x, y + 0.5f * k1y, dt);
        float k2x = getDx(x + 0.5f * k1x, y + 0.5f * k1y, dt);
        float k3y = getDy(x + 0.5f * k2x, y + 0.5f * k2y, dt);
        float k3x = getDx(x + 0.5f * k2x, y + 0.5f * k2y, dt);
        float k4y = getDy(x + k3x, y + k3y, dt);
        float k4x = getDx(x + k3x, y + k3y, dt);

        dx = 1f/6f * (k1x + 2*k2x + 2*k3x + k4x);
        dy = 1f/6f * (k1y + 2*k2y + 2*k3y + k4y);
        update();
    }

    public void updateLeapFrog(float dt) {
        float k1x = getDx(x, y, dt);
        float k1y = getDy(x, y, dt);
        dx = getDx(x + k1x * 0.5f, y + k1y * 0.5f, dt);
        dy = getDy(x + k1x * 0.5f, y + k1y * 0.5f, dt);
        update();
    }

    public void updateYoshidaOrder4()
    {

    }

    public float getDx(float x, float y, float dt)
    {
        float r = (float) Math.sqrt(x*x + y*y);
        return y / r * 32f * dt;
    }

    public float getDy(float x, float y, float dt)
    {
        float r = (float) Math.sqrt(x*x + y*y);
        return -x / r * 32f * dt;
    }
}
