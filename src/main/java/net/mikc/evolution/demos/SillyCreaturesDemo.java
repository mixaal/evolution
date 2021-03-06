package net.mikc.evolution.demos;

import net.mikc.evolution.creatures.ICreature;
import net.mikc.evolution.creatures.SillyCreature;
import net.mikc.evolution.genetics.GeneticEvolution;
import net.mikc.evolution.gfx.GfxInternals;
import net.mikc.evolution.neuralnets.SequentialBuilder;
import net.mikc.evolution.neuralnets.layers.Activation;
import net.mikc.evolution.utils.MathUtils;

import java.util.ArrayList;
import java.util.List;


public class SillyCreaturesDemo extends GeneticEvolution implements IDemo {
    public enum SelectionCriteria {
        KILL_BY_TIME,
        KILL_ON_RIGHT,
        KILL_IN_CIRCLE_AROUND_CENTER,
        KILL_OUT_OF_CIRCLE_AROUND_CENTER,
        SURVIVE_IN_CORNERS
    }

    private static final SequentialBuilder accurateBrain = new SequentialBuilder()
            .input(3, Activation.PASS_VALUE)
            .dense(4, Activation.LEAKY_RELU)
            .dense(4, Activation.LEAKY_RELU)
            .dense(4, Activation.SOFTMAX);


    private static final SequentialBuilder largeBrain = new SequentialBuilder()
            .input(3, Activation.PASS_VALUE)
            .dense(16, Activation.LEAKY_RELU)
            .dense(8, Activation.LEAKY_RELU)
            .dense(16, Activation.LEAKY_RELU)
            .dense(4, Activation.SOFTMAX);

    private final SelectionCriteria selectionCriteria;
    private final int generations;
    private final int viewPopulationSize;

    public SillyCreaturesDemo(int generations, int populationSize, int viewPopulationSize, SelectionCriteria criteria) {
        super(populationSize, 300, () -> new SillyCreature(accurateBrain.build(), MathUtils.rnd(), MathUtils.rnd()));
        this.generations = generations;
        this.selectionCriteria = criteria;
        this.viewPopulationSize = viewPopulationSize;
    }

    @Override
    public List<ICreature> applySelection() {
        int killed = 0;
        float dx = 0, dy = 0, d=0;
        for (ICreature creature : population) {
            SillyCreature sillyCreature = (SillyCreature) creature;
            switch (selectionCriteria) {
                case KILL_ON_RIGHT:
                    if (sillyCreature.getX() > 0.0f) {
                        sillyCreature.kill();
                        killed++;
                    }
                    break;
                case KILL_IN_CIRCLE_AROUND_CENTER:
                    if(sillyCreature.getX()* sillyCreature.getX() + sillyCreature.getY()*sillyCreature.getY() < 0.25f) {
                        sillyCreature.kill();
                        killed++;
                    }
                    break;
                case KILL_OUT_OF_CIRCLE_AROUND_CENTER:
                    dx = sillyCreature.getX();
                    dy = sillyCreature.getY();
                    d = dx*dx + dy*dy;
//                    System.out.println("d="+d);
                    if(d >= 0.25f) {
                        sillyCreature.kill();
                        killed++;
                    }
                    break;
                case SURVIVE_IN_CORNERS:
                    break;
            }
        }
        System.out.println("Killed: " + killed);
        List<ICreature> alive = new ArrayList<>();
        for(ICreature creature: population){
            if(!creature.isDead()) {
                alive.add(creature);
            }
        }
        return alive;
    }

    @Override
    public void drawPlayground(GfxInternals gfx) {
        gfx.cube(0, -1, 0, 24, 0.1f, 24, 255, 255, 255, 255);
        gfx.cylinder(0, -0.2f, 0, 12, 0.1f, 255, 0, 0, 255);
    }

    @Override
    public void update(GfxInternals gfx, boolean firstTime, boolean reset) {
        long fps = gfx.getFps();
        if (fps == 0) selectNewPopulation(viewPopulationSize);
        if (fps > 3000) {
            gfx.clear();
            drawPlayground(gfx);
            fps = 0;
            selectNewPopulation(viewPopulationSize);
        }
        fps++;

        oneStep(0.5f);
        int i = 0;
        for (ICreature creature : getPopulation()) {
            i++;
            creature.draw(gfx, (i % 100) == 0);
        }
    }

    @Override
    public void initialize(GfxInternals gfx) {
        long start = System.currentTimeMillis();
        for (int i = 0; i < generations; i++) {
            generation();
            System.out.println("Generation #" + i);
        }
        long end = System.currentTimeMillis();
        long ms = end - start;
        System.out.println("Total time: " + ms / 1000);
    }
}
