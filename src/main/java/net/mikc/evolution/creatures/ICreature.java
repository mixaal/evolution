package net.mikc.evolution.creatures;

import net.mikc.evolution.gfx.GfxInternals;
import net.mikc.evolution.neuralnets.NeuralNetwork;

public interface ICreature {
    /**
     * Get my id.
     *
     * @return creature id
     */
    String getId();

    /**
     * Is creature dead or alive? Used for selection.
     *
     * @return true if dead.
     */
    boolean isDead();


    /**
     * Kill creature, e.g. during selection process.
     */
    void kill();

    /**
     * Get creature's brain.
     *
     * @return creature's neural network.
     */
    NeuralNetwork getBrain();

    /**
     * Take action number (the output of neural network), take in account other creatures.
     *
     * @param action action to take
     */
    void doAction(int action);

    /**
     * Born new creature.
     *
     * @param other partner creature
     * @return new creature born
     */
    ICreature mutate(ICreature other);

    /**
     * Get sensor inputs, this is the input to the first layer of the neural network.
     *
     * @return creature sensor inputs.
     */
    float[] getSensorInputs();

    /**
     * Set creature age - 0..1
     * @param age age 0 on born 1 when dies
     */
    void setAge(float age);


    /**
     * Get creature age.
     *
     * @return creature age.
     */
    float getAge();

    /**
     * Get creature fitness.
     *
     * @return creature fitness.
     */
    float getFitness();

    /**
     * Draw creature.
     *
     * @param gfx graphics internals object
     */
    void draw(GfxInternals gfx, boolean drawBrain);
}
