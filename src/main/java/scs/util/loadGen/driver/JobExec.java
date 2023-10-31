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
        System.out.println(ConfigPara.funcName[serviceId-1] + " request");
        //FunctionExec functionExec = new FunctionExec(httpClient, queryItemsStr, serviceId, jsonParmStr, sleepUnit, "POST");

        if(ConfigPara.firstTime[serviceId-1] == 0){
            ConfigPara.firstTime[serviceId-1] = new Date().getTime();
        }
        if(ConfigPara.funcFlagArray[serviceId-1] == 0) {
            System.out.println("目前大小：" + ConfigPara.getRemainMemCapacity());
            ConfigPara.setMemoryCapacity(ConfigPara.getRemainMemCapacity() - ConfigPara.funcCapacity[serviceId - 1]);
            System.out.println("目前大小：" + ConfigPara.getRemainMemCapacity());
            ConfigPara.funcFlagArray[serviceId-1] = 2;
            ConfigPara.coldStartTime[serviceId-1]++;
            //System.out.println(tool.exec(createCmd[serviceId-1]));
            System.out.println(ConfigPara.funcName[serviceId-1] + " cold start time is " + ConfigPara.coldStartTime[serviceId-1]);
        }
        else
            ConfigPara.funcFlagArray[serviceId-1] = 2;

        if(!ConfigPara.start[serviceId-1])
        {
            ConfigPara.oldTime[serviceId-1] = new Date().getTime();
            ConfigPara.start[serviceId-1] = true;
        }else {
            ConfigPara.nowTime[serviceId-1] = new Date().getTime();
            int intervalTime = (int) ((ConfigPara.nowTime[serviceId-1] - ConfigPara.firstTime[serviceId-1]) / 1000); //add
            ArrayList<Integer> preList = ARIMAReader.predictList.get(serviceId);
            System.out.println(preList.size() + "and" + ConfigPara.invokeTime[serviceId-1]);
            for(int i = intervalTime + 1; i < preList.size();i++)
            {
                if(preList.get(i) != 0)
                {
                    ConfigPara.preWarm[serviceId-1] = (i - intervalTime - 1) * 1000.0;
                }
            }
            ConfigPara.keepAlive[serviceId-1] = 1200000.0;
        }

        //ConfigPara.kpArray[serviceId-1] = (int)keepAlive;        //Setting the keep-alive
        //ConfigPara.funcFlagArray[serviceId-1] = 2;
        CloseableHttpClient httpClient;
        String queryItemsStr = Repository.HashFaasBaseURL;
        String jsonParmStr;
        httpClient= HttpClientPool.getInstance().getConnection();
        jsonParmStr=Repository.resNet50ParmStr;
        queryItemsStr=queryItemsStr.replace("Ip","192.168.1.7");
        queryItemsStr=queryItemsStr.replace("Port","31112");
        queryItemsStr=queryItemsStr.replace("Hash",ConfigPara.funcName[serviceId-1]);
        int time= HttpClientPool.postResponseTime(httpClient, queryItemsStr, jsonParmStr);
        System.out.println(time);
        //ConfigPara.funcFlagArray[serviceId-1] = 1;
        ConfigPara.invokeTime[serviceId-1]++;
        System.out.println(ConfigPara.funcName[serviceId-1] + " Invoke time is " + ConfigPara.invokeTime[serviceId-1] + ", cold start time is " + ConfigPara.coldStartTime[serviceId-1] + ", cold start rate is " + ((double)ConfigPara.coldStartTime[serviceId-1]/ConfigPara.invokeTime[serviceId-1])*100.0 + "%, preWarm time is " + ConfigPara.preWarm[serviceId-1] + ", keepAive time is " + ConfigPara.keepAlive[serviceId-1]);
        ConfigPara.funcFlagArray[serviceId-1] = 1;

        if(ConfigPara.preWarm[serviceId-1] != 0.0) {
            Date now1 = new Date();
            Date preWarmTime = new Date(now1.getTime() + (long) ConfigPara.preWarm[serviceId-1]);
            FunctionList.preMap.put(serviceId, preWarmTime);
            Timer timer1 = new Timer();
            TimerTask timerTask1 = new TimerTask() {
                @Override
                public void run() {
                    Date now = new Date();
                    System.out.println("prewarm start!!!!!!!!! " + ConfigPara.funcFlagArray[serviceId-1]);
                    if (ConfigPara.funcFlagArray[serviceId-1] == 0) {
                        FunctionList.funcMap.put(serviceId, true);
                        System.out.println(ConfigPara.funcName[serviceId-1] + " prewarm now. pre-warm is " + ConfigPara.preWarm[serviceId-1]);
                        if(ConfigPara.funcCapacity[serviceId - 1] > ConfigPara.getRemainMemCapacity()) {
                            int kp = 0;
                            int invoke = 0;
                            for(int j=0;j<ConfigPara.funcFlagArray.length;j++)
                            {
                                if(ConfigPara.invokeTime[j]>invoke)
                                {
                                    invoke = ConfigPara.invokeTime[j];
                                }
                                if(ConfigPara.kpArray[j]>kp)
                                {
                                    kp = ConfigPara.kpArray[j];
                                }
                            }
                            for(int i=0;i<ConfigPara.funcFlagArray.length;i++)
                            {
                                ConfigPara.costNum[i] = 0.5*(ConfigPara.invokeTime[i]/invoke) + 0.5*(ConfigPara.kpArray[i]/kp); //计算每个函数容器的释放代价
                            }

                            Double min = Double.MAX_VALUE;
                            Set<Integer> bestList = new HashSet<>();
                            for(int i=0;i<ConfigPara.funcFlagArray.length;i++)
                            {
                                double cost = 0.0;
                                Set<Integer> list = new HashSet<>();
                                Map<Set<Integer>,Double> mp1 = new HashMap<>();
                                mp1 = DFSFunction(list,i,ConfigPara.funcCapacity[serviceId - 1],ConfigPara.costNum[i]);
                                Set<Integer> list1 = new HashSet<>();
                                for (Map.Entry<Set<Integer>,Double> entry : mp1.entrySet()) {
                                    cost = entry.getValue();
                                    list.addAll(entry.getKey());
                                }
                                if(cost < min)
                                {
                                    min = cost;
                                    bestList.clear();
                                    bestList.addAll(list1);
                                }
                            }
                            for(int i=0;i<ConfigPara.funcFlagArray.length;i++)
                            {
                                if(bestList.contains(i)) {
                                    System.out.println(i + "-----------release-----------");
                                    //System.out.println(tool.exec(deleteCmd[i]));
                                    ConfigPara.funcFlagArray[i] = 0;
                                    ConfigPara.setMemoryCapacity(ConfigPara.getRemainMemCapacity() + ConfigPara.funcCapacity[i]);
                                }
                            }
                        }
                        ConfigPara.setMemoryCapacity(ConfigPara.getRemainMemCapacity() - ConfigPara.funcCapacity[serviceId-1]);
                        ConfigPara.funcFlagArray[serviceId - 1] = 1;
                        //System.out.println(tool.exec(createCmd[serviceId-1]));
                    }
                }
            };
            timer1.schedule(timerTask1, (long) ConfigPara.preWarm[serviceId-1]);
        }

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
                if(ConfigPara.funcFlagArray[serviceId-1] == 1 && ConfigPara.invokeTime[serviceId-1] == lastTime)
                {
//						try {
//							ConfigPara.setMemoryCapacity(ConfigPara.getRemainMemCapacity() + ConfigPara.funcCapacity[serviceId-1]);
//							ConfigPara.funcFlagArray[serviceId-1] = 0;
//							System.out.println(FuncName[serviceId-1] + " keepAlive over. keepalive is " + keepAlive);
//							System.out.println(tool.exec(deleteCmd[serviceId-1]));
//						} catch (IOException e) {
//							e.printStackTrace();
//						}
                    ConfigPara.setMemoryCapacity(ConfigPara.getRemainMemCapacity() + ConfigPara.funcCapacity[serviceId-1]);
                    ConfigPara.funcFlagArray[serviceId-1] = 0;
                    System.out.println(ConfigPara.funcName[serviceId-1] + " keepAlive over. keepalive is " + ConfigPara.keepAlive[serviceId-1]);
                }
            }
        };
        timer.schedule(timerTask, (long) ConfigPara.keepAlive[serviceId-1]);

    }
}
