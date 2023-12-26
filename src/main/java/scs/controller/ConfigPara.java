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

    public static String[] funcName;

    public static Long[] firstTime;
    public static boolean[] start;
    public static Integer[] coldStartTime;
    public static Long[] oldTime;
    public static Long[] nowTime;
    public static double[] preWarm;
    public static double[] keepAlive;


    public ConfigPara() {
        maxFuncCapacity = 120.5;
        minFuncCapacity = 10.6;
        currFuncCapacity = 60.0;
        funcCapacity = new Double[300];

        kpArray = new Integer[300];
        funcFlagArray = new Integer[300];
        waitQueue = new LinkedList<>();

        initTime = new Double[300];
        invokeTime = new Integer[300];

        costNum = new Double[300];
        funcName = new String[300];
        firstTime = new Long[300];
        start = new boolean[300];
        coldStartTime = new Integer[300];
        oldTime = new Long[300];
        nowTime = new Long[300];
        preWarm = new double[300];
        keepAlive = new double[300];
        for(int i=0;i<300;i++)
        {
            funcCapacity[i] = 100.0;
            initTime[i] = 1.0 + (i%5)*0.2;
            funcName[i] = "func" + i;
        }
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

