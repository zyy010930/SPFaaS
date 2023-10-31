package scs.methods.SPFaaS.math;

/**
 * @ClassName IActivationFunction
 * @Description ...
 * @Author @WZhang
 * @Date 2023/3/8 21:17
 * @Version 1.0
 */
public interface IActivationFunction {
    double calc(double x);
    public enum ActivationFunctionENUM {
        STEP, LINEAR, SIGMOID, HYPERTAN
    }
}
