package scs.methods.LCS;

import scs.controller.ConfigPara;
import scs.controller.OperWaitQueue;

public class Lcs {
    public Lcs() {}

    public static void run(int serviceId) throws InterruptedException {
        //ConfigPara.invokeTime[serviceId - 1]++;
        ConfigPara.priory[serviceId - 1] = ConfigPara.invokeTime[serviceId - 1]/2.0;
        if(ConfigPara.waitQueue.size() != 0) {
            if (ConfigPara.funcFlagArray[serviceId - 1] == 0) {
                Double pri = Double.MAX_VALUE;
                Integer tempSid = 0;
                System.out.println(serviceId + "-------------waitQueue not empty,Add waitQueue-----------");
                ConfigPara.waitQueue.add(serviceId);
                for(int i = 0; i < 300; i++) {
                    if(ConfigPara.funcFlagArray[i] == 1 && ConfigPara.priory[i] < pri) {
                        pri = ConfigPara.priory[i];
                        tempSid = i + 1;
                    }
                }
                if(tempSid != 0) {
                    System.out.println(serviceId + "-----------release-----------");
                    //OperWaitQueue.releaseFunc(tempSid);
                    ConfigPara.funcFlagArray[serviceId - 1] = 0;

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
                OperWaitQueue.execFunc(serviceId);
            }
        }
        else {
            if(ConfigPara.funcCapacity[serviceId-1] <= ConfigPara.getRemainMemCapacity()) {
                System.out.println(serviceId + "show capacity: " + ConfigPara.funcCapacity[serviceId-1] + " " + ConfigPara.getRemainMemCapacity());
                OperWaitQueue.execFunc(serviceId);
            }
            else {
                System.out.println(serviceId + "-------------capacity not enough,Add waitQueue-----------");
                ConfigPara.waitQueue.add(serviceId);
            }
        }
    }
}
