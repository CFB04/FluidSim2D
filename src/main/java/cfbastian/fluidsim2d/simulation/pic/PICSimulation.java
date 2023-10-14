package cfbastian.fluidsim2d.simulation.pic;

import cfbastian.fluidsim2d.Application;
import cfbastian.fluidsim2d.Renderer;
import cfbastian.fluidsim2d.simulation.Simulation;
import cfbastian.fluidsim2d.simulation.util.Bounds;

public class PICSimulation extends Simulation {

    private PICGridpoint[] gridpoints;
    private PICParticle[] particles;

    private float widthScalar, heightScalar;

    private int rows, cols;

    private int gridpointParticleArrLen = 20;

    public PICSimulation(Bounds bounds, Bounds particleBounds, int numParticles, int particlesPerRow, int rows, int cols) {
        super(bounds);
        this.rows = rows;
        this.cols = cols;
        gridpoints = new PICGridpoint[rows * cols];
        particles = new PICParticle[numParticles];

        widthScalar = Application.width/bounds.getWidth();
        heightScalar = Application.height/bounds.getHeight();

        for (int i = 0; i < gridpoints.length; i++) {
            int x = i % (cols), y = i / (cols);
            gridpoints[i] = new PICGridpoint(
                    bounds.getxMin() + x * (bounds.getWidth()) / (float) (cols - 1),
                    bounds.getyMin() + y * (bounds.getHeight()) / (float) (rows - 1),
                    0f, 0f, 0xFFAAAAAA, gridpointParticleArrLen);
        }

        int particlesPerCol = numParticles/particlesPerRow;

        for (int i = 0; i < numParticles; i++)
            this.particles[i] = new PICParticle(
                    particleBounds.getxMin() + particleBounds.getWidth()/(particlesPerRow - 1) * (i % particlesPerRow),
                    particleBounds.getyMin() + particleBounds.getHeight()/(particlesPerCol - 1) * (i / particlesPerRow),
                    0f, 0f, 0xFF22FFFF, 0.05f, 1f);
    }

    public synchronized void impartMAndRho(int particleIndex)
    {
        PICParticle p = particles[particleIndex];
        System.out.println(particleIndex);
        float x = p.getX(), y = p.getY(), dx = p.getDx(), dy = p.getDy(), m = p.getM();
        int[] g = new int[4];
        g[0] = selectGridpoint(p);
        g[1] = g[0] + cols;
        g[2] = g[0] + 1;
        g[3] = g[0] + cols + 1;

        float w[] = new float[4];
        float x1 = x - gridpoints[g[0]].getX(), y1 = y - gridpoints[g[0]].getY();
        w[0] = (float) Math.sqrt(x1*x1 + y1*y1);
        x1 = x - gridpoints[g[1]].getX(); y1 = y - gridpoints[g[1]].getY();
        w[1] = (float) Math.sqrt(x1*x1 + y1*y1);
        x1 = x - gridpoints[g[2]].getX(); y1 = y - gridpoints[g[2]].getY();
        w[2] = (float) Math.sqrt(x1*x1 + y1*y1);
        x1 = x - gridpoints[g[3]].getX(); y1 = y - gridpoints[g[3]].getY();
        w[3] = (float) Math.sqrt(x1*x1 + y1*y1);

        w[0] = 1f - w[0];
        w[1] = 1f - w[1];
        w[2] = 1f - w[2];
        w[3] = 1f - w[3];
        w[0] = Math.max(w[0], 0f);
        w[1] = Math.max(w[1], 0f);
        w[2] = Math.max(w[2], 0f);
        w[3] = Math.max(w[3], 0f);
        float sum = w[0] + w[1] + w[2] + w[3];
        w[0] /= sum;
        w[1] /= sum;
        w[2] /= sum;
        w[3] /= sum;

        for (int i = 0; i < 4; i++) {
            gridpoints[g[i]].setM(m * w[i]);
            gridpoints[g[i]].setdRhoX(m * dx * w[i]);
            gridpoints[g[i]].setdRhoY(m * dy * w[i]);
            if(!gridpoints[g[i]].appendParticle(particleIndex, w[i])) System.out.println("\u001B[31m" + "Grid-point Overflow on grid-point: " + g[i] + "\u001B[0m");
            System.out.println("Grid-point: " + g[i]);
        }
    }

    public int selectGridpoint(float x, float y)
    {
        int xi = (int) ((x - bounds.getxMin()) * (float) (cols - 1) / bounds.getWidth());
        int yi = (int) ((y - bounds.getyMin()) * (float) (rows - 1) / bounds.getHeight());
        xi -= x == bounds.getxMax()? 1 : 0;
        yi -= y == bounds.getyMax()? 1 : 0;
        return xi + yi * cols;
    }

    public int selectGridpoint(PICParticle p)
    {
        return selectGridpoint(p.getX(), p.getY());
    }

    PICParticle p = new PICParticle(4.53f, 4.4f, 0f,0f ,0xFFFF0000, 0.05f, 1f);

    @Override
    public void render(Renderer renderer) {
        gridpoints[0].setColor(0xFFFFFF00);

        for (int i = 0; i < gridpoints.length; i++) {
            renderer.drawEmptyCircle((int) (gridpoints[i].getX() * widthScalar), Application.height - (int) (gridpoints[i].getY() * heightScalar), 2, gridpoints[i].getColor());
        }

//        gridpoints[selectGridpoint(p)].setColor(0xFFFF0000);
//        float x = p.getX() * Application.width / bounds.getWidth();
//        float y = (bounds.getHeight() - p.getY()) * Application.height / bounds.getHeight();
//        float r = p.getR() * Application.width / bounds.getWidth();
//        renderer.drawCircle((int) x, (int) y, (int) r, p.getColor());

        for (int i = 0; i < particles.length; i++) {
            float x = particles[i].getX() * Application.width / bounds.getWidth();
            float y = (bounds.getHeight() - particles[i].getY()) * Application.height / bounds.getHeight();
            float r = particles[i].getR() * Application.width / bounds.getWidth();
            renderer.drawCircle((int) x, (int) y, (int) r, particles[i].getColor());
        }
    }

    @Override
    public void update(float dt) {
        for (int i = 0; i < particles.length; i++) {
            // Kinematics
            particles[i].update(bounds);
            // Transfer to grid space
            impartMAndRho(i);
        }
        for (int i = 0; i < gridpoints.length; i++) {
//            //Dynamics TODO ADD FLUID DYNAMICS
            gridpoints[i].setdRhoX(0f);
            gridpoints[i].setdRhoY(-0.01f*dt);
            // Transfer back to particle space
            int[] pIndices = gridpoints[i].getParticleIndices();
            float[] pWeights = gridpoints[i].getParticleWeights();
            for (int j = 0; j < gridpointParticleArrLen; j++) {
                if(pIndices[j] == -1) break;
                float invM = 1f / particles[pIndices[j]].getM();
                particles[pIndices[j]].incDx(gridpoints[i].getdRhoX() * invM * pWeights[j]);
                particles[pIndices[j]].incDy(gridpoints[i].getdRhoY() * invM * pWeights[j]);
            }
            gridpoints[i].clearArrays();
        }
    }
}
