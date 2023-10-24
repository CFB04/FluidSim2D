package cfbastian.fluidsim2d.simulation.pic;

import cfbastian.fluidsim2d.Renderer;
import cfbastian.fluidsim2d.simulation.Simulation;
import cfbastian.fluidsim2d.simulation.util.Bounds;
import cfbastian.fluidsim2d.simulation.util.SimMath;

import java.util.Random;


/**
 * The PIC loop works like this:<br>
 * <br>Particle kinematics
 * <br>Particles -> Grid transfer
 * <br>Grid dynamics (gravity, pressure projection)
 * <br>Grid -> Particles transfer
 */
public class PICSimulation extends Simulation {

    // TODO fix boundary stability issues
    // TODO fix particle separator stability issues

    private final PICParticle[] particles;
    PICGrid grid;

    private final Bounds windowBounds;

    private final float res;

    public PICSimulation(Bounds bounds, Bounds windowBounds, Bounds particleBounds, int res)
    {
        super(bounds);
        this.windowBounds = windowBounds;

        this.res = res;

        int numParticles = (int) (particleBounds.getWidth() * particleBounds.getHeight() * res * res * 4);
        int particlesPerRow = (int) (particleBounds.getWidth() * res * 2);
        int particlesPerCol = (int) (particleBounds.getHeight() * res * 2);

        System.out.println("\nNum Particles: " + numParticles);

        this.particles = new PICParticle[numParticles];
        this.grid = new PICGrid(bounds, windowBounds, res);

        float jitter = 4f/res;
        Random r = new Random();

        for (int i = 0; i < numParticles; i++) {
            this.particles[i] = new PICParticle(
                    particleBounds.getXMin() + jitter * r.nextFloat() * particleBounds.getWidth() / (particlesPerRow) + particleBounds.getWidth() / (particlesPerRow) * (i % particlesPerRow),
                    particleBounds.getYMin() + jitter * r.nextFloat() * particleBounds.getHeight() / (particlesPerCol) + particleBounds.getHeight() / (particlesPerCol) * (i / particlesPerRow),
                    0f, 0f, 0xFF22FFFF, 0.25f);
            this.particles[i].x = Math.max(bounds.getXMin(), Math.min(bounds.getXMax(), particles[i].x));
            this.particles[i].y = Math.max(bounds.getYMin(), Math.min(bounds.getYMax(), particles[i].y));
        }
//        for (int i = 0; i < numParticles; i++)
//            this.particles[i] = new PICParticle(
//                    particleBounds.getXMin() + r.nextFloat() * particleBounds.getWidth(),
//                    particleBounds.getYMin() + r.nextFloat() * particleBounds.getHeight(),
//                    0f, 0f, 0xFF22FFFF, 0.25f);
        init();
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
    private void lagrangeStep(float dt)
    {
        for (int i = 0; i < particles.length; i++) {
            int[] gM = grid.selectGridpoints(grid.selectMGridpoint(particles[i]));
            int[] gU = grid.selectGridpoints(grid.selectUGridpoint(particles[i]));
            int[] gV = grid.selectGridpoints(grid.selectVGridpoint(particles[i]));

            // Receive velocities
            float x1 = (particles[i].x - grid.mGridpoints[gM[0]].x) * grid.res;
            float y1 = (particles[i].y - grid.mGridpoints[gM[0]].y) * grid.res;

            float vX = SimMath.checkNaNLerp(
                    SimMath.checkNaNLerp(grid.uGridpoints[gU[0]].v, grid.uGridpoints[gU[1]].v, y1),
                    SimMath.checkNaNLerp(grid.uGridpoints[gU[2]].v, grid.uGridpoints[gU[3]].v, y1),
                    (x1 + 0.5f) % 1f);
            float vY = SimMath.checkNaNLerp(
                    SimMath.checkNaNLerp(grid.vGridpoints[gV[0]].v, grid.vGridpoints[gV[2]].v, x1),
                    SimMath.checkNaNLerp(grid.vGridpoints[gV[1]].v, grid.vGridpoints[gV[3]].v, x1),
                    (y1 + 0.5f) % 1f);

            particles[i].dx = vX;
            particles[i].dy = vY;
        }

        // Kinematics
        for (int i = 0; i < particles.length; i++) particles[i].update(bounds, dt);
    }

    private float getPushApart(float d)
    {
        if(d > 1f / res) return 0f;
        float v = (1f - res * d);
        return v*v;
    }

    /**
     * This handles the particle to grid transfer
     */
    private void transferLagrangian()
    {
        grid.reset();

        for (int i = 0; i < particles.length; i++) {
            int[] gM = grid.selectGridpoints(grid.selectMGridpoint(particles[i]));
            int[] gU = grid.selectGridpoints(grid.selectUGridpoint(particles[i]));
            int[] gV = grid.selectGridpoints(grid.selectVGridpoint(particles[i]));

            //Impart mass and momentum
            float x1 = particles[i].x - grid.mGridpoints[gM[0]].x;
            float y1 = particles[i].y - grid.mGridpoints[gM[0]].y;
            float x2 = x1 * grid.res, y2 = y1 * grid.res;

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
                grid.mGridpoints[gM[j]].v += particles[i].m * w[j];
                grid.mGridpoints[gM[j]].w += w[j];
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
                grid.uGridpoints[gU[j]].v += particles[i].dx * w[j];
                grid.uGridpoints[gU[j]].w += w[j];
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
                grid.vGridpoints[gV[j]].v += particles[i].dy * w[j];
                grid.vGridpoints[gV[j]].w += w[j];
            }

            grid.cellTypes[grid.selectCell(particles[i])] = PICGrid.CellType.WATER;
        }

        // Reset wall cells
        for (int x = 0; x < grid.cols; x++) {
//            grid.uGridpoints[x].v = grid.uGridpoints[x + grid.cols].v;
//            grid.uGridpoints[x + grid.cols * (grid.rows - 1)].v = grid.uGridpoints[x + grid.cols * (grid.rows - 2)].v;
//
//            grid.uGridpoints[x].w = grid.uGridpoints[x + grid.cols].w;
//            grid.uGridpoints[x + grid.cols * (grid.rows - 1)].w = grid.uGridpoints[x + grid.cols * (grid.rows - 2)].w;

            grid.vGridpoints[x].v = Math.max(0f, grid.vGridpoints[x].v);
            grid.vGridpoints[x + grid.cols * (grid.rows - 2)].v = Math.min(0f, grid.vGridpoints[x + grid.cols * (grid.rows - 2)].v);
        }
        for (int y = 0; y < grid.rows; y++) {
//            grid.vGridpoints[y * grid.cols].v = grid.vGridpoints[y * grid.cols + 1].v;
//            grid.vGridpoints[y * grid.cols + grid.cols - 1].v = grid.vGridpoints[y * grid.cols + grid.cols - 2].v;
//
//            grid.vGridpoints[y * grid.cols].w = grid.vGridpoints[y * grid.cols + 1].w;
//            grid.vGridpoints[y * grid.cols + grid.cols - 1].w = grid.vGridpoints[y * grid.cols + grid.cols - 2].w;

            grid.uGridpoints[y * grid.cols].v = Math.max(0f, grid.uGridpoints[y * grid.cols].v);
            grid.uGridpoints[y * grid.cols + grid.cols - 2].v = Math.min(0f, grid.uGridpoints[y * grid.cols + grid.cols - 2].v);
        }

        for (int i = 0; i < grid.mGridpoints.length; i++) {
            // Remove velocities from air cells
            if(grid.cellTypes[i] == PICGrid.CellType.AIR)
            {
                int[] c = grid.selectCells(i);
                if(grid.cellTypes[c[0]] == PICGrid.CellType.AIR) grid.uGridpoints[i].v = Float.NaN;
                if(grid.cellTypes[c[1]] == PICGrid.CellType.AIR) grid.vGridpoints[i].v = Float.NaN;
                if(grid.cellTypes[c[2]] == PICGrid.CellType.AIR) grid.uGridpoints[i - 1].v = Float.NaN;
                if(grid.cellTypes[c[3]] == PICGrid.CellType.AIR) grid.vGridpoints[i - grid.cols].v = Float.NaN;
            }

            // Adjust by weights
            float wU = grid.uGridpoints[i].w, wV = grid.vGridpoints[i].w;
            wU = wU == 0f? 1f : 1f / wU;
            wV = wV == 0f? 1f : 1f / wV;
            grid.uGridpoints[i].v = grid.uGridpoints[i].v * wU;
            grid.vGridpoints[i].v = grid.vGridpoints[i].v * wV;
        }
    }

