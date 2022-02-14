package net.mikc.evolution.demos;

import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import net.mikc.evolution.creatures.ICreature;
import net.mikc.evolution.creatures.Racer;
import net.mikc.evolution.genetics.GeneticEvolution;
import net.mikc.evolution.gfx.GfxInternals;
import net.mikc.evolution.gfx.MaterialBuilder;
import net.mikc.evolution.gfx.Mesh;
import net.mikc.evolution.neuralnets.NeuralNetwork;
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
    public static final SequentialBuilder accurateBrain = new SequentialBuilder()
            .input(9, Activation.PASS_VALUE)
            .dense(12, Activation.LEAKY_RELU)
            .dense(12, Activation.LEAKY_RELU)
            .dense(4, Activation.SOFTMAX);
    public static Mesh mesh = null;
    private boolean manualMode;

    private Racer car;
    private final int generations;
    private final int viewPopulationSize;

    public CarRacerDemo(int generations, int populationSize, int viewPopulationSize, boolean manualMode) {
        super(populationSize, 1_000, () -> new Racer(accurateBrain.build()));
        this.manualMode = manualMode;
        this.generations = generations;
        this.viewPopulationSize = viewPopulationSize;
        NeuralNetwork savedModel = null;
        try {
            savedModel = NeuralNetwork.load("best-0.nn");
        }
        catch (Throwable t) {
            System.out.println("Model not found.");
        }
        this.car = manualMode ? new Racer(savedModel!=null ? savedModel : accurateBrain.build()) : null;
    }

    public void initialize(GfxInternals gfx) {
        if (manualMode) {
            initializeManualModeControls(gfx.getInputManager());
        } else {
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

    public void update(GfxInternals gfx, boolean firstTime, boolean reset) {
        if (manualMode) {
            gfx.clear();
            drawPlayground(gfx);
            car.draw(gfx, manualMode);
        } else {
            if (firstTime) selectNewPopulation(viewPopulationSize);
            if (reset) {
                gfx.clear();
                drawPlayground(gfx);
                selectNewPopulation(viewPopulationSize);
            }
            oneStep(0.5f);
            for (ICreature creature : getPopulation()) {
                creature.draw(gfx, false);
            }
        }
    }


    private void initializeManualModeControls(final InputManager inputManager) {
        if (car != null) {
            inputManager.addListener((ActionListener) (name, isPressed, tpf) -> {
                car.doAction(2);
            }, "Accelerate");
            inputManager.addListener((ActionListener) (name, isPressed, tpf) -> {
                car.doAction(3);
            }, "Break");
            inputManager.addListener((ActionListener) (name, isPressed, tpf) -> {
                car.doAction(1);
            }, "SteerRight");
            inputManager.addListener((ActionListener) (name, isPressed, tpf) -> {
                car.doAction(0);
            }, "SteerLeft");
            inputManager.addMapping("Accelerate", new KeyTrigger(KeyInput.KEY_NUMPAD8));
            inputManager.addMapping("Break", new KeyTrigger(KeyInput.KEY_NUMPAD5));
            inputManager.addMapping("SteerLeft", new KeyTrigger(KeyInput.KEY_NUMPAD4));
            inputManager.addMapping("SteerRight", new KeyTrigger(KeyInput.KEY_NUMPAD6));
        }
    }


    @Override
    public void drawPlayground(GfxInternals g) {
        Mesh loadedMesh = g.drawModel(PLAYGROUND_NAME, "assets/Models/IMOLA.obj", 0f, 0f, 0f, 1f, new MaterialBuilder().diffuseTexture("assets/Textures/DiffuseBaked.png"));
        if (mesh == null) {
            mesh = loadedMesh;
        }
    }

    @Override
    public List<ICreature> applySelection() {
        Collections.sort(population, (o1, o2) -> {
            Racer r1 = (Racer) o1;
            Racer r2 = (Racer) o2;
//            System.out.println("r1.dist="+r1.getDistanceTravelled());
//            System.out.println("r2.dist="+r2.getDistanceTravelled());
            if (r1.getDistanceTravelled() == r2.getDistanceTravelled()) {
                return 0;
            } else if (r1.getDistanceTravelled() > r2.getDistanceTravelled()) {
                return -1;
            } else {
                return 1;
            }
        });
        int origSize = population.size();
        return population.subList(0, origSize / 10);
    }
}
