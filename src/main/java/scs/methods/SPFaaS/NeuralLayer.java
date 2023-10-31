package scs.methods.SPFaaS;

import scs.methods.SPFaaS.math.IActivationFunction;

import java.util.ArrayList;

/**
 * @ClassName NeuralLayer
 * @Description ...
 * @Author @WZhang
 * @Date 2023/3/8 21:46
 * @Version 1.0
 */
public class NeuralLayer {
    protected int numberOfNeuronsInLayer;
    private ArrayList<Neuron> neurons;
    protected IActivationFunction iActivationFunction;
    protected NeuralLayer previousLayer;
    protected NeuralLayer nextLayer;
    protected ArrayList<Double> input;
    protected ArrayList<Double> output;
    protected  int numberOfInputs;

    public NeuralLayer(int numberOfNeuronsInLayer, IActivationFunction iActivationFunction) {
        this.numberOfNeuronsInLayer = numberOfNeuronsInLayer;
        this.iActivationFunction = iActivationFunction;
        this.neurons = new ArrayList<>(numberOfNeuronsInLayer);
        this.output = new ArrayList<>(numberOfNeuronsInLayer);
    }

    protected void init() {
        if(numberOfNeuronsInLayer >= 0) {
            for(int i = 0; i < numberOfNeuronsInLayer; i++) {
                try {
                    neurons.get(i).setiActivationFunction(iActivationFunction);
                    neurons.get(i).init();
                }
                catch (IndexOutOfBoundsException iobe) {
                    neurons.add(new Neuron(numberOfInputs, iActivationFunction));
                    neurons.get(i).init();
                }
            }
        }
    }

    protected void calc() {
        if(input != null && neurons != null) {
            for (int i = 0; i < numberOfNeuronsInLayer; i++) {
                neurons.get(i).setInputs(this.input);
                neurons.get(i).calc();
                try {
                    output.set(i, neurons.get(i).getOutput());
                }
                catch (IndexOutOfBoundsException iobe) {
                    output.add(neurons.get(i).getOutput());
                }
            }
        }
    }
    protected void setNeurons(int i, Neuron neuron) {
        try {
            this.neurons.set(i, neuron);
        }
        catch (IndexOutOfBoundsException iobe) {
            this.neurons.add(neuron);
        }
    }
    protected Neuron getNeuron(int i) {
        return neurons.get(i);
    }
    protected void setInputs(ArrayList<Double> inputs){
        this.numberOfInputs=inputs.size();
        this.input=inputs;
    }
    public ArrayList<Neuron> getListOfNeurons(){
        return neurons;
    }
    protected void setNextLayer(NeuralLayer neuralLayer) {
        this.nextLayer = neuralLayer;
    }
    protected void setPreviousLayer(NeuralLayer neuralLayer) {
        this.previousLayer = neuralLayer;
    }
    public int getNumberOfNeuronsInLayer() {
        return numberOfNeuronsInLayer;
    }
    public NeuralLayer getPreviousLayer() {
        return previousLayer;
    }
    protected ArrayList<Double> getOutputs() {
        return output;
    }
}
