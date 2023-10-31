package scs.methods.LRFU;

import scs.controller.ConfigPara;
import scs.controller.OperWaitQueue;
import scs.util.repository.Repository;

import java.util.LinkedList;
import java.util.Queue;

/**
 * @ClassName LFU
 * @Description ...
 * @Author @WZhang
 * @Date 2023/3/8 9:52
 * @Version 1.0
 */
public class LFU {
    private static Integer[] funcFreqArray = new Integer[]{0, 0, 0, 0, 0, 0, 0};

    public LFU() {
        //funcFreqArray = new Integer[]{0, 0, 0, 0, 0, 0, 0};
    }

    public static void run(Integer sid) {
        if(ConfigPara.waitQueue.size() != 0) {
            if(ConfigPara.funcFlagArray[sid-1] == 0) {
                Integer tempFreq = 0;
                Integer tempSid = 0;

                ConfigPara.waitQueue.add(sid);
                for(int i = 0; i < ConfigPara.funcFlagArray.length; i++) {
                    if(ConfigPara.funcFlagArray[i] == 1 && tempFreq < funcFreqArray[i]) {
                        tempFreq = funcFreqArray[i];
                        tempSid = i + 1;
                    }
                }
                if(tempSid != 0) {
                    OperWaitQueue.releaseFunc(tempSid);

                    for(int j = 0; j < ConfigPara.waitQueue.size(); j++) {
                        if(ConfigPara.funcCapacity[ConfigPara.waitQueue.peek()-1] <= ConfigPara.getRemainMemCapacity()) {
                            Integer tempSid1 = ConfigPara.waitQueue.poll();
                            OperWaitQueue.execFunc(tempSid1);
                            funcFreqArray[tempSid1-1] ++;
                        } else {
                            break;
                        }
                    }
                }

            }
            else {
                OperWaitQueue.execFunc(sid);
                funcFreqArray[sid-1] ++;
            }
        }
        else {
            if(ConfigPara.funcCapacity[sid-1] <= ConfigPara.getRemainMemCapacity()) {
                OperWaitQueue.execFunc(sid);
                funcFreqArray[sid-1] ++;
            }
            else {
                ConfigPara.waitQueue.add(sid);
            }
        }
    }

    public Integer maxTwoNum(Integer i1, Integer i2) {
        if(i1 < i2) {
            return i2;
        } else {
            return i1;
        }
    }

}
