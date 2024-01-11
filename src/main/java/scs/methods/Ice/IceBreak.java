package scs.methods.Ice;

import scs.controller.ConfigPara;

import java.util.Date;

public class IceBreak {
    public IceBreak(){}

    public static void run(Integer serviceId) throws InterruptedException {
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

        ConfigPara.keepAlive[serviceId-1] = 600000.0;

        //int time= HttpClientPool.getResponseTime(httpClient, url);
        //System.out.println(time);
        //ConfigPara.funcFlagArray[serviceId-1] = 1;
        ConfigPara.invokeTime[serviceId-1]++;
        System.out.println(ConfigPara.funcName[serviceId-1] + " Invoke time is " + ConfigPara.invokeTime[serviceId-1] + ", cold start time is " + ConfigPara.coldStartTime[serviceId-1] + ", cold start rate is " + ((double)ConfigPara.coldStartTime[serviceId-1]/ConfigPara.invokeTime[serviceId-1])*100.0 + "%, preWarm time is " + ConfigPara.preWarm[serviceId-1] + ", keepAive time is " + ConfigPara.keepAlive[serviceId-1]);
        ConfigPara.funcFlagArray[serviceId-1] = 1;

        int lastTime = ConfigPara.invokeTime[serviceId-1];
        Thread.sleep((long) ConfigPara.keepAlive[serviceId-1]);
        if(ConfigPara.funcFlagArray[serviceId-1] == 1 && ConfigPara.invokeTime[serviceId-1] == lastTime) //如果期间没有新的调用，就直接释放
        {
            ConfigPara.funcFlagArray[serviceId-1] = 0;
            ConfigPara.getRemainMemCapacity();
            System.out.println(ConfigPara.funcName[serviceId-1] + " keepAlive over. keepalive is " + ConfigPara.keepAlive[serviceId-1]);
        }
    }
}
