package cfbastian.fluidsim2d.simulation.pic;

import cfbastian.fluidsim2d.Application;
import cfbastian.fluidsim2d.Renderer;
import cfbastian.fluidsim2d.simulation.Simulation;
import cfbastian.fluidsim2d.simulation.util.Bounds;
import cfbastian.fluidsim2d.simulation.util.SimMath;

public class PICSimulation extends Simulation {

    private final PICGridpoint[] gridpoints;
    private final PICParticle[] particles;

    private final float widthScalar, heightScalar;

    private final int rows, cols;

    private final float gridStepWidth, gridStepHeight;

    private final Bounds windowBounds;

    public PICSimulation(Bounds bounds, Bounds windowBounds, Bounds particleBounds, int numParticles, int particlesPerRow, int rows, int cols) {
        super(bounds);
        this.windowBounds = windowBounds;
        this.rows = rows;
        this.cols = cols;
        this.gridStepWidth = (bounds.getWidth()) / (float) (cols - 1);
        this.gridStepHeight = (bounds.getHeight()) / (float) (rows - 1);
        this.gridpoints = new PICGridpoint[rows * cols];
        this.particles = new PICParticle[numParticles];

        widthScalar = windowBounds.getWidth()/bounds.getWidth();
        heightScalar = windowBounds.getHeight()/bounds.getHeight();

        for (int i = 0; i < gridpoints.length; i++) {
            int x = i % (cols), y = i / (cols);
            gridpoints[i] = new PICGridpoint(
                    bounds.getxMin() + x * gridStepWidth,
                    bounds.getyMin() + y * gridStepHeight,
                    0f, 0f, 0xFFAAAAAA);
        }

        int particlesPerCol = numParticles/particlesPerRow;

        for (int i = 0; i < numParticles; i++)
            this.particles[i] = new PICParticle(
                    particleBounds.getxMin() + particleBounds.getWidth()/(particlesPerRow - 1) * (i % particlesPerRow),
                    particleBounds.getyMin() + particleBounds.getHeight()/(particlesPerCol - 1) * (i / particlesPerRow),
                    0f, 0f, 0xFF22FFFF, 0.05f, 1f);
    }

    @Override
    public void init()
    {
        for (int i = 0; i < gridpoints.length; i++) {
//            gridpoints[i].setVX(0f);
//            gridpoints[i].setVY(1f);
//            gridpoints[i].setVX(-(gridpoints[i].getY() - bounds.getCenterY()));
//            gridpoints[i].setVY((gridpoints[i].getX() - bounds.getCenterX()));
            gridpoints[i].setVX(gridpoints[i].getX() - bounds.getCenterX());
            gridpoints[i].setVY(gridpoints[i].getY() - bounds.getCenterY());
            System.out.println(gridpoints[i].getY() - bounds.getCenterY());
        }
    }

    private synchronized void lagrangeStep(float dt)
    {
        for (int i = 0; i < particles.length; i++) {
            PICParticle p = particles[i];
            float x = p.getX(), y = p.getY(), dx = p.getDx(), dy = p.getDy(), m = p.getM();
            int[] g = new int[4];
            g[0] = selectGridpoint(p);
            g[1] = g[0] + cols;
            g[2] = g[0] + 1;
            g[3] = g[0] + cols + 1;

            //Receive velocities
            float x1 = x - gridpoints[g[0]].getX();
            float y1 = y - gridpoints[g[0]].getY();
            float x2 = x1 / gridStepWidth, y2 = y1 / gridStepHeight;

            float vX = SimMath.lerp(
                    SimMath.lerp(gridpoints[g[0]].getVX(), gridpoints[g[2]].getVX(), x2),
                    SimMath.lerp(gridpoints[g[1]].getVX(), gridpoints[g[3]].getVX(), x2),
                    y2);
            float vY = SimMath.lerp(
                    SimMath.lerp(gridpoints[g[0]].getVY(), gridpoints[g[2]].getVY(), x2),
                    SimMath.lerp(gridpoints[g[1]].getVY(), gridpoints[g[3]].getVY(), x2),
                    y2);
            particles[i].setDx(vX);
            particles[i].setDy(vY);

            //Kinematics
            particles[i].update(bounds, dt);
        }
    }

    public void transferLagrangian()
    {
        for (int i = 0; i < gridpoints.length; i++) gridpoints[i].reset();

        for (int i = 0; i < particles.length; i++) {
            PICParticle p = particles[i];
            float x = p.getX(), y = p.getY(), dx = p.getDx(), dy = p.getDy(), m = p.getM();

            int[] g = new int[4];
            g[0] = selectGridpoint(p);
            g[1] = g[0] + cols;
            g[2] = g[0] + 1;
            g[3] = g[0] + cols + 1;

            //Impart mass and momentum
            float w[] = new float[4];
            float x1 = x - gridpoints[g[0]].getX();
            float y1 = y - gridpoints[g[0]].getY();
            float x2 = x1 / gridStepWidth, y2 = y1 / gridStepHeight;

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
                gridpoints[g[j]].incM(m * w[j]);// TODO FIX
                gridpoints[g[j]].incVX(dx * w[j]);
                gridpoints[g[j]].incVY(dy * w[j]);
            }
        }

        for (int i = 0; i < gridpoints.length; i++) {
            float m = gridpoints[i].getM();
            if(m != 0) {
                gridpoints[i].setVX(gridpoints[i].getVX() / m);
                gridpoints[i].setVY(gridpoints[i].getVY() / m);
            }
        }
    }

    public int selectGridpoint(float x, float y)
    {
        int xi = (int) ((x - bounds.getxMin()) / gridStepWidth);
        int yi = (int) ((y - bounds.getyMin()) / gridStepHeight);
        xi -= x >= bounds.getxMax()? 1 : 0;
        yi -= y >= bounds.getyMax()? 1 : 0;
        return xi + yi * cols;
    }

    public int selectGridpoint(PICParticle p)
    {
        int ret = selectGridpoint(p.getX(), p.getY());
        return ret;
    }

    @Override
    public void update(float dt) {
        lagrangeStep(dt);
        transferLagrangian();
        for (int i = 0; i < gridpoints.length; i++) {
            //Dynamics TODO ADD FLUID DYNAMICS
//            gridpoints[i].incVY(-1f * dt);
//            gridpoints[i].setVX(-(gridpoints[i].getY() - bounds.getCenterY()) * 1f * dt);
//            gridpoints[i].setVY((gridpoints[i].getX() - bounds.getCenterX()) * 1f * dt);
        }
    }

    @Override
    public void render(Renderer renderer) {
        renderer.drawRectangle((int) windowBounds.getxMin() - 1, (int) windowBounds.getyMin() - 1, (int) windowBounds.getWidth() + 1, (int) windowBounds.getHeight() + 1, 0xFFFFFFFF);

        for (int i = 0; i < gridpoints.length; i++) {
            renderer.drawEmptyCircle((int) (gridpoints[i].getX() * widthScalar + windowBounds.getxMin()), (int) (windowBounds.getHeight() - (gridpoints[i].getY() * heightScalar) + windowBounds.getyMin()), 2, gridpoints[i].getColor());
        }

        for (int i = 0; i < particles.length; i++) {
            float x = windowBounds.getxMin() + particles[i].getX() * windowBounds.getWidth() / bounds.getWidth();
            float y = windowBounds.getyMin() + (bounds.getHeight() - particles[i].getY()) * windowBounds.getHeight() / bounds.getHeight();
            float r = particles[i].getR() * Application.width / bounds.getWidth();
            renderer.drawCircle((int) x, (int) y, (int) r, particles[i].getColor());
        }
    }
}
