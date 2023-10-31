package scs.methods.SPFaaS;

import scs.methods.SPFaaS.math.IActivationFunction;
import scs.methods.SPFaaS.math.RandomNumberGenerator;

import java.util.ArrayList;

/**
 * @ClassName Neuron
 * @Description ...
 * @Author @WZhang
 * @Date 2023/3/8 21:10
 * @Version 1.0
 */
public class Neuron {
    protected ArrayList<Double> weight;
    private ArrayList<Double> input;
    private Double output;
    private Double outputBeforeActivation;
    private int numberOfInputs = 0;
    protected Double bias = 1.0;
    private IActivationFunction iActivationFunction;


    public Neuron(int numberOfInputs){
        this.numberOfInputs = numberOfInputs;
        weight=new ArrayList<>(numberOfInputs + 1);
        input=new ArrayList<>(numberOfInputs);
    }


    public Neuron(int numberOfInputs, IActivationFunction iActivationFunction) {
        this.numberOfInputs = numberOfInputs;
        this.iActivationFunction = iActivationFunction;
        weight = new ArrayList<>(numberOfInputs + 1);
        input = new ArrayList<>(numberOfInputs);
    }

    public void init() {
        if(numberOfInputs > 0) {
            for(int i = 0; i < numberOfInputs; i++) {
                double newWeight = RandomNumberGenerator.GenerateNext();
                try {
                    this.weight.set(i, newWeight);
                }
                catch (IndexOutOfBoundsException iobe) {
                    this.weight.add(newWeight);
                }
            }
        }
    }

    public void calc() {
        outputBeforeActivation = 0.0;

        if(numberOfInputs > 0) {
            if(input != null && weight != null) {
                for (int i = 0; i < numberOfInputs; i++) {
                    outputBeforeActivation = outputBeforeActivation + (i == numberOfInputs?bias:input.get(i))*weight.get(i);
                }
            }
        }
        output = iActivationFunction.calc(outputBeforeActivation);
    }

    public void setiActivationFunction(IActivationFunction iActivationFunction) {
        this.iActivationFunction = iActivationFunction;
    }

    public void setInputs(double[] values) {
        if(values.length == numberOfInputs) {
            for(int i = 0; i < numberOfInputs; i++) {
                try {
                    input.set(i, values[i]);
                }
                catch (IndexOutOfBoundsException iobe) {
                    input.add(values[i]);
                }
            }
        }
    }


    public void setInputs(ArrayList<Double> values) {
        if(values.size() == numberOfInputs) {
            this.input = values;
        }
    }
    public double getOutput() {
        return output;
    }
}
