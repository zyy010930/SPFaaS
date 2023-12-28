package scs.util.loadGen.driver;

import org.apache.http.impl.client.CloseableHttpClient;
import scs.controller.ConfigPara;
import scs.pojo.FunctionList;
import scs.util.loadGen.threads.FunctionExec;
import scs.util.repository.Repository;
import scs.util.tools.ARIMAReader;
import scs.util.tools.HttpClientPool;

import java.util.*;

import static scs.methods.Zyy.ZyyFaaS.DFSFunction;

public class JobExec {

    public void ZyyExec(int serviceId)
    {
        CloseableHttpClient httpClient;
        String url = "http://192.168.1.7:31112/function/func"+serviceId;
        String url0 = "http://192.168.1.7:31112/zero/func"+serviceId;
        httpClient= HttpClientPool.getInstance().getConnection();
        System.out.println(ConfigPara.funcName[serviceId-1] + " request");

        if(ConfigPara.firstTime[serviceId-1] == 0){
            ConfigPara.firstTime[serviceId-1] = new Date().getTime();
        }
        if(ConfigPara.funcFlagArray[serviceId-1] == 0) {
            ConfigPara.funcFlagArray[serviceId-1] = 2;
            ConfigPara.coldStartTime[serviceId-1]++;
            System.out.println("目前大小：" + ConfigPara.getRemainMemCapacity());
            //HttpClientPool.getResponseTime(httpClient, url);
            System.out.println(ConfigPara.funcName[serviceId-1] + " cold start time is " + ConfigPara.coldStartTime[serviceId-1]);
        }
        else {
            ConfigPara.funcFlagArray[serviceId - 1] = 2;
            System.out.println("目前大小：" + ConfigPara.getRemainMemCapacity());
        }

//        if(!ConfigPara.start[serviceId-1])
//        {
//            ConfigPara.oldTime[serviceId-1] = new Date().getTime();
//            ConfigPara.start[serviceId-1] = true;
//        }else {
//            ConfigPara.nowTime[serviceId-1] = new Date().getTime();
//            int intervalTime = (int) ((ConfigPara.nowTime[serviceId-1] - ConfigPara.firstTime[serviceId-1]) / 1000); //add
//            ArrayList<Integer> preList = ARIMAReader.predictList.get(serviceId);
//            System.out.println(preList.size() + "and" + ConfigPara.invokeTime[serviceId-1]);
//            for(int i = intervalTime + 1; i < preList.size();i++)
//            {
//                if(preList.get(i) != 0)
//                {
//                    ConfigPara.preWarm[serviceId-1] = (i - intervalTime - 1) * 1000.0;
//                }
//            }
//        }
        ConfigPara.keepAlive[serviceId-1] = 600000.0;

        //int time= HttpClientPool.getResponseTime(httpClient, url);
        //System.out.println(time);
        //ConfigPara.funcFlagArray[serviceId-1] = 1;
        ConfigPara.invokeTime[serviceId-1]++;
        System.out.println(ConfigPara.funcName[serviceId-1] + " Invoke time is " + ConfigPara.invokeTime[serviceId-1] + ", cold start time is " + ConfigPara.coldStartTime[serviceId-1] + ", cold start rate is " + ((double)ConfigPara.coldStartTime[serviceId-1]/ConfigPara.invokeTime[serviceId-1])*100.0 + "%, preWarm time is " + ConfigPara.preWarm[serviceId-1] + ", keepAive time is " + ConfigPara.keepAlive[serviceId-1]);
        ConfigPara.funcFlagArray[serviceId-1] = 1;

//        if(ConfigPara.preWarm[serviceId-1] != 0.0) {
//            Date now1 = new Date();
//            Date preWarmTime = new Date(now1.getTime() + (long) ConfigPara.preWarm[serviceId-1]);
//            FunctionList.preMap.put(serviceId, preWarmTime);
//            Timer timer1 = new Timer();
//            TimerTask timerTask1 = new TimerTask() {
//                @Override
//                public void run() {
//                    Date now = new Date();
//                    System.out.println("prewarm start!!!!!!!!! " + ConfigPara.funcFlagArray[serviceId-1]);
//                    if (ConfigPara.funcFlagArray[serviceId-1] == 0) {
//                        FunctionList.funcMap.put(serviceId, true);
//                        System.out.println(ConfigPara.funcName[serviceId-1] + " prewarm now. pre-warm is " + ConfigPara.preWarm[serviceId-1]);
//                        if(ConfigPara.funcCapacity[serviceId - 1] > ConfigPara.getRemainMemCapacity()) {
//                            ConfigPara.containerRelease(serviceId); //替换容器
//                        }
//                        //ConfigPara.setMemoryCapacity(ConfigPara.getRemainMemCapacity() - ConfigPara.funcCapacity[serviceId-1]);
//                        ConfigPara.funcFlagArray[serviceId - 1] = 1;
//                        ConfigPara.getRemainMemCapacity();
//                        //HttpClientPool.getResponseTime(httpClient, url);
//                        //System.out.println(tool.exec(createCmd[serviceId-1]));
//                    }
//                }
//            };
//            timer1.schedule(timerTask1, (long) ConfigPara.preWarm[serviceId-1]);
//        }

        Date now = new Date();
        Date deleteTime = new Date(now.getTime() + (long) ConfigPara.keepAlive[serviceId-1]);
        FunctionList.timeMap.put(serviceId, deleteTime);
        Timer timer = new Timer();
        int lastTime = ConfigPara.invokeTime[serviceId-1];
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                Date now = new Date();
                System.out.println("delete start!!!!!!!!! " + ConfigPara.funcFlagArray[serviceId-1]);
                if(ConfigPara.funcFlagArray[serviceId-1] == 1 && ConfigPara.invokeTime[serviceId-1] == lastTime) //如果期间没有新的调用，就直接释放
                {
                    //ConfigPara.setMemoryCapacity(ConfigPara.getRemainMemCapacity() + ConfigPara.funcCapacity[serviceId-1]);
                    ConfigPara.funcFlagArray[serviceId-1] = 0;
                    ConfigPara.getRemainMemCapacity();
                    System.out.println(ConfigPara.funcName[serviceId-1] + " keepAlive over. keepalive is " + ConfigPara.keepAlive[serviceId-1]);
                    //HttpClientPool.getResponseTime(httpClient, url0);
                }
            }
        };
        timer.schedule(timerTask, (long) ConfigPara.keepAlive[serviceId-1]);

    }
}
