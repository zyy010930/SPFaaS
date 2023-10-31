package scs.methods.Zyy;

import scs.controller.ConfigPara;
import scs.controller.OperWaitQueue;
import scs.util.loadGen.driver.AbstractJobDriver;

import java.util.*;

public class ZyyFaaS {
    public static Double[] cost = new Double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
    public ZyyFaaS(){}

    public static void queuePrint(Queue<Integer> queue)
    {
        for(Integer i : queue)
        {
            System.out.println(i + " ");
        }
    }

    public static void printMemory()
    {
        System.out.println("-----当前内存包括-----");
        for(int i = 0; i < 16; i++)
        {
            if(ConfigPara.funcFlagArray[i] != 0)
                System.out.println(AbstractJobDriver.FuncName[i]);
        }
        System.out.println("------内存:" + ConfigPara.getRemainMemCapacity() + "-----");
    }

    public static Map<Set<Integer>,Double> DFSFunction(Set<Integer> idList, int id, double capacity, double sumCost)
    {
        Map<Set<Integer>,Double> mp = new HashMap<>();
        if(capacity <= ConfigPara.funcCapacity[id])
        {
            Set<Integer> list = new HashSet<>(idList);
            list.add(id);
            mp.put(idList,0.5 * ((double) idList.size()/ConfigPara.funcFlagArray.length) + 0.5 * sumCost);
            return mp;
        }
        Double min = Double.MAX_VALUE;
        Set<Integer> bestList = new HashSet<>();
        for (int i = 0; i < ConfigPara.funcFlagArray.length; i++) {
            double cost = 0.0;
            if(!idList.contains(i))
            {
                Set<Integer> list = new HashSet<>(idList);
                list.add(id);
                Map<Set<Integer>,Double> mp1 = new HashMap<>();
                mp1 = DFSFunction(list,i,capacity - ConfigPara.funcCapacity[id],sumCost + ConfigPara.costNum[i]);
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
            }
        }
        mp.put(bestList,min);
        return mp;
    }

    public static void run(Integer sid) {
        System.out.println("毕设" + AbstractJobDriver.FuncName[sid - 1]);
        ConfigPara.invokeTime[sid - 1]++;
        //cost[sid - 1] = ConfigPara.invokeTime[sid - 1] * ConfigPara.initTime[sid - 1] / ConfigPara.funcCapacity[sid - 1];
        if(ConfigPara.funcFlagArray[sid - 1] == 0) { //若没有预热容器则需要进行冷启动
            if(ConfigPara.funcCapacity[sid - 1] > ConfigPara.getRemainMemCapacity()) {
                int kp = 0;
                int invokeTime = 0;
                for (int j = 0; j < ConfigPara.funcFlagArray.length; j++) {
                    if (ConfigPara.invokeTime[j] > invokeTime) {
                        invokeTime = ConfigPara.invokeTime[j];
                    }
                    if (ConfigPara.kpArray[j] > kp) {
                        kp = ConfigPara.kpArray[j];
                    }
                }
                for (int i = 0; i < ConfigPara.funcFlagArray.length; i++) {
                    ConfigPara.costNum[i] = 0.5 * (ConfigPara.invokeTime[i] / invokeTime) + 0.5 * (ConfigPara.kpArray[i] / kp); //计算每个函数容器的释放代价
                }

                double min = Double.MAX_VALUE;
                Set<Integer> bestList = new HashSet<>();
                for (int i = 0; i < ConfigPara.funcFlagArray.length; i++) {
                    double cost = 0.0;
                    Set<Integer> list = new HashSet<>();
                    Map<Set<Integer>, Double> mp1 = new HashMap<>();
                    mp1 = DFSFunction(list, i, ConfigPara.funcCapacity[sid - 1], ConfigPara.costNum[i]);
                    Set<Integer> list1 = new HashSet<>();
                    for (Map.Entry<Set<Integer>, Double> entry : mp1.entrySet()) {
                        cost = entry.getValue();
                        list1.addAll(entry.getKey());
                    }
                    if (cost < min) {
                        min = cost;
                        bestList.clear();
                        bestList.addAll(list1);
                    }
                }
                for (int i = 0; i < ConfigPara.funcFlagArray.length; i++) {
                    if (bestList.contains(i))
                        System.out.println(i + "-----------release-----------");
                    OperWaitQueue.releaseFunc(i + 1);
                }
            }
        }
        OperWaitQueue.execFuncZyy(sid);
        printMemory();
    }

}
