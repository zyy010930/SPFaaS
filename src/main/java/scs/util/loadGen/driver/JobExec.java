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

    public void ZyyExec(int serviceId) throws InterruptedException {
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
            HttpClientPool.getResponseTime(httpClient, url);
            System.out.println(ConfigPara.funcName[serviceId-1] + " cold start time is " + ConfigPara.coldStartTime[serviceId-1]);
        }
        else {
            ConfigPara.funcFlagArray[serviceId - 1] = 2;
            System.out.println("目前大小：" + ConfigPara.getRemainMemCapacity());
        }

        ConfigPara.keepAlive[serviceId-1] = 600000.0;

        int time= HttpClientPool.getResponseTime(httpClient, url);
        System.out.println(time);
        //ConfigPara.funcFlagArray[serviceId-1] = 1;
        ConfigPara.invokeTime[serviceId-1]++;
        System.out.println(ConfigPara.funcName[serviceId-1] + " Invoke time is " + ConfigPara.invokeTime[serviceId-1] + ", cold start time is " + ConfigPara.coldStartTime[serviceId-1] + ", cold start rate is " + ((double)ConfigPara.coldStartTime[serviceId-1]/ConfigPara.invokeTime[serviceId-1])*100.0 + "%, preWarm time is " + ConfigPara.preWarm[serviceId-1] + ", keepAive time is " + ConfigPara.keepAlive[serviceId-1]);
        ConfigPara.funcFlagArray[serviceId-1] = 1;

        int lastTime = ConfigPara.invokeTime[serviceId-1];
        Thread.sleep((long) ConfigPara.keepAlive[serviceId-1]);
        if(ConfigPara.funcFlagArray[serviceId-1] == 1 && ConfigPara.invokeTime[serviceId-1] == lastTime) //如果期间没有新的调用，就直接释放
        {
            HttpClientPool.getResponseTime(httpClient, url0);
            ConfigPara.funcFlagArray[serviceId-1] = 0;
            ConfigPara.getRemainMemCapacity();
            System.out.println(ConfigPara.funcName[serviceId-1] + " keepAlive over. keepalive is " + ConfigPara.keepAlive[serviceId-1]);
        }

    }
}
