package scs.methods.SPFaaS;

import scs.methods.SPFaaS.math.IActivationFunction;

/**
 * @ClassName OutputLayer
 * @Description ...
 * @Author @WZhang
 * @Date 2023/3/9 8:38
 * @Version 1.0
 */
public class OutputLayer extends NeuralLayer{
    public OutputLayer(int numberOfNeurons, IActivationFunction iActivationFunction, int numberOfInputs) {
        super(numberOfNeurons, iActivationFunction);
        this.numberOfInputs = numberOfInputs;
        nextLayer = null;
        init();
    }
    public void setNextLayer(NeuralLayer nextLayer) {
        nextLayer = null;
    }
    public void setPreviousLayer(NeuralLayer neuralLayer) {
        this.previousLayer = neuralLayer;
        if(neuralLayer.nextLayer != this) {
            neuralLayer.setNextLayer(this);
        }
    }
}
