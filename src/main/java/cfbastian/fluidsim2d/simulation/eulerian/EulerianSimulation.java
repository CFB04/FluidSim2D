package cfbastian.fluidsim2d.simulation.eulerian;

import cfbastian.fluidsim2d.Renderer;
import cfbastian.fluidsim2d.simulation.Simulation;
import cfbastian.fluidsim2d.simulation.util.Bounds;

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
