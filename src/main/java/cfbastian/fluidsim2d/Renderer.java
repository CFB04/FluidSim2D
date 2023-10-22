package cfbastian.fluidsim2d;

import cfbastian.fluidsim2d.simulation.Renderable;
import cfbastian.fluidsim2d.simulation.util.SimMath;

import java.util.Arrays;

public class Renderer {

    static int[] pixels = new int[Application.width * Application.height];

    public int[] render(Renderable renderable) {
        Arrays.fill(pixels, 0xFF000000);
        renderable.render(this);
        return pixels;
    }

    public void init() {

    }

    public void setPixel(int i, int color)
    {
        pixels[i] = color;
    }

    public void setPixel(int x, int y, int color)
    {
        y = Application.height - y;
        if(x >= 0 && x < Application.width && y >= 0 && y < Application.height) pixels[x + Application.width * y] = color;
    }

    public void drawEmptyRectangle(int x, int y, int w, int h, int color)
    {
        for (int i = x; i < x + w; i++) {
            setPixel(i, y, color);
            setPixel(i, y + h, color);
        }
        for (int i = y; i < y + h; i++) {
            setPixel(x, i, color);
            setPixel(x + w, i, color);
        }
    }

    public void drawRectangle(int x, int y, int w, int h, int color)
    {
        for (int x1 = x; x1 < x + w; x1++) {
            for (int y1 = y; y1 < y + h; y1++) {
                setPixel(x1, y1, color);
            }
        }
    }

    public void drawDottedRectangle(int x, int y, int w, int h, int color)
    {
        for (int i = x; i < x + w; i+=2) {
            setPixel(i, y, color);
            setPixel(i, y + h, color);
        }
        for (int i = y; i < y + h; i+=2) {
            setPixel(x, i, color);
            setPixel(x + w, i, color);
        }
    }

    public void drawCircle(int x, int y, int r, int color)
    {
        int rSqrd = r*r;

        for (int x1 = -r; x1 <= r; x1++) {
            for (int y1 = 0; y1 < (int) (Math.sqrt(rSqrd - x1 * x1) + 0.5); y1++) {
                setPixel(x + x1, y + y1, color);
                setPixel(x + x1, y - y1, color);
                setPixel(x - x1, y + y1, color);
                setPixel(x - x1, y - y1, color);
            }
        }
    }

    public void drawEmptyCircle(int x, int y, int r, int color)
    {
        int rSqrd = r*r;
        for (int x1 = -r; x1 <= r; x1++) {
            int y1 = (int) (Math.sqrt(rSqrd - x1 * x1) + 0.5);
            setPixel(x + x1, y + y1, color);
            setPixel(x + x1, y - y1, color);
        }

        for (int y1 = -r; y1 <= r; y1++) {
            int x1 = (int) (Math.sqrt(rSqrd - y1 * y1) + 0.5);
            setPixel(x + x1, y + y1, color);
            setPixel(x - x1, y + y1, color);
        }
    }

    public void drawTrapezoid(int x, int y, int w, int h1, int h2, int color)
    {
        for (int x1 = x; x1 < x + w; x1++) {
            int h = (int) SimMath.lerp(h1, h2, (x1 - x) / (float) w);
            for (int y1 = y; y1 < y + h; y1++) {
                setPixel(x1, y1, color);
            }
        }
    }
}
