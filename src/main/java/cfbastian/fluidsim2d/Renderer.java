package cfbastian.fluidsim2d;

import cfbastian.fluidsim2d.Application;

public class Renderer {

    static int[] pixels = new int[Application.width * Application.height];


    public int[] render() {
        drawCircle(1280/2, 720/2, 50, 0xFFFFFFFF);

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
        pixels[x + Application.width * y] = color;
    }

    public void drawCircle(int x, int y, int r, int color)
    {
        int rSqrd = r*r;

        for (int x1 = -r; x1 <= r; x1++) {
            for (int y1 = 0; y1 < (int) (Math.sqrt(rSqrd - x1*x1) + 0.5); y1++) {
                setPixel(x + x1, y + y1, color);
                setPixel(x + x1, y - y1, color);
                setPixel(x - x1, y + y1, color);
                setPixel(x - x1, y - y1, color);
            }
        }
    }
}
