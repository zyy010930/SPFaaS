package scs.methods.SPFaaS;

import scs.methods.SPFaaS.math.IActivationFunction;

/**
 * @ClassName HiddenLayer
 * @Description ...
 * @Author @WZhang
 * @Date 2023/3/9 8:34
 * @Version 1.0
 */
public class HiddenLayer extends NeuralLayer{
    public HiddenLayer(int numberOfNeurons, IActivationFunction iActivationFunction, int numOfInputs) {
        super(numberOfNeurons, iActivationFunction);
        this.numberOfInputs = numOfInputs;
        init();
    }
    public void setNextLayer(NeuralLayer nextLayer) {
        this.nextLayer = nextLayer;
        if(nextLayer.previousLayer != this) {
            nextLayer.setPreviousLayer(this);
        }
    }
    public void setPreviousLayer(NeuralLayer previousLayer) {
        this.previousLayer = previousLayer;
        if(previousLayer.nextLayer != this) {
            previousLayer.setNextLayer(this);
        }
    }
}
