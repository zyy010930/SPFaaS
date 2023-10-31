package scs.methods.SPFaaS;

import scs.methods.SPFaaS.math.IActivationFunction;
import scs.methods.SPFaaS.math.Linear;

import java.util.ArrayList;

/**
 * @ClassName InputLayer
 * @Description ...
 * @Author @WZhang
 * @Date 2023/3/8 22:17
 * @Version 1.0
 */
public class InputLayer extends NeuralLayer{
    public InputLayer(int numberOfInputs) {
        super(numberOfInputs, new Linear(1));
        previousLayer = null;
        this.numberOfInputs = numberOfInputs;
        init();
    }
    public void init() {
        for (int i = 0; i < numberOfInputs; i++) {
            this.setNeurons(i, new InputNeuron());
            this.getNeuron(i).init();
        }
    }
    public void calc() {
        if(input != null && getListOfNeurons() != null) {
            for(int i = 0; i < numberOfNeuronsInLayer; i++) {
                double[] firstInput = {this.input.get(i)};
                getNeuron(i).setInputs(firstInput);
                getNeuron(i).calc();
                try{
                    output.set(i,getNeuron(i).getOutput());
                }
                catch(IndexOutOfBoundsException iobe){
                    output.add(getNeuron(i).getOutput());
                }
            }
        }
    }
    public void setInputs(ArrayList<Double> inputs){
        if(inputs.size() == numberOfInputs){
            this.input = inputs;
        }
    }
}
