package cfbastian.fluidsim2d.simulation.util;

public final class SimMath {

    public static float lerp(float v1, float v2, float w)
    {
        return v1 * (1f - w) + v2 * w;
    }

    public static float checkNaNLerp(float v1, float v2, float w)
    {
//        if(Float.isNaN(v1) && Float.isNaN(v2)) return 0f;
        if(Float.isNaN(v1)) return v2;
        if(Float.isNaN(v2)) return v1;
        return v1 * (1f - w) + v2 * w;
    }



}
