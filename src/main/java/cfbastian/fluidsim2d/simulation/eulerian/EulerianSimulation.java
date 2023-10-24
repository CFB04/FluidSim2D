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

            float x1 = (x - grid.vGridpoints[vs[0]].x) * res;
            float y1 = (y - grid.vGridpoints[vs[0]].y) * res;

            float dy = SimMath.checkNaNLerp(
                    SimMath.checkNaNLerp(grid.vGridpoints[vs[0]].v, grid.vGridpoints[vs[2]].v, x1),
                    SimMath.checkNaNLerp(grid.vGridpoints[vs[1]].v, grid.vGridpoints[vs[3]].v, x1),
                    (y1 + 0.5f) % 1f);
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
