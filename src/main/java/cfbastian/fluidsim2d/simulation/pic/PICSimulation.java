package cfbastian.fluidsim2d.simulation.pic;

import cfbastian.fluidsim2d.Application;
import cfbastian.fluidsim2d.Renderer;
import cfbastian.fluidsim2d.simulation.Simulation;
import cfbastian.fluidsim2d.simulation.util.Bounds;
import cfbastian.fluidsim2d.simulation.util.SimMath;


/**
 * The PIC loop works like this:<br>
 * <br>Particle kinematics
 * <br>Particles -> Grid transfer
 * <br>Grid dynamics (gravity, pressure projection)
 * <br>Grid -> Particles transfer
 */
public class PICSimulation extends Simulation {

    // TODO add boundary gridpoints
    // TODO add gauss-seidel incompressibility

    PICGrid grid;

    private final PICParticle[] particles;

    private final int rows, cols;

    private final float invGridStepWidth, invGridStepHeight;

    private final Bounds windowBounds;

    public PICSimulation(Bounds bounds, Bounds windowBounds, Bounds particleBounds, int numParticles, int particlesPerRow, int rows, int cols)
    {
        super(bounds);
        this.windowBounds = windowBounds;
        this.rows = rows;
        this.cols = cols;
        this.invGridStepWidth = (cols - 1f) / bounds.getWidth();
        this.invGridStepHeight = (rows - 1f) / bounds.getHeight();
        this.particles = new PICParticle[numParticles];

        int particlesPerCol = numParticles/particlesPerRow;

        grid = new PICGrid(bounds, rows, cols);

        for (int i = 0; i < numParticles; i++)
            this.particles[i] = new PICParticle(
                    particleBounds.getxMin() + particleBounds.getWidth()/(particlesPerRow - 1) * (i % particlesPerRow),
                    particleBounds.getyMin() + particleBounds.getHeight()/(particlesPerCol - 1) * (i / particlesPerRow),
                    0f, 0f, 0xFF22FFFF, 0.05f, 1f);
    }

    @Override
    public void init()
    {
        grid.init();
    }

    /**
     * This handles the grid to particle transfer as well as particle kinematics
     */
    private synchronized void lagrangeStep(float dt)
    {
        for (int i = 0; i < particles.length; i++) {
            int[] g = grid.selectGridpoints(particles[i]);

            //Receive velocities
            float x1 = particles[i].getX() - grid.gridpoints[g[0]].getX();
            float y1 = particles[i].getY() - grid.gridpoints[g[0]].getY();
            float x2 = x1 * invGridStepWidth, y2 = y1 * invGridStepHeight;

            float vX = SimMath.lerp(
                    SimMath.lerp(grid.gridpoints[g[0]].getVX(), grid.gridpoints[g[2]].getVX(), x2),
                    SimMath.lerp(grid.gridpoints[g[1]].getVX(), grid.gridpoints[g[3]].getVX(), x2),
                    y2);
            float vY = SimMath.lerp(
                    SimMath.lerp(grid.gridpoints[g[0]].getVY(), grid.gridpoints[g[2]].getVY(), x2),
                    SimMath.lerp(grid.gridpoints[g[1]].getVY(), grid.gridpoints[g[3]].getVY(), x2),
                    y2);

            //Kinematics
            particles[i].setDx(vX);
            particles[i].setDy(vY);
            particles[i].update(bounds, dt);
        }
    }

    /**
     * This handles the particle to grid transfer
     */
    public void transferLagrangian()
    {
        grid.reset();
        for (int i = 0; i < particles.length; i++) {
            int[] g = grid.selectGridpoints(particles[i]);

            //Impart mass and momentum
            float[] w = new float[4];
            float x1 = particles[i].getX() - grid.gridpoints[g[0]].getX();
            float y1 = particles[i].getY() - grid.gridpoints[g[0]].getY();
            float x2 = x1 * invGridStepWidth, y2 = y1 * invGridStepHeight;

            w[0] = (1f - x2) * (1f - y2);
            w[1] = (1f - x2) * y2;
            w[2] = x2 * (1f - y2);
            w[3] = x2 * y2;

            float sum = w[0] + w[1] + w[2] + w[3];
            sum = sum == 0f? 1f : sum;
            w[0] /= sum;
            w[1] /= sum;
            w[2] /= sum;
            w[3] /= sum;

            for (int j = 0; j < 4; j++) {
                grid.gridpoints[g[j]].incM(particles[i].getM() * w[j]);
                grid.gridpoints[g[j]].incVX(particles[i].getDx() * w[j]);
                grid.gridpoints[g[j]].incVY(particles[i].getDy() * w[j]);
            }
        }

        for (int i = 0; i < grid.gridpoints.length; i++) {
            float invM = grid.gridpoints[i].getM();
            if(invM != 0) {
                invM = 1f/invM;
                grid.gridpoints[i].setVX(grid.gridpoints[i].getVX() * invM);
                grid.gridpoints[i].setVY(grid.gridpoints[i].getVY() * invM);
            }
        }
    }

    /**
     * This handles the grid dynamics
     */
    public void eulerianStep(float dt)
    {
        for (int i = 0; i < grid.gridpoints.length; i++) {
            //Dynamics
            grid.gridpoints[i].incVY(-9.81f * dt);
        }
    }

    @Override
    public void update(float dt)
    {
        lagrangeStep(dt);
        transferLagrangian();
        eulerianStep(dt);
    }

    @Override
    public void render(Renderer renderer)
    {
        renderBox(renderer);
        renderGridpoints(renderer);
        renderParticles(renderer);
    }

    public void renderBox(Renderer renderer)
    {
        renderer.drawRectangle((int) windowBounds.getxMin() - 1, (int) windowBounds.getyMin() - 1, (int) windowBounds.getWidth() + 1, (int) windowBounds.getHeight() + 1, 0xFFFFFFFF);
    }

    public void renderGridpoints(Renderer renderer)
    {
        for (int i = 0; i < grid.gridpoints.length; i++) {
            renderer.drawEmptyCircle((int) (grid.gridpoints[i].getX() * windowBounds.getWidth()/bounds.getWidth() + windowBounds.getxMin()), (int) (windowBounds.getHeight() - (grid.gridpoints[i].getY() * windowBounds.getHeight()/bounds.getHeight()) + windowBounds.getyMin()), 2, grid.gridpoints[i].getColor());
        }
    }

    public void renderParticles(Renderer renderer)
    {
        for (int i = 0; i < particles.length; i++) {
            float x = windowBounds.getxMin() + particles[i].getX() * windowBounds.getWidth() / bounds.getWidth();
            float y = windowBounds.getyMin() + (bounds.getHeight() - particles[i].getY()) * windowBounds.getHeight() / bounds.getHeight();
            float r = particles[i].getR() * Application.width / bounds.getWidth();
            renderer.drawCircle((int) x, (int) y, (int) r, particles[i].getColor());
        }
    }
}
