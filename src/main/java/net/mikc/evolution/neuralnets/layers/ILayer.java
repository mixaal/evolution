package net.mikc.evolution.neuralnets.layers;

/**
 * Neural network layer interface.
 */
public interface ILayer {
    /**
     * Create a copy of existing layer.
     *
     * @return copy of existing layer
     */
    ILayer copy();

    /**
     * Get number of neurons in the current layer.
     *
     * @return number of neurons in the current layer
     */
    int getNumberOfNeurons();

    /**
     * Get activation function. See {@link Activation} for a list of activation methods.
     *
     * @return activation function.
     */
    Activation getActivation();

    /**
     * Initialize weights vector to random values.
     */
    void initWeights();

    /**
     * Multiply weights matrix with feature vector X and store the results as output values vector.
     *
     * @param X feature vector to multiply with
     */
    void multiply(float []X);

    /**
     * Get current output values vector.
     *
     * @return output values vector
     */
    float[] getOutputs();

    /**
     * Bring sensors inputs as layer input values.
     *
     * @param sensorInputs input values
     */
    void setSensorInputs(float[] sensorInputs);

    /**
     * Get weights matrix.
     *
     * @return weights matrix
     */
    float[] getWeights();

    /**
     * Mutate layer with another layer.
     *
     * @param other another layer
     * @return mutated layer
     */
    ILayer mutate(ILayer other);

    /**
     * Activatoin function default implementation.
     *
     * @param x input
     * @param activation activation function
     * @return value of activation function.
     */
    default float activate(float x, Activation activation) {
        switch (activation) {
            case RELU:
                return java.lang.Math.max(0, x);
            case LEAKY_RELU:
                return java.lang.Math.max(0.1f * x, x);
            case SIGMOID:
                return 1.0f / (1.0f + (float) java.lang.Math.exp(-x));
            case TANH:
                return (float) java.lang.Math.tanh(x);
            case PASS_VALUE:
            default:
                return x;
        }
    }
}
