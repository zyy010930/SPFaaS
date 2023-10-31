package scs.methods.FaaSCache;
import scs.controller.ConfigPara;
import scs.controller.OperWaitQueue;
import java.util.Queue;
public class GreedyDual {
    private static Double[] priority = new Double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
    private static long startTime = System.currentTimeMillis();
    public GreedyDual(){}
    public static void queuePrint(Queue<Integer> queue)
    {
        for(Integer i : queue)
        {
            System.out.println(i + " ");
        }
    }
    public static void run(int sid)
    {
        ConfigPara.invokeTime[sid - 1]++;
        priority[sid - 1] = ConfigPara.invokeTime[sid - 1]*ConfigPara.initTime[sid - 1]/ConfigPara.funcCapacity[sid - 1];
        if(ConfigPara.waitQueue.size() != 0) {
            if (ConfigPara.funcFlagArray[sid - 1] == 0) {
                Double pri = Double.MAX_VALUE;
                Integer tempSid = 0;
                System.out.println(sid + "-------------waitQueue not empty,Add waitQueue-----------");
                ConfigPara.waitQueue.add(sid);
                queuePrint(ConfigPara.waitQueue);

                for(int i = 0; i < ConfigPara.funcFlagArray.length; i++) {
                    if(ConfigPara.funcFlagArray[i] == 1 && priority[i] < pri) {
                        pri = priority[i];
                        tempSid = i + 1;
                    }
                }
                if(tempSid != 0) {
                    System.out.println(sid + "-----------release-----------");
                    OperWaitQueue.releaseFunc(tempSid);

                    for(int j = 0; j < ConfigPara.waitQueue.size(); j++) {
                        if(ConfigPara.funcCapacity[ConfigPara.waitQueue.peek()-1] <= ConfigPara.getRemainMemCapacity()) {
                            Integer tempSid1 = ConfigPara.waitQueue.poll();
                            OperWaitQueue.execFunc(tempSid1);
                        } else {
                            break;
                        }
                    }
                }
            }
            else {
                System.out.println(sid + "---------------Func exec-----------------");
                OperWaitQueue.execFunc(sid);
            }
        }
        else {
            if(ConfigPara.funcFlagArray[sid - 1] != 0 || ConfigPara.funcCapacity[sid-1] <= ConfigPara.getRemainMemCapacity()) {
                System.out.println(sid + "show capacity: " + ConfigPara.funcCapacity[sid-1] + " " + ConfigPara.getRemainMemCapacity());
                OperWaitQueue.execFunc(sid);
            }
            else {
                System.out.println(sid + "-------------capacity not enough,Add waitQueue-----------");
                ConfigPara.waitQueue.add(sid);
                queuePrint(ConfigPara.waitQueue);
            }
        }
    }

}