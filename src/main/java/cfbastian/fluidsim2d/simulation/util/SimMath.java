package cfbastian.fluidsim2d.simulation.util;

public final class SimMath {

    public static float lerp(float v1, float v2, float w)
    {
        return v1 * (1f - w) + v2 * w;
    }


}
