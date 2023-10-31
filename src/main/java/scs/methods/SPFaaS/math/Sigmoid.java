package scs.methods.SPFaaS.math;

/**
 * @ClassName Sigmoid
 * @Description ...
 * @Author @WZhang
 * @Date 2023/3/8 22:07
 * @Version 1.0
 */
public class Sigmoid implements IActivationFunction{
    private double a = 1.0;
    public Sigmoid(double value) {
        this.setA(value);
    }

    public void setA(double value) {
        this.a = value;
    }

    public double calc(double x) {
        return 1.0/(1.0 + Math.exp(-a*x));
    }
}
