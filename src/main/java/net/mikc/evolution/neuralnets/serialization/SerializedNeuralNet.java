package net.mikc.evolution.neuralnets.serialization;

import java.util.List;

public class SerializedNeuralNet {
    public SerializedNeuralNet(List<SerializedLayer> layers) {
        this.layers = layers;
    }
    private List<SerializedLayer> layers;

    public List<SerializedLayer> getLayers() {
        return layers;
    }
}
