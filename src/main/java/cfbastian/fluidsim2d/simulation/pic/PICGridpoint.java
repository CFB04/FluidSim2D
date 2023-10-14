package cfbastian.fluidsim2d.simulation.pic;

import cfbastian.fluidsim2d.simulation.Gridpoint;

import java.util.Arrays;

public class PICGridpoint extends Gridpoint {
    protected float dRhoX, dRhoY, m; //momentum and mass
    protected int[] particleIndices;
    protected float[] particleWeights;

    public PICGridpoint(float x, float y, float dRhoX, float dRhoY, int color, int particleArrLen) {
        super(x, y, color);
        this.dRhoX = dRhoX;
        this.dRhoY = dRhoY;
        this.m = 0f;
        this.particleIndices = new int[particleArrLen];
        this.particleWeights = new float[particleArrLen];
        clearArrays();
    }

    public boolean appendParticle(int particleIndex, float particleWeight)
    {
        for (int i = 0; i < particleIndices.length; i++) {
            if(particleIndices[i] != -1) continue;
            particleIndices[i] = particleIndex;
            particleWeights[i] = particleWeight;
            return true;
        }
        return false;
    }

    public void clearArrays()
    {
        Arrays.fill(particleIndices, -1);
        Arrays.fill(particleWeights, Float.NaN);
    }

    public int[] getParticleIndices() {
        return particleIndices;
    }

    public float[] getParticleWeights() {
        return particleWeights;
    }

    public float getdRhoX() {
        return dRhoX;
    }

    public void setdRhoX(float dRhoX) {
        this.dRhoX = dRhoX;
    }

    public float getdRhoY() {
        return dRhoY;
    }

    public void setdRhoY(float dRhoY) {
        this.dRhoY = dRhoY;
    }

    public float getM() {
        return m;
    }

    public void setM(float m) {
        this.m = m;
    }
}
