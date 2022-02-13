package net.mikc.evolution.demos;

import net.mikc.evolution.creatures.ICreature;
import net.mikc.evolution.creatures.Racer;
import net.mikc.evolution.genetics.GeneticEvolution;
import net.mikc.evolution.gfx.GfxInternals;
import net.mikc.evolution.gfx.MaterialBuilder;
import net.mikc.evolution.gfx.Mesh;
import net.mikc.evolution.neuralnets.SequentialBuilder;
import net.mikc.evolution.neuralnets.layers.Activation;

import java.util.Collections;
import java.util.List;


public class CarRacerDemo extends GeneticEvolution implements IDemo {
    public static final String PLAYGROUND_NAME = "IMOLA";
//    private static final SequentialBuilder accurateBrain = new SequentialBuilder()
//            .input(9, Activation.PASS_VALUE)
//            .dense(5, Activation.LEAKY_RELU)
//            .dense(5, Activation.LEAKY_RELU)
//            .dense(8, Activation.LEAKY_RELU)
//            .dense(5, Activation.LEAKY_RELU)
//            .dense(4, Activation.SOFTMAX);
    private static final SequentialBuilder accurateBrain = new SequentialBuilder()
            .input(9, Activation.PASS_VALUE)
            .dense(12, Activation.LEAKY_RELU)
            .dense(12, Activation.LEAKY_RELU)
            .dense(4, Activation.SOFTMAX);
    public static Mesh mesh = null;

    public CarRacerDemo(int populationSize) {
        super(populationSize, 1_000, () -> new Racer(accurateBrain.build())) ;
    }

    @Override
    public void drawPlayground(GfxInternals g) {
        Mesh loadedMesh = g.drawModel(PLAYGROUND_NAME, "assets/Models/IMOLA.obj", 0f, 0f, 0f, 1f, new MaterialBuilder().diffuseTexture("assets/Textures/DiffuseBaked.png"));
        if(mesh == null) {
            mesh = loadedMesh;
        }
    }

    @Override
    public List<ICreature> applySelection() {
        Collections.sort(population, (o1, o2) -> {
            Racer r1 = (Racer)o1;
            Racer r2 = (Racer)o2;
//            System.out.println("r1.dist="+r1.getDistanceTravelled());
//            System.out.println("r2.dist="+r2.getDistanceTravelled());
            if(r1.getDistanceTravelled()==r2.getDistanceTravelled()) {
                return 0;
            } else if(r1.getDistanceTravelled()>r2.getDistanceTravelled()) {
                return -1;
            } else {
                return 1;
            }
        });
        int origSize = population.size();
        return population.subList(0, origSize/10);
    }
}
