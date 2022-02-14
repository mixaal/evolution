package net.mikc.evolution.neuralnets.serialization;

import net.mikc.evolution.neuralnets.layers.Activation;

import java.util.List;

public class SerializedLayer {
    private Activation activation;
    private List<Float> weights;
    private Integer numberOfNeurons;

    public SerializedLayer(int numberOfNeurons, Activation activation, List<Float> weights) {
        this.activation = activation;
        this.weights = weights;
        this.numberOfNeurons = numberOfNeurons;
    }

    public Activation getActivation() {
        return activation;
    }

    public List<Float> getWeights() {
        return weights;
    }

    public Integer getNumberOfNeurons() {
        return numberOfNeurons;
    }
}
