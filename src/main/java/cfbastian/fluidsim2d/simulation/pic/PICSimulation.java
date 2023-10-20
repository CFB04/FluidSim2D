package cfbastian.fluidsim2d.simulation.pic;

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

    // TODO add gauss-seidel incompressibility

    PICGrid grid;

    private final PICParticle[] particles;

    private final Bounds windowBounds;

    public PICSimulation(Bounds bounds, Bounds windowBounds, Bounds particleBounds, int numParticles, int particlesPerRow, int rows, int cols)
    {
        super(bounds);
        this.windowBounds = windowBounds;
        this.particles = new PICParticle[numParticles];

        int particlesPerCol = numParticles/particlesPerRow;

        grid = new PICGrid(bounds, windowBounds, rows, cols);

        for (int i = 0; i < numParticles; i++)
            this.particles[i] = new PICParticle(
                    particleBounds.getxMin() + 0.5f * particleBounds.getWidth()/(particlesPerRow) + particleBounds.getWidth()/(particlesPerRow) * (i % particlesPerRow),
                    particleBounds.getyMin() + 0.5f * particleBounds.getWidth()/(particlesPerRow) + particleBounds.getHeight()/(particlesPerCol) * (i / particlesPerRow),
                    0f, 0f, 0xFF22FFFF, 0.05f, 1f);
    }

    @Override
    public void init()
    {
        grid.init();
        for (int i = 0; i < particles.length; i++) grid.cellTypes[grid.selectCell(particles[i])] = PICGrid.CellType.WATER;
    }

    /**
     * This handles the grid to particle transfer as well as particle kinematics
     */
    private synchronized void lagrangeStep1(float dt)
    {
        for (int i = 0; i < particles.length; i++) {
            int[] gM = grid.selectGridpoints(grid.selectMGridpoint(particles[i]));
            int[] gU = grid.selectGridpoints(grid.selectUGridpoint(particles[i]));
            int[] gV = grid.selectGridpoints(grid.selectVGridpoint(particles[i]));

            //Receive velocities
            float x1 = particles[i].getX() - grid.mGridpoints[gM[0]].getX();
            float y1 = particles[i].getY() - grid.mGridpoints[gM[0]].getY();
            float x2 = x1 * grid.getInvGridCellWidth(), y2 = y1 * grid.getInvGridCellHeight();

            float vX = SimMath.lerp(
                    SimMath.lerp(grid.uGridpoints[gU[0]].getV(), grid.uGridpoints[gU[1]].getV(), y2),
                    SimMath.lerp(grid.uGridpoints[gU[2]].getV(), grid.uGridpoints[gU[3]].getV(), y2),
                    (x2 + 0.5f) % 1f);
            float vY = SimMath.lerp(
                    SimMath.lerp(grid.vGridpoints[gV[0]].getV(), grid.vGridpoints[gV[2]].getV(), x2),
                    SimMath.lerp(grid.vGridpoints[gV[1]].getV(), grid.vGridpoints[gV[3]].getV(), x2),
                    (y2 + 0.5f) % 1f);

            particles[i].setDx(vX);
            particles[i].setDy(vY);

            //Kinematics
            particles[i].update(bounds, dt);
        }
    }

    /**
     * This handles the particle to grid transfer
     */
    public void transferLagrangian1()
    {
        grid.reset();
        for (int x = 1; x < grid.getCols() - 1; x++) {
            for (int y = 1; y < grid.getRows() - 1; y++) {
                grid.cellTypes[x + y * grid.getCols()] = PICGrid.CellType.AIR;
            }
        }

        for (int i = 0; i < particles.length; i++) {
            int[] gM = grid.selectGridpoints(grid.selectMGridpoint(particles[i]));
            int[] gU = grid.selectGridpoints(grid.selectUGridpoint(particles[i]));
            int[] gV = grid.selectGridpoints(grid.selectVGridpoint(particles[i]));

            //Impart mass and momentum
            float x1 = particles[i].getX() - grid.mGridpoints[gM[0]].getX();
            float y1 = particles[i].getY() - grid.mGridpoints[gM[0]].getY();
            float x2 = x1 * grid.getInvGridCellWidth(), y2 = y1 * grid.getInvGridCellHeight();

            float[] w = new float[4];
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
                grid.mGridpoints[gM[j]].incV(particles[i].getM() * w[j]);
                grid.mGridpoints[gM[j]].incW(w[j]);
            }

            float x3 = (x2 + 0.5f) % 1f;
            w[0] = (1f - x3) * (1f - y2);
            w[1] = (1f - x3) * y2;
            w[2] = x3 * (1f - y2);
            w[3] = x3 * y2;
            sum = w[0] + w[1] + w[2] + w[3];
            sum = sum == 0f? 1f : sum;
            w[0] /= sum;
            w[1] /= sum;
            w[2] /= sum;
            w[3] /= sum;
            for (int j = 0; j < 4; j++) {
                grid.uGridpoints[gU[j]].incV(particles[i].getDx() * w[j]);
                grid.uGridpoints[gU[j]].incW(w[j]);
            }

            float y3 = (y2 + 0.5f) % 1f;
            w[0] = (1f - x2) * (1f - y3);
            w[1] = (1f - x2) * y3;
            w[2] = x2 * (1f - y3);
            w[3] = x2 * y3;
            sum = w[0] + w[1] + w[2] + w[3];
            sum = sum == 0f? 1f : sum;
            w[0] /= sum;
            w[1] /= sum;
            w[2] /= sum;
            w[3] /= sum;
            for (int j = 0; j < 4; j++) {
                grid.vGridpoints[gV[j]].incV(particles[i].getDy() * w[j]);
                grid.vGridpoints[gV[j]].incW(w[j]);
            }

            grid.cellTypes[grid.selectCell(particles[i])] = PICGrid.CellType.WATER;
        }

        for (int i = 0; i < grid.mGridpoints.length; i++) {
            float wM = grid.mGridpoints[i].getW(), wU = grid.uGridpoints[i].getW(), wV = grid.vGridpoints[i].getW();
            wM = wM == 0f? 1f : 1f / wM;
            wU = wU == 0f? 1f : 1f / wU;
            wV = wV == 0f? 1f : 1f / wV;
            grid.mGridpoints[i].setV(grid.mGridpoints[i].getV() * wM);
            grid.uGridpoints[i].setV(grid.uGridpoints[i].getV() * wU);
            grid.vGridpoints[i].setV(grid.vGridpoints[i].getV() * wV);
        }
    }

    /**
     * This handles the grid dynamics
     */
    public void eulerianStep(float dt)
    {
        //Dynamics
        int maxSteps = 20;
        float divTolerance = 0.001f;
        float divergence = Float.MAX_VALUE, s;
        float oRFactor = 1.25f; // Over-relaxation factor
        for (int i = 0; i < maxSteps; i++) {
            for (int x = 1; x < grid.getCols() - 1; x++) {
                for (int y = 1; y < grid.getRows() - 1; y++) {
                    int j = x + y * grid.getCols();

                    int[] g = grid.getCellVelocities(j);
                    int[] c = grid.selectCells(j);

                    float u1 = grid.uGridpoints[g[0]].getV();
                    float v1 = grid.vGridpoints[g[1]].getV();
                    float u2 = grid.uGridpoints[g[2]].getV();
                    float v2 = grid.vGridpoints[g[3]].getV();

                    float s0 = grid.cellTypes[c[0]].s;
                    float s1 = grid.cellTypes[c[1]].s;
                    float s2 = grid.cellTypes[c[2]].s;
                    float s3 = grid.cellTypes[c[3]].s;

                    divergence = u1 + v1 - u2 - v2;

                    s = s0 + s1 + s2 + s3;
                    s = 1f / s;

                    grid.uGridpoints[g[0]].setV(u1 - divergence * s0 * s * oRFactor);
                    grid.vGridpoints[g[1]].setV(v1 - divergence * s1 * s * oRFactor);
                    grid.uGridpoints[g[2]].setV(u2 + divergence * s2 * s * oRFactor);
                    grid.vGridpoints[g[3]].setV(v2 + divergence * s3 * s * oRFactor);
                }
            }

            if (Math.abs(divergence) < divTolerance) break;
        }

        for (int i = 0; i < grid.mGridpoints.length; i++) grid.vGridpoints[i].incV(-9.81f * dt);
    }

    @Override
    public void update(float dt)
    {
        lagrangeStep1(dt);
        transferLagrangian1();
        eulerianStep(dt);
    }

    @Override
    public void render(Renderer renderer)
    {
        grid.render(renderer);
        renderBox(renderer);
        renderParticles(renderer);
    }

    public void renderBox(Renderer renderer)
    {
        renderer.drawEmptyRectangle((int) windowBounds.getxMin() - 1, (int) windowBounds.getyMin() - 1, (int) windowBounds.getWidth() + 1, (int) windowBounds.getHeight() + 1, 0xFFFFFFFF);
    }

    public void renderParticles(Renderer renderer)
    {
        for (int i = 0; i < particles.length; i++) {
            float x = windowBounds.getxMin() + particles[i].getX() * windowBounds.getWidth() / bounds.getWidth();
            float y = windowBounds.getyMin() + (bounds.getHeight() - particles[i].getY()) * windowBounds.getHeight() / bounds.getHeight();
            renderer.setPixel((int) x, (int) y, particles[i].getColor());
        }
    }
}
