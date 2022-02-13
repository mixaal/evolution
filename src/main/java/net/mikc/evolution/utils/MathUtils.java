package net.mikc.evolution.utils;

import java.util.Random;

public class MathUtils {
    static final Random random = new Random();


    public static float rnd(float rangeMin, float rangeMax)
    {
        Random r = random;
        float createdRanNum = rangeMin + (rangeMax - rangeMin) * r.nextFloat();
        return createdRanNum;
    }

    public static float rnd() {
        return rnd(-1.0f, 1.0f);
    }
}
