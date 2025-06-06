package scs.controller;

import org.apache.http.impl.client.CloseableHttpClient;
import scs.util.tools.HttpClientPool;

import java.util.*;

import static java.lang.Math.max;
import static scs.methods.MultiContainer.SPFaaS.DFSFunctionMulti;
import static scs.methods.Zyy.ZyyFaaS.DFSFunction;

/**
 * @ClassName ConfigPara
 * @Description ...
 * @Author @WZhang
 * @Date 2023/3/6 15:47
 * @Version 1.0
 */
public class ConfigPara {
    private static Double maxFuncCapacity;      //The maximum memory capacity storing functions
    private static Double currFuncCapacity;     //The current remaining memory capacity storing functions
    public static Double[] funcCapacity;        //The sizes of functions
    public static Integer[] kpArray;            //This array records the keep-alive time for functions

    public static Double[] initTime;
    public static Integer[] invokeTime;
    public static Double[] costNum;

    public static Double[] priory;

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

    public static Integer[] outOfBound;

    public static ArrayList<ArrayList<Long>> timeList;

    public static Double[] mean;

    public static Double[] standard;

    public static Double[] cv;

    public static double beta;

    public static double gama;

    public static Integer maxContainer;

    public static Integer[] preWarmTime;

    public ConfigPara() {
        maxContainer = 1;
        //maxFuncCapacity = 43500.0;
        beta = 0.5;
        gama = 0.5;
        maxFuncCapacity = 22500.0;
        currFuncCapacity = 0.0;
        funcCapacity = new Double[300];

        kpArray = new Integer[300];
        funcFlagArray = new Integer[300];
        waitQueue = new LinkedList<>();

        initTime = new Double[300];
        invokeTime = new Integer[300];

        costNum = new Double[300];
        priory = new Double[300];
        funcName = new String[300];
        firstTime = new Long[300];
        start = new boolean[300];
        coldStartTime = new Integer[300];
        oldTime = new Long[300];
        nowTime = new Long[300];
        preWarm = new double[300];
        keepAlive = new double[300];
        outOfBound = new Integer[300];
        mean = new Double[300];
        standard = new Double[300];
        cv = new Double[300];
        timeList = new ArrayList<>();
        preWarmTime = new Integer[300];


        for(int i=0;i<300;i++)
        {
            funcCapacity[i] = 100.0 + ((i%20)-5)*10;
            initTime[i] = 1.0 + (i%5)*0.2;
            funcName[i] = "func" + i;
            kpArray[i] = 0;
            funcFlagArray[i] = 0;
            invokeTime[i] = 0;
            costNum[i] = 0.0;
            priory[i] = Double.MAX_VALUE;
            firstTime[i] = 0L;
            start[i] = false;
            coldStartTime[i] = 0;
            oldTime[i]= 0L;
            nowTime[i]= 0L;
            preWarm[i] = 0.0;
            keepAlive[i] = 0.0;
            outOfBound[i] = 0;
            timeList.add(new ArrayList<Long>());
            mean[i] = 0.0;
            standard[i] = 0.0;
            cv[i] = 0.0;
            preWarmTime[i] = 0;
        }
    }

    public static void setMemoryCapacity(Double currFuncCapacity1) {
        if(currFuncCapacity1 <= maxFuncCapacity && currFuncCapacity1 >= 0) {
            currFuncCapacity = currFuncCapacity1;
        }
        else{
            System.out.println("Capacity Setting Error!");
        }
    }

    public static synchronized Double getRemainMemCapacity(){
        double capacity = 0.0;
        for(int i=0;i<300;i++)
        {
            if(ConfigPara.funcFlagArray[i] != 0)
            {
                capacity+=ConfigPara.funcCapacity[i];
            }
        }
        currFuncCapacity = maxFuncCapacity - capacity;
        return currFuncCapacity;
    }

