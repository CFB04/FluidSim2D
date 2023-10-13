package cfbastian.fluidsim2d.simulation.sph;

import cfbastian.fluidsim2d.Renderer;
import cfbastian.fluidsim2d.simulation.Renderable;
import cfbastian.fluidsim2d.simulation.Updateable;

public class SPHSimulation implements Updateable, Renderable {
    SPHParticle[] particles;

    public SPHSimulation(int numParticles) {
        this.particles = new SPHParticle[numParticles];
    }

    @Override
    public void update(float dt)
    {
        for (SPHParticle p : particles)
        {
            p.accelerate(10f * dt, 0f);
            p.update();
        }
    }

    public SPHParticle[] getParticles() {
        return particles;
    }

    @Override
    public void render(Renderer renderer) {
        for (SPHParticle p : particles) renderer.drawCircle((int) p.getX(), (int) p.getY(), (int) p.getR(), p.getColor());
    }
}
