package scs.methods.ZyyCache;

import scs.controller.ConfigPara;
import scs.controller.OperWaitQueue;
import scs.util.loadGen.driver.AbstractJobDriver;

import java.util.Queue;

public class Hybrid {
    public static Double[] priority = new Double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,0.0,0.0};
    public Hybrid(){}

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

    public static void run(Integer sid) {
        System.out.println("毕设" + AbstractJobDriver.FuncName[sid - 1]);
        ConfigPara.invokeTime[sid - 1]++;
        priority[sid - 1] = ConfigPara.invokeTime[sid - 1] * ConfigPara.initTime[sid - 1] / ConfigPara.funcCapacity[sid - 1];
        if(ConfigPara.funcFlagArray[sid - 1] == 0) {
            Double pri = Double.MAX_VALUE;
            Integer tempSid = 0;
            while (ConfigPara.funcCapacity[sid - 1] > ConfigPara.getRemainMemCapacity()) {
                System.out.println("开始查询符合的容器");
                for (int i = 0; i < ConfigPara.funcFlagArray.length; i++) {
                    if (priority[i] < pri && ConfigPara.funcFlagArray[i] == 1) {
                        pri = priority[i];
                        tempSid = i + 1;
                    }
                }
                if (tempSid != 0) {
                    System.out.println(tempSid + "-----------release-----------");
                    OperWaitQueue.releaseFunc(tempSid);
                }
            }
            System.out.println("释放内存给:" + AbstractJobDriver.FuncName[sid - 1]);
            //ConfigPara.setMemoryCapacity(ConfigPara.getRemainMemCapacity() - ConfigPara.funcCapacity[sid - 1]);
        }
        OperWaitQueue.execFuncHybrid(sid);
        printMemory();
    }
}
