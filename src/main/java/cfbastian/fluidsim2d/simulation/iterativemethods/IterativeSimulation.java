package cfbastian.fluidsim2d.simulation.iterativemethods;

import cfbastian.fluidsim2d.Application;
import cfbastian.fluidsim2d.Renderer;
import cfbastian.fluidsim2d.simulation.Particle;
import cfbastian.fluidsim2d.simulation.Simulation;
import cfbastian.fluidsim2d.simulation.util.Bounds;

import java.util.Random;

public class IterativeSimulation extends Simulation {
    IterativeParticle[] particles;

    public IterativeSimulation(Bounds bounds, int numParticles) {
        super(bounds);
        this.particles = new IterativeParticle[numParticles];

        Random r = new Random();

        for (int i = 0; i < particles.length; i++)
            particles[i] = new IterativeParticle((r.nextFloat() - 0.5f) * bounds.getHeight(), (r.nextFloat() - 0.5f) * bounds.getHeight(), 0f, 0f, 0xFF22FFFF);
    }

    @Override
    public void render(Renderer renderer)
    {
        for (int i = 0; i < 32; i++) {
            renderer.drawEmptyCircle(Application.width/2, Application.height/2, Application.width/32 * i, 0xFF999999);
        }

        for (Particle p : particles)
        {
            float x = (p.getX() + bounds.getWidth()/2f) * Application.width / bounds.getWidth();
            float y = (bounds.getHeight()/2f - p.getY()) * Application.height / bounds.getHeight();
            float r = 0.05f * Application.width / bounds.getWidth();
            renderer.drawCircle((int) x, (int) y, (int) r, p.getColor());
        }
    }

    @Override
    public void update(float dt)
    {
        for (IterativeParticle p : particles)
            p.updateRK2(dt);
    }
}
