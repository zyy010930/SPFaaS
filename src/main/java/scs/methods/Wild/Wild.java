package scs.methods.Wild;

import scs.controller.ConfigPara;
import scs.util.repository.Repository;

import java.util.Comparator;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class Wild {
    public Wild(){}

    public static void run(Integer serviceId) throws InterruptedException {
//        Repository.loaderMap.get(id).getAbstractJobDriver().executeJob(id, 3);
        System.out.println(ConfigPara.funcName[serviceId-1] + " request");
        //FunctionExec functionExec = new FunctionExec(httpClient, queryItemsStr, serviceId, jsonParmStr, sleepUnit, "POST");

        if(ConfigPara.funcFlagArray[serviceId-1] == 0) {
            ConfigPara.getRemainMemCapacity();
            ConfigPara.funcFlagArray[serviceId-1] = 2;
            ConfigPara.coldStartTime[serviceId-1]++;
            System.out.println(ConfigPara.funcName[serviceId-1] + " cold start time is " + ConfigPara.coldStartTime[serviceId-1]);
        }
        else
            ConfigPara.funcFlagArray[serviceId-1] = 2;

        if(!ConfigPara.start[serviceId-1])
        {
            ConfigPara.oldTime[serviceId-1] = new Date().getTime();
            ConfigPara.start[serviceId-1] = true;
        }else {
            long nowTime = new Date().getTime();
            long t = nowTime - ConfigPara.oldTime[serviceId-1];
            if(t >= 7200000)
            {
                ConfigPara.outOfBound[serviceId-1]++;
            }
            ConfigPara.timeList.get(serviceId-1).add(t);
            ConfigPara.oldTime[serviceId-1] = nowTime;
            if(ConfigPara.mean[serviceId-1] == 0)
            {
                ConfigPara.mean[serviceId-1] = (double)t;
            }else{
                int len = ConfigPara.timeList.get(serviceId-1).size();
                double oldMean = ConfigPara.mean[serviceId-1];
                int invokeTime = ConfigPara.invokeTime[serviceId-1];
                ConfigPara.mean[serviceId-1] = oldMean + ((double)t - oldMean)/len;
                ConfigPara.standard[serviceId-1] = ConfigPara.standard[serviceId-1] + (t - oldMean)*(t - ConfigPara.mean[serviceId-1]);
                ConfigPara.cv[serviceId-1] = ConfigPara.standard[serviceId-1]/ConfigPara.mean[serviceId-1]/60000.0/ConfigPara.invokeTime[serviceId-1];

                if(len >= 20 && ConfigPara.cv[serviceId-1] <= 2.0) //样本数目足够且直方图具有代表性，采用5%和99%的样本点
                {
                    ConfigPara.preWarm[serviceId-1] = (double)ConfigPara.timeList.get(serviceId-1).get(Math.min(len - 1,((int)(len*0.05) - 1)));
                    ConfigPara.keepAlive[serviceId-1] = (int)(double)ConfigPara.timeList.get(serviceId-1).get(Math.max(0,((int)(len*0.99) - 1)));
                } else if((double)ConfigPara.outOfBound[serviceId-1]/ConfigPara.invokeTime[serviceId-1] >= 0.5)
                {
//						ArrayList<Double> arimaList = ARIMAReader.arimaList.get(serviceId);
//						ConfigPara.preWarm[serviceId-1] = arimaList.get(invokeTime)*0.85;
//						ConfigPara.kpArray[serviceId-1] = (int)(arimaList.get(invokeTime)*0.3);
                    ConfigPara.preWarm[serviceId-1] = 0.0;
                    ConfigPara.keepAlive[serviceId-1] = 600000;
                }
                else { //样本不足或者直方图不具有代表性，pre-warm设置为0，keep-alive设置一个较长时间
                    ConfigPara.preWarm[serviceId-1] = 0.0;
                    ConfigPara.keepAlive[serviceId-1] = 600000;
                }
            }
        }
        ConfigPara.timeList.get(serviceId-1).sort(Comparator.naturalOrder());

        ConfigPara.invokeTime[serviceId-1]++;
        ConfigPara.funcFlagArray[serviceId-1] = 1;
        ConfigPara.getRemainMemCapacity();

        Thread.sleep((long) ConfigPara.preWarm[serviceId-1]);
        if (ConfigPara.funcFlagArray[serviceId-1] == 0) {
            ConfigPara.funcFlagArray[serviceId - 1] = 1;
            ConfigPara.getRemainMemCapacity();
        }
        int lastTime = ConfigPara.invokeTime[serviceId-1];
        Thread.sleep((long) ConfigPara.keepAlive[serviceId-1]);
        if(ConfigPara.funcFlagArray[serviceId-1] == 1 && ConfigPara.invokeTime[serviceId-1] == lastTime)
        {
            ConfigPara.funcFlagArray[serviceId-1] = 0;
            ConfigPara.getRemainMemCapacity();
        }

//        if(ConfigPara.preWarm[serviceId-1] != 0.0) {
//            Timer timer1 = new Timer();
//            TimerTask timerTask1 = new TimerTask() {
//                @Override
//                public void run() {
//                    if (ConfigPara.funcFlagArray[serviceId-1] == 0) {
//                        ConfigPara.funcFlagArray[serviceId - 1] = 1;
//                        ConfigPara.getRemainMemCapacity();
//                    }
//                }
//            };
//            timer1.schedule(timerTask1, (long) ConfigPara.preWarm[serviceId-1]);
//        }
//
//        Timer timer = new Timer();
//        int lastTime = ConfigPara.invokeTime[serviceId-1];
//        TimerTask timerTask = new TimerTask() {
//            @Override
//            public void run() {
//                if(ConfigPara.funcFlagArray[serviceId-1] == 1 && ConfigPara.invokeTime[serviceId-1] == lastTime)
//                {
//                    ConfigPara.funcFlagArray[serviceId-1] = 0;
//                    ConfigPara.getRemainMemCapacity();
//                }
//            }
//        };
//        timer.schedule(timerTask, (long) ConfigPara.keepAlive[serviceId-1]);
    }

}