    public static synchronized void containerRelease(Integer serviceId){
        double kp = 0;
        int invoke = 0;
        int coldStartTime = 0;
        CloseableHttpClient httpClient;
        String url = "http://192.168.1.7:31112/function/func"+serviceId;
        String url0 = "http://192.168.1.7:31112/zero/func"+serviceId;
        httpClient= HttpClientPool.getInstance().getConnection();
        for(int j=0;j<ConfigPara.funcFlagArray.length;j++)
        {
            if(ConfigPara.invokeTime[j]>invoke)
            {
                invoke = ConfigPara.invokeTime[j];
            }
            if(ConfigPara.keepAlive[j]>kp)
            {
                kp = ConfigPara.keepAlive[j];
            }
            if(ConfigPara.coldStartTime[j]>coldStartTime)
            {
                coldStartTime = ConfigPara.coldStartTime[j];
            }
        }
        for(int i=0;i<300;i++)
        {
            //ConfigPara.costNum[i] = 0.5*(ConfigPara.invokeTime[i]/invoke) + 0.5*(ConfigPara.keepAlive[i]/kp); //计算每个函数容器的释放代价
            ConfigPara.costNum[i] = ConfigPara.beta*((ConfigPara.invokeTime[i]+1)/(invoke+1)) + (1-ConfigPara.beta)*(1 - (ConfigPara.coldStartTime[i]+1)/(coldStartTime+1)); //计算每个函数容器的释放代价
        }

        Double min = Double.MAX_VALUE;
        Set<Integer> bestList = new HashSet<>();
        int num = 0;
        Random random = new Random();
        int minValue = 0;
        int maxValue = 299;
        int randomInt = random.nextInt(maxValue - minValue + 1) + minValue;
        for(int i=randomInt;i < randomInt + 300;i++)
        {
            int id = i % 300;
            if(ConfigPara.funcFlagArray[id] == 0)
            {
                continue;
            }
            double cost = 0.0;
            Set<Integer> list = new HashSet<>();
            Map<Set<Integer>,Double> mp1 = new HashMap<>();
            mp1 = DFSFunction(list,id,ConfigPara.funcCapacity[serviceId - 1],ConfigPara.costNum[id]);
            Set<Integer> list1 = new HashSet<>();
            for (Map.Entry<Set<Integer>,Double> entry : mp1.entrySet()) {
                cost = entry.getValue();
                list1.addAll(entry.getKey());
            }
            if(cost < min)
            {
                min = cost;
                bestList.clear();
                bestList.addAll(list1);
            }
            if(num == 5)
            {
                break;
            }
            num++;
        }
        for(int i=0;i<300;i++)
        {
            if(bestList.contains(i)) {
                System.out.println(i + "-----------release-----------");
                ConfigPara.funcFlagArray[i] = 0;
                //HttpClientPool.getResponseTime(httpClient, url0);
                ConfigPara.getRemainMemCapacity();
            }
        }
    }

    public static synchronized void containerReleaseMulti(Integer serviceId){
        double kp = 0;
        int invoke = 0;
        int coldStartTime = 0;
        CloseableHttpClient httpClient;
        String url = "http://192.168.1.7:31112/function/func"+serviceId;
        String url0 = "http://192.168.1.7:31112/zero/func"+serviceId;
        httpClient= HttpClientPool.getInstance().getConnection();
        for(int j=0;j<ConfigPara.funcFlagArray.length;j++)
        {
            if(ConfigPara.invokeTime[j]>invoke)
            {
                invoke = ConfigPara.invokeTime[j];
            }
            if(ConfigPara.keepAlive[j]>kp)
            {
                kp = ConfigPara.keepAlive[j];
            }
            if(ConfigPara.coldStartTime[j]>coldStartTime)
            {
                coldStartTime = ConfigPara.coldStartTime[j];
            }
        }
        for(int i=0;i<300;i++)
        {
            //ConfigPara.costNum[i] = 0.5*(ConfigPara.invokeTime[i]/invoke) + 0.5*(ConfigPara.keepAlive[i]/kp); //计算每个函数容器的释放代价
            ConfigPara.costNum[i] = ConfigPara.beta*((ConfigPara.invokeTime[i]+1)/(invoke+1)) + (1-ConfigPara.beta)*(1 - (ConfigPara.coldStartTime[i]+1)/(coldStartTime+1)); //计算每个函数容器的释放代价
        }

        Double min = Double.MAX_VALUE;
        Set<Integer> bestList = new HashSet<>();
        int num = 0;
        Random random = new Random();
        int minValue = 0;
        int maxValue = 299;
        int randomInt = random.nextInt(maxValue - minValue + 1) + minValue;
        for(int i=randomInt;i < randomInt + 300;i++)
        {
            int id = i % 300;
            if(ConfigPara.funcFlagArray[id] <= 0)
            {
                continue;
            }
            double cost = 0.0;
            Set<Integer> list = new HashSet<>();
            Map<Set<Integer>,Double> mp1 = new HashMap<>();
            mp1 = DFSFunctionMulti(list,id,ConfigPara.funcCapacity[serviceId - 1],ConfigPara.costNum[id]);
            Set<Integer> list1 = new HashSet<>();
            for (Map.Entry<Set<Integer>,Double> entry : mp1.entrySet()) {
                cost = entry.getValue();
                list1.addAll(entry.getKey());
            }
            if(cost < min)
            {
                min = cost;
                bestList.clear();
                bestList.addAll(list1);
            }
            if(num == 5)
            {
                break;
            }
            num++;
        }
        for(int i=0;i<300;i++)
        {
            if(bestList.contains(i)) {
                System.out.println(i + "-----------release-----------");
                ConfigPara.funcFlagArray[i]--;
                ConfigPara.funcFlagArray[i] = max(0, ConfigPara.funcFlagArray[i]);
                //HttpClientPool.getResponseTime(httpClient, url0);
                ConfigPara.getRemainMemCapacity();
            }
        }
    }
}

