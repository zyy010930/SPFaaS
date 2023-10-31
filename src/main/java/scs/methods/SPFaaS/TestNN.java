package scs.methods.SPFaaS;

import scs.methods.SPFaaS.math.IActivationFunction;
import scs.methods.SPFaaS.math.Linear;
import scs.methods.SPFaaS.math.RandomNumberGenerator;
import scs.methods.SPFaaS.math.Sigmoid;

/**
 * @ClassName TestNN
 * @Description ...
 * @Author @WZhang
 * @Date 2023/3/9 9:14
 * @Version 1.0
 */
public class TestNN {
    public static void main(String[] args) {
        RandomNumberGenerator.seed = 0;
        int numberOfInputs = 2;
        int numberOfOutputs = 1;
        int[] numberOfHiddenNeurons = {3};
        IActivationFunction[] hiddenIAF = {new Sigmoid(1.0)};
        Linear outputIAF = new Linear(1.0);

        System.out.println("Create NN...");
        NeuralNet neuralNet = new NeuralNet(numberOfInputs, numberOfOutputs, numberOfHiddenNeurons, hiddenIAF, outputIAF);
        System.out.println("Neural Network Network...");

        System.out.println("Feeding the values {1.5;0.5} to the neural network");
        double[] neuralInput = {1.5, 0.5};
        double[] neuralOutput;
        neuralNet.setInput(neuralInput);
        neuralNet.calc();
        neuralOutput = neuralNet.getOutputs();
        System.out.println("OutPut 1:" + neuralOutput[0]);

        neuralInput[0] = 1.0;
        neuralInput[1] = 2.1;
        System.out.println("Feeding the values {1.0;2.1} to the neural network");
        neuralNet.setInput(neuralInput);
        neuralNet.calc();
        neuralOutput = neuralNet.getOutputs();
        System.out.println("OutPut 2:" + neuralOutput[0]);
    }
}
