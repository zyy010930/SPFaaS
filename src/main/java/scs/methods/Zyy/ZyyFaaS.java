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
        for(int i = 0; i < 300; i++)
        {
            if(ConfigPara.funcFlagArray[i] != 0)
                System.out.println(ConfigPara.funcName[i]);
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

    public static void run(Integer sid) throws InterruptedException {
        // ConfigPara.invokeTime[sid - 1]++;
        if(ConfigPara.funcFlagArray[sid - 1] == 0) { //若没有预热容器则需要进行冷启动
            if(ConfigPara.funcCapacity[sid - 1] > ConfigPara.getRemainMemCapacity()) {
                ConfigPara.containerRelease(sid);
            }
        }
        OperWaitQueue.execFuncZyy(sid);
        // printMemory();
    }

}
