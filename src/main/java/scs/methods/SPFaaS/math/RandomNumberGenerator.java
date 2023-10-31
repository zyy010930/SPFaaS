package scs.methods.SPFaaS.math;

import java.util.Random;

/**
 * @ClassName RandomNumberGenerator
 * @Description ...
 * @Author @WZhang
 * @Date 2023/3/8 21:29
 * @Version 1.0
 */
public class RandomNumberGenerator {
    public static long seed = 0;
    public static Random r;

    public static double GenerateNext() {
        if(r == null) {
            r = new Random(seed);
        }
        return r.nextDouble();
    }

    public static void setSeed(long seed) {
        RandomNumberGenerator.seed = seed;
        r.setSeed(seed);
    }
}
