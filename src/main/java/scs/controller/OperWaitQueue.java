package scs.controller;

import org.apache.http.impl.client.CloseableHttpClient;
import scs.util.loadGen.threads.FunctionExec;
import scs.util.repository.Repository;
import scs.util.tools.HttpClientPool;
import scs.util.tools.SSHTool;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.Queue;

/**
 * @ClassName OperWaitQueue
 * @Description ...
 * @Author @WZhang
 * @Date 2023/3/7 10:22
 * @Version 1.0
 */
public class OperWaitQueue {
    private static SSHTool tool1 = new SSHTool("192.168.1.4", "root", "wnlof309b507", StandardCharsets.UTF_8);

    public static void execQueueFunc() {
        if(ConfigPara.waitQueue.size() != 0) {
            for(int i = 0; i < ConfigPara.funcFlagArray.length; i++) {
                if(ConfigPara.funcFlagArray[i] == 1) {
                    releaseFunc(i+1);
                }
            }
            for(int j = 0; j < ConfigPara.waitQueue.size(); j++) {
                if(ConfigPara.funcCapacity[ConfigPara.waitQueue.peek()-1] <= ConfigPara.getRemainMemCapacity()) {
                    Integer sid = ConfigPara.waitQueue.poll();
                    ConfigPara.setMemoryCapacity(ConfigPara.getRemainMemCapacity() - ConfigPara.funcCapacity[sid-1]);
                    //ConfigPara.funcFlagArray[sid-1] = 2;
                    Repository.loaderMap.get(sid).getAbstractJobDriver().executeJob(sid,0);
                } else {
                    break;
                }
            }
        }
    }

    public static void execRequests(Integer serviceId) {
        if(ConfigPara.waitQueue.size() != 0) {
            ConfigPara.waitQueue.add(serviceId);
        }else {
            //Repository.loaderMap.get(serviceId).getAbstractJobDriver().executeJob(serviceId);
            execFunc(serviceId);
        }
    }

    public static void execFunc(Integer sid) {
        Repository.loaderMap.get(sid).getAbstractJobDriver().executeJob(sid,0);
    }

    public static void execFuncHybrid(Integer sid){
//        if(ConfigPara.funcFlagArray[sid-1] == 0) {
//            ConfigPara.setMemoryCapacity(ConfigPara.getRemainMemCapacity() - ConfigPara.funcCapacity[sid - 1]);
//            System.out.println("目前大小：" + ConfigPara.getRemainMemCapacity());
//        }
        Repository.loaderMap.get(sid).getAbstractJobDriver().exec(sid);
    }

    public static void execFuncZyy(Integer sid){
        Repository.loaderMap.get(sid).getAbstractJobDriver().ZyyExec(sid);
    }

    public static void releaseFunc(Integer sid) {
        switch (sid){
            case 1:
                //System.out.println(tool1.exec("bash /home/zyy/INFless/developer/servingFunctions/resnet-50.sh"));
                ConfigPara.setMemoryCapacity(ConfigPara.getRemainMemCapacity() + ConfigPara.funcCapacity[sid-1]);
                ConfigPara.funcFlagArray[sid-1] = 0;
                break;
            case 2:
                //System.out.println(tool1.exec("bash /home/zyy/INFless/developer/servingFunctions/mobilenet.sh"));
                ConfigPara.setMemoryCapacity(ConfigPara.getRemainMemCapacity() + ConfigPara.funcCapacity[sid-1]);
                ConfigPara.funcFlagArray[sid-1] = 0;
                break;
            case 3:
                //System.out.println(tool1.exec("bash /home/zyy/BurstyServerlessBenchmark/DIC/WebServices/openfaas/python-code/hash.sh"));
                ConfigPara.setMemoryCapacity(ConfigPara.getRemainMemCapacity() + ConfigPara.funcCapacity[sid-1]);
                ConfigPara.funcFlagArray[sid-1] = 0;
                break;
            case 4:
                //System.out.println(tool1.exec("bash /home/zyy/BurstyServerlessBenchmark/DIC/WebServices/openfaas/python-code/Md5.sh"));
                ConfigPara.setMemoryCapacity(ConfigPara.getRemainMemCapacity() + ConfigPara.funcCapacity[sid-1]);
                ConfigPara.funcFlagArray[sid-1] = 0;
                break;
            case 5:
                //System.out.println(tool1.exec("bash /home/zyy/BurstyServerlessBenchmark/DIC/WebServices/openfaas/python-code/hello.sh"));
                ConfigPara.setMemoryCapacity(ConfigPara.getRemainMemCapacity() + ConfigPara.funcCapacity[sid-1]);
                ConfigPara.funcFlagArray[sid-1] = 0;
                break;
            case 6:
                //System.out.println(tool1.exec("bash /home/zyy/BurstyServerlessBenchmark/DIC/WebServices/openfaas/python-code/sort.sh"));
                ConfigPara.setMemoryCapacity(ConfigPara.getRemainMemCapacity() + ConfigPara.funcCapacity[sid-1]);
                ConfigPara.funcFlagArray[sid-1] = 0;
                break;
            case 7:
                //System.out.println(tool1.exec("bash /home/zyy/BurstyServerlessBenchmark/DIC/WebServices/openfaas/python-code/cryptography.sh"));
                ConfigPara.setMemoryCapacity(ConfigPara.getRemainMemCapacity() + ConfigPara.funcCapacity[sid-1]);
                ConfigPara.funcFlagArray[sid-1] = 0;
                break;
            case 8:
            case 9:
            case 10:
            case 11:
            case 12:
            case 13:
            case 14:
            case 15:
            case 16:
                //System.out.println(tool1.exec("bash /home/zyy/test.sh"));
                ConfigPara.setMemoryCapacity(ConfigPara.getRemainMemCapacity() + ConfigPara.funcCapacity[sid-1]);
                ConfigPara.funcFlagArray[sid-1] = 0;
                break;
            default:
                System.out.println("Function Release Error!");
        }
    }

    public void deleteQueueNode(Queue<Integer> Q, Integer sid) {
        Queue<Integer> tempQ = new LinkedList<>();
        int j = 0;

        if(Q.size() != 0 && Q.contains(sid)) {
            for (Integer i : Q) {
                if(i == sid) {
                    break;
                }
                j = j + 1;
            }
        }

        if(Q.size() != 0) {
            int k = 0;

            while(k != j) {
                tempQ.add(Q.poll());
                k = k + 1;
            }
            Q.poll();
            while(k < Q.size()) {
                tempQ.add(Q.poll());
                k = k + 1;
            }

            while(k != 0) {
                Q.add(tempQ.poll());
                k = k - 1;
            }
        }
    }
}
