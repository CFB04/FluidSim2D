package cfbastian.fluidsim2d.simulation;

import cfbastian.fluidsim2d.Renderer;

public interface Renderable extends Updateable{
    void render(Renderer renderer);
}
