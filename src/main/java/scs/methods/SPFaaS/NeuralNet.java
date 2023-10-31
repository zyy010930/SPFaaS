package scs.methods.SPFaaS;

import scs.methods.SPFaaS.math.IActivationFunction;

import java.util.ArrayList;

/**
 * @ClassName NeuralNet
 * @Description ...
 * @Author @WZhang
 * @Date 2023/3/8 22:11
 * @Version 1.0
 */
public class NeuralNet {
    private InputLayer inputLayer;
    private ArrayList<HiddenLayer> hiddenLayers;
    private OutputLayer outputLayer;
    private int numberOfInputs;
    private int numberOfOutputs;
    private int numberOfHiddenLayers;
    private ArrayList<Double> input;
    private ArrayList<Double> output;

    public NeuralNet(int numberOfInputs, int numberOfOutputs, int[] numberOfHiddenNeurons, IActivationFunction[] hiddenIAF, IActivationFunction outputIAF) {
        this.numberOfInputs = numberOfInputs;
        this.numberOfOutputs = numberOfOutputs;
        this.numberOfHiddenLayers = numberOfHiddenNeurons.length;

        if(numberOfHiddenLayers == hiddenIAF.length) {
            this.input = new ArrayList<>(numberOfInputs);
            this.inputLayer = new InputLayer(numberOfInputs);
            if(numberOfHiddenLayers > 0) {
                this.hiddenLayers = new ArrayList<>(numberOfHiddenLayers);
            }
            for (int i = 0; i < numberOfHiddenLayers; i++) {
                if(i == 0) {
                    try {
                        hiddenLayers.set(i, new HiddenLayer(numberOfHiddenNeurons[i], hiddenIAF[i], inputLayer.getNumberOfNeuronsInLayer()));
                    }
                    catch (IndexOutOfBoundsException iobe) {
                        hiddenLayers.add(new HiddenLayer(numberOfHiddenNeurons[i], hiddenIAF[i], inputLayer.getNumberOfNeuronsInLayer()));
                    }
                    inputLayer.setNextLayer(hiddenLayers.get(i));
                }
                else {
                    try {
                        hiddenLayers.set(i, new HiddenLayer(numberOfHiddenNeurons[i], hiddenIAF[i], hiddenLayers.get(i - 1).getNumberOfNeuronsInLayer()));
                    }
                    catch (IndexOutOfBoundsException iobe) {
                        hiddenLayers.add(new HiddenLayer(numberOfHiddenNeurons[i], hiddenIAF[i], hiddenLayers.get(i - 1).getNumberOfNeuronsInLayer()));
                    }
                    hiddenLayers.get(i - 1).setNextLayer(hiddenLayers.get(i));
                }
            }
            if(numberOfHiddenLayers > 0) {
                outputLayer = new OutputLayer(numberOfOutputs, outputIAF, hiddenLayers.get(numberOfHiddenLayers-1).numberOfNeuronsInLayer);
                hiddenLayers.get(numberOfHiddenLayers-1).setNextLayer(outputLayer);
            }
            else {
                outputLayer = new OutputLayer(numberOfInputs, outputIAF, numberOfOutputs);
                inputLayer.setNextLayer(outputLayer);
            }
        }
    }
    /*
    public void setInput(ArrayList<Double> inputs) {
        if(inputs.size() == numberOfInputs) {
            this.input = inputs;
        }
    }

     */
    public void setInput(double[] inputs) {
        if(inputs.length == numberOfInputs) {
            for (int i = 0; i < numberOfInputs; i++) {
                try {
                    input.set(i, inputs[i]);
                }
                catch (IndexOutOfBoundsException iobe) {
                    input.add(inputs[i]);
                }
            }
        }
    }
    public void calc() {
        inputLayer.setInputs(input);
        inputLayer.calc();

        if(numberOfHiddenLayers > 0) {
            for (int i = 0; i < numberOfHiddenLayers; i++) {
                HiddenLayer hiddenLayer = hiddenLayers.get(i);
                hiddenLayer.setInputs(hiddenLayer.getPreviousLayer().getOutputs());
                hiddenLayer.calc();
            }
        }
        outputLayer.setInputs(outputLayer.getPreviousLayer().getOutputs());
        outputLayer.calc();
        this.output = outputLayer.getOutputs();
    }
    public double[] getOutputs() {
        double[] outputs = new double[numberOfOutputs];

        for(int i = 0; i < numberOfOutputs; i++) {
            outputs[i] = output.get(i);
        }
        return outputs;
    }
}
