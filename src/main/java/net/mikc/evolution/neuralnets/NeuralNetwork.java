package net.mikc.evolution.neuralnets;

import com.google.common.primitives.Floats;
import com.google.gson.Gson;
import net.mikc.evolution.gfx.GfxInternals;
import net.mikc.evolution.neuralnets.layers.Dense;
import net.mikc.evolution.neuralnets.layers.ILayer;
import net.mikc.evolution.neuralnets.serialization.SerializedLayer;
import net.mikc.evolution.neuralnets.serialization.SerializedNeuralNet;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class NeuralNetwork {
    private final ILayer layers[];

    private NeuralNetwork(List<ILayer> layers, boolean skipWeightsInitialization) {
        this.layers = copy(layers.toArray(new ILayer[0]));
        if(!skipWeightsInitialization) {
            for (int i = 1; i < this.layers.length; i++) {
                this.layers[i].initWeights();
            }
        }
    }
    public NeuralNetwork(List<ILayer> layers) {
        this(layers, false);
    }


    /**
     * Save neural network to file.
     *
     * @param filename filename to save the network
     */
    public void save(String filename) {
        final Gson gson = new Gson();
        List<SerializedLayer> serializedLayers = new ArrayList<>();
        for(ILayer layer: layers) {
            List<Float> weights = new ArrayList<>();
            if(layer.getWeights()!=null) {
                for (float w : layer.getWeights()) {
                    weights.add(w);
                }
            } else {
                weights = null;
            }
            serializedLayers.add(new SerializedLayer(layer.getNumberOfNeurons(), layer.getActivation(), weights));
        }
        SerializedNeuralNet snn = new SerializedNeuralNet(serializedLayers);
        String serialized = gson.toJson(snn);

        try {
            Files.write( Paths.get(filename), serialized.getBytes(StandardCharsets.UTF_8));
        }
        catch (IOException ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }

    /**
     * Load nerual network from file.
     *
     * @param filename filename where the neural network is stored.
     * @return neural network instance
     */
    public static NeuralNetwork load(String filename) {
        try {
            Gson gson = new Gson();
            String content = new String ( Files.readAllBytes( Paths.get(filename) ) );
            SerializedNeuralNet snn = gson.fromJson( content, SerializedNeuralNet.class );
            List<ILayer> layers = new ArrayList<>();
            for(SerializedLayer serializedLayer: snn.getLayers()) {
                float []weights = Floats.toArray(serializedLayer.getWeights());
                Dense currentLayer = new Dense(serializedLayer.getNumberOfNeurons(), serializedLayer.getActivation(),  weights);
                layers.add(currentLayer);

            }
            return new NeuralNetwork(layers, true);

        }
        catch (IOException ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }

    private static ILayer[] copy(ILayer layers[]) {
        if(layers==null) {
            return null;
        }
        ILayer[] out = new ILayer[layers.length];
        for(int i=0; i<layers.length; i++) {
            out[i] = layers[i].copy();
        }
        return out;
    }

    public void feedForward() {
        for (int i=1; i<layers.length; i++) {
            float []X = layers[i-1].getOutputs();
            this.layers[i].multiply(X);
        }
    }

    public int argmax() {
        int last_idx = layers.length - 1;
        ILayer last = layers[last_idx];
        float output[] = last.getOutputs();
        int N = last.getNumberOfNeurons();
        int arg_idx = 0;
        float arg_val = -1.0f;
        for (int i=0; i<N; i++) {
            if(output[i]>arg_val) {
                arg_val = output[i];
                arg_idx = i;
            }
        }
        return arg_idx;

    }

    ILayer[] getLayers() {
        return layers;
    }

    public NeuralNetwork mutate(NeuralNetwork other) {
        List<ILayer> newLayers = new ArrayList<>();
        ILayer[] partnerLayers = other.getLayers();
        for(int i=0; i<layers.length; i++) {
            newLayers.add(layers[i].mutate(partnerLayers[i]));
        }
        return new NeuralNetwork(newLayers, true);
    }

    public void setSensorInputs(float[] input) {
        layers[0].setSensorInputs(input);
    }


    private void drawLayer(GfxInternals gfx, ILayer layer, float y, int r, int g, int b) {
        float []output= layer.getOutputs();
        for(int i=0; i< layer.getNumberOfNeurons(); i++) {
            gfx.cube(i*2, y, 0, 0.5f, r, g, b, 15 + (int)(240*output[i]));
            gfx.text(output[i], 0.2f, i*2, y, 0.6f);
        }
    }

    public void draw(GfxInternals gfx) {
        float y = 0;
        for(int i=0; i<layers.length; i++) {
            int r=0, g=0, b=0;
            if(i==0) g=255;
            else if(i==layers.length-1) r=255;
            else b=255;
            drawLayer(gfx, layers[i], y, r, g, b);
            y+=2;
        }
        y=2;
        for(int i=1; i<layers.length; i++) {
            int pN = layers[i-1].getNumberOfNeurons();
            int N = layers[i].getNumberOfNeurons();
            float w[] = layers[i].getWeights();
            for(int j=0; j<N; j++)
                for(int k=0; k<pN; k++) {
                    gfx.line(j * 2, y , 0, k*2, y-2, 0, 255, 255, 255, 255);
                    float sx = j*2 + 2.0f*(k*2-j*2)/3.0f;
                    float sy = y - 2.0f*2/3.0f;
                    gfx.text(w[j*pN+k], 0.1f, sx, sy, 0.6f);
                }
            y+=2;
        }
    }
}
