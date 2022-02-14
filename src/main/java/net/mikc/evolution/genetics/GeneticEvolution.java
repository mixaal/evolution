package net.mikc.evolution.genetics;

import net.mikc.evolution.creatures.ICreature;
import net.mikc.evolution.neuralnets.NeuralNetwork;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

public abstract class GeneticEvolution {
    protected List<ICreature> population = new ArrayList<>();
    private final int stepsPerGeneration;
    private final int populationSize;
    private final Supplier<ICreature> creatureSupplier;
    public GeneticEvolution(int populationSize, int stepsPerGeneration, Supplier<ICreature> creatureSupplier) {
        this.stepsPerGeneration = stepsPerGeneration;
        this.populationSize = populationSize;
        this.creatureSupplier = creatureSupplier;
        for(int i=0; i<populationSize; i++) {
            population.add(creatureSupplier.get());
        }
    }

    public abstract List<ICreature> applySelection();

    public List<ICreature> getPopulation() {
        return population;
    }

    public static void advance(ICreature creature) {
        if(creature.isDead()) return;
        NeuralNetwork brain = creature.getBrain();
        brain.setSensorInputs(creature.getSensorInputs());
        brain.feedForward();
        int action = brain.argmax();
        //action = (int)MathUtils.rnd(0, 4);
        creature.doAction(action);
    }

    public void oneStep(float age) {
//        for(ICreature creature: population) {
//            creature.setAge(age);
//            advance(creature);
//        }

        population.parallelStream().forEach(creature -> {creature.setAge(age); advance(creature);});
    }

    public static float getMaxFitness(List<ICreature> selection) {
        float maxFitness = 0;
        for(ICreature c: selection) {
//            System.out.println("maxFitness="+c.getFitness());
            if(c.getFitness()>maxFitness) {
                maxFitness = c.getFitness();
            }
        }
        return maxFitness;
    }

    public void selectNewPopulation(int newPopulationSize) {
        Random random = new Random();
        List<ICreature> alive = applySelection();
        if(alive.size() <=0) {
            System.out.println("Nobody survived. Exit.");
            System.exit(1);
        }
//        System.out.println(" --> max fitness: " + getMaxFitness(alive));
        System.out.println("Alive0.fitness: "+alive.get(0).getFitness());
        int currentSize=alive.size();
        // Fill-in some random creatures to fit new population size
        while(currentSize++<newPopulationSize) {
            alive.add(creatureSupplier.get());
        }

        // best creatures selection
        List<ICreature> bestCreatures = new ArrayList<>();
        bestCreatures.add(alive.get(0));
        bestCreatures.add(alive.get(1));
        bestCreatures.add(alive.get(2));
        // save creatures
        for(int k=0; k<bestCreatures.size(); k++) {
            bestCreatures.get(k).getBrain().save("best-"+k+".nn");
        }

        // Mutate everything together and include the best creatures
        List<ICreature> newPopulation = new ArrayList<>();
        newPopulation.addAll(bestCreatures);
        if(newPopulationSize>1) {
            for (int i = bestCreatures.size(); i < newPopulationSize/2; i++) {

                int i1 = random.nextInt(alive.size());
                int i2 = random.nextInt(alive.size());
                while (i1 == i2) {
                    i2 = random.nextInt(alive.size());
                }
                newPopulation.add(alive.get(i1).mutate(alive.get(i2)));
            }
            for (int i = 1; i < newPopulationSize/2; i++) {
                newPopulation.add(creatureSupplier.get());
            }
        }
        this.population = newPopulation;
    }

    /**
     * Simulates one round of the generation.
     *
     */
    public void generation() {
        for(int i=0; i<stepsPerGeneration; i++) {
            oneStep((float)i/stepsPerGeneration);
        }
        selectNewPopulation(populationSize);
    }
}
