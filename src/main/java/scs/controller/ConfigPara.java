package scs.controller;

import java.util.Date;
import java.util.LinkedList;
import java.util.Queue;

/**
 * @ClassName ConfigPara
 * @Description ...
 * @Author @WZhang
 * @Date 2023/3/6 15:47
 * @Version 1.0
 */
public class ConfigPara {
    private static Double maxFuncCapacity;      //The maximum memory capacity storing functions
    private static Double minFuncCapacity;      //The minimum memory capacity storing functions
    private static Double currFuncCapacity;     //The current remaining memory capacity storing functions
    public static Double[] funcCapacity;        //The sizes of functions
    public static Integer[] kpArray;            //This array records the keep-alive time for functions

    public static Double[] initTime;
    public static Integer[] invokeTime;
    public static Double[] costNum;

    /*********
        When functions are running at memory and the remaining memory capacity cannot support to response other functions,
        the functions are added into the waitQueue. Therefore, the waitQueue stores ids of waiting functions.
    ********/
    public static Queue<Integer> waitQueue;

    /********
        This array records the state of each function.
        0: not initial;
        1: keep-alive;
        2: running.
    ********/
    public static Integer[] funcFlagArray;

    public ConfigPara() {
        maxFuncCapacity = 120.5;
        minFuncCapacity = 10.6;
        currFuncCapacity = 60.0;
        funcCapacity = new Double[]{7.3, 6.1, 6.5, 5.7, 7.8, 9.0, 8.5,10.6,5.4,7.1,6.6,8.1,9.2,8.0,7.2,10.4};

        kpArray = new Integer[]{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
        funcFlagArray = new Integer[]{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
        waitQueue = new LinkedList<>();

        initTime = new Double[]{1.7,2.2,2.5,3.1,2.9,3.5,2.0,1.7,2.2,2.5,3.1,2.9,3.5,2.0,1.7,2.8};
        invokeTime = new Integer[]{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};

        costNum = new Double[]{0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0};
    }

    public static void setMemoryCapacity(Double currFuncCapacity1) {
        if(currFuncCapacity1 < maxFuncCapacity && currFuncCapacity1 >= 0) {
            currFuncCapacity = currFuncCapacity1;
        }
        else{
            System.out.println("Capacity Setting Error!");
        }
    }

    public static Double getRemainMemCapacity(){
        return currFuncCapacity;
    }
}

