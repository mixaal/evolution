package net.mikc.evolution.neuralnets.layers;

import net.mikc.evolution.utils.MathUtils;

public class Dense implements ILayer {
    private final float weights[];
    private final float output[];
    private final int numberOfNeurons;
    private final Activation activation;

    private Dense(final int N, final Activation activation, float weights[]){
        this.numberOfNeurons = N;
        if(weights==null) {
            this.weights = weights;
        } else {
            this.weights = new float[weights.length];
            System.arraycopy(weights, 0, this.weights, 0, weights.length);
        }
        this.activation = activation;
        this.output = new float[N];
    }

    public Dense(final int N, final Activation activation, ILayer prev) {
        int WN = prev == null ? 0 : prev.getNumberOfNeurons() * N;
        this.weights = prev == null ? null : new float[WN];
        this.output = new float[N];
        this.activation = activation;
        this.numberOfNeurons = N;
    }

    private Dense(final Activation activation, final float weights[], final float output[]) {
        this.numberOfNeurons = output.length;
        this.activation = activation;
        if(weights==null) {
            this.weights = null;
        } else {
            this.weights = new float[weights.length];
            System.arraycopy(weights, 0, this.weights, 0, weights.length);
        }
        if(output==null) {
            this.output = null;
        } else {
            this.output = new float[output.length];
            System.arraycopy(output, 0, this.output, 0, output.length);
        }
    }

    @Override
    public int getNumberOfNeurons() {
        return numberOfNeurons;
    }

    @Override
    public Activation getActivation() {
        return activation;
    }

    @Override
    public void initWeights() {
//        System.out.println("Called");
        for (int i = 0; i < weights.length; i++) {
            weights[i] = MathUtils.rnd(-1.0f, 1.0f);
        }
    }

    @Override
    public float[] getOutputs() {
        return output;
    }

    @Override
    public void setSensorInputs(float[] sensorInputs) {
        for(int i=0; i<sensorInputs.length;i++) {
            output[i] = sensorInputs[i];
        }
    }

    @Override
    public float[] getWeights() {
        return weights;
    }

    @Override
    public void multiply(float[] X) {
        // sum Xi*wi
        for (int i = 0; i < output.length; i++) {
            output[i] = 0.0f;
            for (int j = 0; j < X.length; j++) {
                output[i] += weights[X.length * i + j] * X[j];
            }
            output[i] = activate(output[i], activation);
        }
        if(Activation.SOFTMAX.equals(activation)) {
            double sum = 0;
            for(int i=0; i<output.length; i++) {
                sum += java.lang.Math.exp(output[i]);
            }
            for(int i=0; i<output.length; i++) {
                output[i] = (float)(java.lang.Math.exp(output[i])/sum);
            }
        }
    }

    @Override
    public ILayer mutate(ILayer other) {
        Dense partner = (Dense) other;
        if(weights==null) {
            return new Dense(numberOfNeurons, activation, (float[])null);
        }
        float w[] = new float[weights.length];
        float pw[] = partner.getWeights();
        for(int i=0; i<weights.length/2; i++) {
            w[i] = weights[i];
        }
        for(int i=weights.length/2; i<weights.length; i++) {
            w[i] = pw[i];
        }
        return new Dense(numberOfNeurons, activation, weights);
    }

    @Override
    public ILayer copy() {
        return new Dense(activation, weights, output);
    }
}
