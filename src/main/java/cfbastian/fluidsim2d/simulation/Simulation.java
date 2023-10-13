package cfbastian.fluidsim2d.simulation;

import cfbastian.fluidsim2d.simulation.util.Bounds;

public abstract class Simulation implements Updateable, Renderable{
    protected Bounds bounds;

    public Simulation(Bounds bounds) {
        this.bounds = bounds;
    }
}