    /**
     * This handles the grid dynamics
     */
    private void eulerStep(float dt)
    {
        for (int i = 0; i < grid.mGridpoints.length; i++) grid.vGridpoints[i].v += -9.81f * dt;

        int maxSteps = 100;
        float divergence, s;
        float oRFactor = 1f; // Over-relaxation factor

        float kStiffness = 1f, waterDensity = 1f;

        for (int i = 0; i < maxSteps; i++) {
            for (int j = 0; j < grid.cellTypes.length; j++) {
                int x = j % grid.cols, y = j / grid.cols;
                if(x == 0 || y == 0 || x == grid.cols - 1 || y == grid.rows - 1) continue;
                if(grid.cellTypes[j] == PICGrid.CellType.AIR) continue;

                int[] g = grid.selectCellVelocities(j);
                int[] c = grid.selectCells(j);

                float u1 = grid.uGridpoints[g[0]].v;
                float v1 = grid.vGridpoints[g[1]].v;
                float u2 = grid.uGridpoints[g[2]].v;
                float v2 = grid.vGridpoints[g[3]].v;

                float s0 = grid.cellTypes[c[0]].s;
                float s1 = grid.cellTypes[c[1]].s;
                float s2 = grid.cellTypes[c[2]].s;
                float s3 = grid.cellTypes[c[3]].s;

                s = s0 + s1 + s2 + s3;
                if(s == 0) continue;
                s = 1f / s;

                divergence = u1 + v1 - u2 - v2;

                float density = grid.mGridpoints[j].v;
                if(density > 0f)
                {
                    float compression = density - waterDensity;
                    if(compression > 0f) divergence -= kStiffness * compression;
                }

                grid.uGridpoints[g[0]].v = u1 - divergence * s0 * s * oRFactor;
                grid.vGridpoints[g[1]].v = v1 - divergence * s1 * s * oRFactor;
                grid.uGridpoints[g[2]].v = u2 + divergence * s2 * s * oRFactor;
                grid.vGridpoints[g[3]].v = v2 + divergence * s3 * s * oRFactor;
            }
        }
    }

    @Override
    public void update(float dt)
    {
        lagrangeStep(dt);
        transferLagrangian();
        eulerStep(dt);
    }

    @Override
    public void render(Renderer renderer)
    {
//        grid.render(renderer);
        renderBox(renderer);
        renderParticles(renderer);
    }

    private void renderBox(Renderer renderer)
    {
        renderer.drawEmptyRectangle((int) windowBounds.getXMin() - 1, (int) windowBounds.getYMin() - 1, (int) windowBounds.getWidth() + 1, (int) windowBounds.getHeight() + 1, 0xFFFFFFFF);
    }

    private void renderParticles(Renderer renderer)
    {
        for (int i = 0; i < particles.length; i++) {
            float x = windowBounds.getXMin() + particles[i].x * windowBounds.getWidth() / bounds.getWidth();
            float y = windowBounds.getYMin() + particles[i].y * windowBounds.getHeight() / bounds.getHeight();
            renderer.drawCircle((int) x, (int) y, 3, particles[i].getColor());
        }
    }
}
