package scs.methods.SPFaaS.math;

/**
 * @ClassName Linear
 * @Description ...
 * @Author @WZhang
 * @Date 2023/3/8 22:18
 * @Version 1.0
 */
public class Linear implements IActivationFunction{
    private double a = 1.0;
    public Linear(double value) {
        this.setA(value);
    }
    public void setA(double value) {
        this.a = value;
    }
    public double calc(double x) {
        return a * x;
    }
}
