package scs.methods.SPFaaS;

import scs.methods.SPFaaS.math.Linear;

/**
 * @ClassName InputNeuron
 * @Description ...
 * @Author @WZhang
 * @Date 2023/3/8 22:26
 * @Version 1.0
 */
public class InputNeuron extends Neuron{
    public InputNeuron() {
        super(1);
        setiActivationFunction(new Linear(1));
        bias = 0.0;
    }
    public void init() {
        try {
            this.weight.set(0, 1.0);
            this.weight.set(1, 0.0);
        }
        catch (IndexOutOfBoundsException iobe) {
            this.weight.add(1.0);
            this.weight.add(0.0);
        }
    }
}
