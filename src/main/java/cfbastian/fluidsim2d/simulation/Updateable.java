package cfbastian.fluidsim2d.simulation;

@FunctionalInterface
public interface Updateable {
    void update(float dt);
}
