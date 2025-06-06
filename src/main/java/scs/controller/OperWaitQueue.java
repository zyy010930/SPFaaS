package scs.controller;

import org.apache.http.impl.client.CloseableHttpClient;
import scs.util.loadGen.driver.JobExec;
import scs.util.loadGen.threads.FunctionExec;
import scs.util.repository.Repository;
import scs.util.tools.HttpClientPool;
import scs.util.tools.SSHTool;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @ClassName OperWaitQueue
 * @Description ...
 * @Author @WZhang
 * @Date 2023/3/7 10:22
 * @Version 1.0
 */
public class OperWaitQueue {
    private static SSHTool tool1 = new SSHTool("192.168.1.7", "root", "wnlof309b507", StandardCharsets.UTF_8);

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

    public static void execRequests(Integer serviceId) throws InterruptedException {
        if(ConfigPara.waitQueue.size() != 0) {
            ConfigPara.waitQueue.add(serviceId);
        }else {
            //Repository.loaderMap.get(serviceId).getAbstractJobDriver().executeJob(serviceId);
            execFunc(serviceId);
        }
    }

    public static void execFunc(Integer serviceId) throws InterruptedException {
        System.out.println(ConfigPara.funcName[serviceId-1] + " request");

        if(ConfigPara.funcFlagArray[serviceId-1] == 0) {
            ConfigPara.getRemainMemCapacity();
            ConfigPara.funcFlagArray[serviceId-1] = 2;
            ConfigPara.coldStartTime[serviceId-1]++;
            System.out.println(ConfigPara.funcName[serviceId-1] + " cold start time is " + ConfigPara.coldStartTime[serviceId-1]);
        }
        else
            ConfigPara.funcFlagArray[serviceId-1] = 2;

        ConfigPara.invokeTime[serviceId-1]++;
        ConfigPara.funcFlagArray[serviceId-1] = 1;
        ConfigPara.getRemainMemCapacity();

        int lastTime = ConfigPara.invokeTime[serviceId-1];
        Thread.sleep(600000);
        if(ConfigPara.funcFlagArray[serviceId-1] == 1 && ConfigPara.invokeTime[serviceId-1] == lastTime)
        {
            ConfigPara.funcFlagArray[serviceId-1] = 0;
            ConfigPara.getRemainMemCapacity();
        }
    }

    public static void execFuncHybrid(Integer sid){
        Repository.loaderMap.get(sid).getAbstractJobDriver().exec(sid);
    }

    public static void execFuncZyy(Integer sid) throws InterruptedException {
        JobExec jobExec = new JobExec();
        jobExec.ZyyExec(sid);
        //Repository.loaderMap.get(sid).getAbstractJobDriver().ZyyExec(sid);
    }

    public static void execFuncZyyMulti(Integer sid) throws InterruptedException {
        JobExec jobExec = new JobExec();
        jobExec.ZyyExecMulti(sid);
        //Repository.loaderMap.get(sid).getAbstractJobDriver().ZyyExec(sid);
    }

    public static void releaseFunc(Integer sid) {
        CloseableHttpClient httpClient;
        String url = "http://192.168.1.7:31112/function/func"+sid;
        httpClient= HttpClientPool.getInstance().getConnection();
        //HttpClientPool.getResponseTime(httpClient,url);
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
