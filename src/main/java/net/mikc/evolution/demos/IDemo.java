package net.mikc.evolution.demos;

import net.mikc.evolution.creatures.ICreature;
import net.mikc.evolution.gfx.GfxInternals;

import java.util.List;

public interface IDemo {
    void generation();
    void selectNewPopulation(int newPopulationSize);
    void drawPlayground(GfxInternals g);
    void oneStep(float age);
    List<ICreature> getPopulation();
    void update(GfxInternals gfx, boolean firstTime, boolean reset);
    void initialize(GfxInternals gfx);
}
