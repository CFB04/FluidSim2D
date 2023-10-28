package cfbastian.fluidsim2d.simulation.eulerian;

import cfbastian.fluidsim2d.Renderer;
import cfbastian.fluidsim2d.simulation.Simulation;
import cfbastian.fluidsim2d.simulation.util.Bounds;
import cfbastian.fluidsim2d.simulation.util.SimMath;

/**
 * The Eulerian loop works like this:<br>
 * <br>Modify velocity (gravity, interaction)
 * <br>Pressure projection
 * <br>Advection
 */
public class EulerianSimulation extends Simulation {

    Bounds windowBounds;
    float res;

    EulerianGrid grid;

    public EulerianSimulation(Bounds bounds, Bounds windowBounds, int res) {
        super(bounds);
        this.windowBounds = windowBounds;
        this.res = res;

        grid = new EulerianGrid(bounds, windowBounds, res);
    }

    private void addGravity(float dt)
    {
        for (int i = 0; i < grid.vGridpoints.length; i++) grid.vGridpoints[i].v -= 9.81f * dt;
    }

    private void pressureProject()
    {

    }

    private void advect()
    {
        for (int i = 0; i < grid.uGridpoints.length; i++) {
            float x = grid.uGridpoints[i].x, y = grid.uGridpoints[i].y;
            int[] vs = grid.selectGridpoints(grid.selectVGridpoint(x, y));

            float dy = SimMath.lerp(
                    SimMath.lerp(grid.vGridpoints[vs[0]].v, grid.vGridpoints[vs[2]].v, 0.5f),
                    SimMath.lerp(grid.vGridpoints[vs[1]].v, grid.vGridpoints[vs[3]].v, 0.5f),
                    0.5f);

            float x1 = x - grid.uGridpoints[i].v, y1 = y - dy;

            int[] us = grid.selectGridpoints(grid.selectUGridpoint(x1, y1));

            float xW = (x1 - grid.uGridpoints[us[0]].x) * res;
            float yW = (y1 - grid.uGridpoints[us[0]].y) * res;

            grid.uGridpoints[i].v = SimMath.checkNaNLerp(
                    SimMath.checkNaNLerp(grid.uGridpoints[us[0]].v, grid.uGridpoints[us[1]].v, yW),
                    SimMath.checkNaNLerp(grid.uGridpoints[us[2]].v, grid.uGridpoints[us[3]].v, yW),
                    xW);
        }

        for (int i = 0; i < grid.vGridpoints.length; i++) {
            float x = grid.vGridpoints[i].x, y = grid.vGridpoints[i].y;
            int[] us = grid.selectGridpoints(grid.selectUGridpoint(x, y));

            float dx = SimMath.lerp(
                    SimMath.lerp(grid.uGridpoints[us[0]].v, grid.uGridpoints[us[2]].v, 0.5f),
                    SimMath.lerp(grid.uGridpoints[us[1]].v, grid.uGridpoints[us[3]].v, 0.5f),
                    0.5f);

            float x1 = x - dx, y1 = y - grid.vGridpoints[i].v;

            int[] vs = grid.selectGridpoints(grid.selectVGridpoint(x1, y1));

            float xW = (x1 - grid.vGridpoints[vs[0]].x) * res;
            float yW = (y1 - grid.vGridpoints[vs[0]].y) * res;

            grid.uGridpoints[i].v = SimMath.checkNaNLerp(
                    SimMath.checkNaNLerp(grid.vGridpoints[vs[0]].v, grid.vGridpoints[vs[1]].v, yW),
                    SimMath.checkNaNLerp(grid.vGridpoints[vs[2]].v, grid.vGridpoints[vs[3]].v, yW),
                    xW);
        }

        for (int i = 0; i < grid.mGridpoints.length; i++) {
            float dx = SimMath.lerp(grid.uGridpoints[i].v, grid.uGridpoints[i-1].v, 0.5f);
            float dy = SimMath.lerp(grid.vGridpoints[i].v, grid.uGridpoints[i-grid.cols].v, 0.5f);

            float x1 = grid.mGridpoints[i].x - dx, y1 = grid.mGridpoints[i].y - dy;
            int[] ms = grid.selectGridpoints(grid.selectMGridpoint(x1, y1));

            float xW = (x1 - grid.mGridpoints[ms[0]].x) * res;
            float yW = (y1 - grid.mGridpoints[ms[0]].y) * res;
            grid.mGridpoints[i].v = SimMath.checkNaNLerp(
                    SimMath.checkNaNLerp(grid.mGridpoints[ms[0]].v, grid.mGridpoints[ms[1]].v, yW),
                    SimMath.checkNaNLerp(grid.mGridpoints[ms[2]].v, grid.mGridpoints[ms[3]].v, yW),
                    xW);
        }
    }

    @Override
    public void update(float dt) {
        addGravity(dt);
    }

    @Override
    public void render(Renderer renderer) {
        grid.render(renderer);

        renderBox(renderer);
    }

    private void renderBox(Renderer renderer)
    {
        renderer.drawEmptyRectangle((int) windowBounds.getXMin() - 1, (int) windowBounds.getYMin() - 1, (int) windowBounds.getWidth() + 1, (int) windowBounds.getHeight() + 1, 0xFFFFFFFF);
    }
}
