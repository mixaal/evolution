package net.mikc.evolution.gfx;

public class ColorRGBA {
    int r, g, b, a;

    public static ColorRGBA fromFloats(float r, float g, float b, float a) {
        return new ColorRGBA((int)(255f*r), (int)(255f*g), (int)(255f*b), (int)(255f*a));
    }

    public ColorRGBA(int r, int g, int b, int a) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
    }

    public static  ColorRGBA WHITE = new ColorRGBA(255, 255, 255, 255);
    public static  ColorRGBA BLACK = new ColorRGBA(0, 0, 0, 255);

}
