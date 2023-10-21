package cfbastian.fluidsim2d.simulation.sph;

import cfbastian.fluidsim2d.Application;
import cfbastian.fluidsim2d.Renderer;
import cfbastian.fluidsim2d.simulation.Simulation;
import cfbastian.fluidsim2d.simulation.util.Bounds;

import java.util.Random;

public class SPHSimulation extends Simulation {
    SPHParticle[] particles;
    float decay = 0.7f;

    public SPHSimulation(Bounds bounds, int numParticles, Bounds particleBounds, int particlesPerRow) {
        super(bounds);
        this.particles = new SPHParticle[numParticles];

        int particlesPerCol = numParticles/particlesPerRow;

        Random r = new Random();

        for (int i = 0; i < numParticles; i++)
            this.particles[i] = new SPHParticle(
                    particleBounds.getXMin() + particleBounds.getWidth()/particlesPerRow * (i % particlesPerRow),
                    particleBounds.getYMin() + particleBounds.getHeight()/particlesPerCol * (i / particlesPerRow),
                    (float) r.nextGaussian() * 5f, (float) r.nextGaussian()*2.5f, 0xFF22FFFF, 0.01f);
    }

    @Override
    public void update(float dt)
    {
        for (SPHParticle p : particles)
        {
            p.accelerate(0f, -9.81f * dt);
            float xNew = p.getX() + p.getDx() * dt;
            float yNew = p.getY() + p.getDy() * dt;
            if(xNew < bounds.getXMin())
            {
                xNew = 2 * bounds.getXMin() - xNew;
                p.setDx(-p.getDx() * decay);
            }
            if(xNew > bounds.getXMax())
            {
                xNew = 2 * bounds.getXMax() - xNew;
                p.setDx(-p.getDx() * decay);
            }
            if(yNew < bounds.getYMin())
            {
                yNew = 2 * bounds.getYMin() - yNew;
                p.setDy(-p.getDy() * decay);
            }
            if(yNew > bounds.getYMax())
            {
                yNew = 2 * bounds.getYMax() - yNew;
                p.setDy(-p.getDy() * decay);
            }
            p.setX(xNew);
            p.setY(yNew);
        }
    }

    public SPHParticle[] getParticles() {
        return particles;
    }

    @Override
    public void render(Renderer renderer) {
        for (SPHParticle p : particles)
        {
            float x = p.getX() * Application.width / bounds.getWidth();
            float y = (bounds.getHeight() - p.getY()) * Application.height / bounds.getHeight();
            float r = p.getR() * Application.width / bounds.getWidth();
            renderer.drawCircle((int) x, (int) y, (int) r, p.getColor());
        }
    }
}
