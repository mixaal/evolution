package net.mikc.evolution.neuralnets;

import net.mikc.evolution.neuralnets.layers.Activation;
import net.mikc.evolution.neuralnets.layers.Dense;
import net.mikc.evolution.neuralnets.layers.ILayer;

import java.util.ArrayList;
import java.util.List;

public class SequentialBuilder {
    private List<ILayer> layers = new ArrayList<>();

    public SequentialBuilder input(int N, Activation activation) {
        layers.add(new Dense(N, activation, (ILayer)null));
        return this;
    }

    public SequentialBuilder dense(int N, Activation activation) {
        ILayer last = layers.get(layers.size()-1);
        layers.add(new Dense(N, activation, last));
        return this;
    }

    public NeuralNetwork build() {
        return new NeuralNetwork(layers);
    }
}
