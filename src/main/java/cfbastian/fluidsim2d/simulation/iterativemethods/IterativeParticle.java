package cfbastian.fluidsim2d.simulation.iterativemethods;

import cfbastian.fluidsim2d.simulation.Particle;

public class IterativeParticle extends Particle {

    private float speed = 1f;

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

    public void updateCircular(float dt, float du)
    {
        float v1x = du * getDx(x, y, dt);
        float v1y = du * getDy(x, y, dt);
        float m1 = (float) Math.sqrt(v1x*v1x + v1y*v1y);
        if(m1 == 0f) return;
        float v2x = du * getDx(x + v1x / m1, y + v1y / m1, dt);
        float v2y = du * getDy(x + v1x / m1, y + v1y / m1, dt);
        float m2 = (float) Math.sqrt(v2x*v2x + v2y*v2y);
        if(m2 == 0f) return;
        float dot = (v1x * v2x + v1y * v2y) / (m1 * m2);
        float rPartial = 1f - dot*dot;
        if(Math.abs(rPartial) < 1E-6f)
        {
            dx = v1x/du;
            dy = v1y/du;
            update();
            return;
        }
        float r = dot / (float) Math.sqrt(rPartial);
        if(r == 0f) return;
        float sign = Math.signum(v1x*v2y-v1y*v2x);
        float xRel = sign * r * v1y / m1;
        float yRel = -sign * r * v1x / m1;
        double k = (sign / r * speed * dt + Math.atan2(yRel, xRel));
        dx = r * (float) Math.cos(k) - xRel;
        dy = r * (float) Math.sin(k) - yRel;
        update();
    }

    public float getDx(float x, float y, float dt)
    {
        x -= 1f;
        float r = (float) Math.sqrt(x*x + y*y);
        if(r == 0f) return 0f;
//        return 1f * speed * dt;
        return y / r * speed * dt;
    }

    public float getDy(float x, float y, float dt)
    {
        x -= 1f;
        float r = (float) Math.sqrt(x*x + y*y);
        if(r == 0f) return 0f;
//        return (float) Math.sin(x) * speed * dt;
        return -x / r * speed * dt;
    }
}
