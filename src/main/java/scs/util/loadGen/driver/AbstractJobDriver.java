package scs.util.loadGen.driver;
  
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

import org.apache.http.impl.client.CloseableHttpClient;
import scs.controller.ConfigPara;
import scs.controller.OperWaitQueue;
import scs.methods.ZyyCache.Hybrid;
import scs.pojo.FunctionList;
import scs.util.loadGen.threads.FunctionExec;
import scs.util.tools.ARIMAReader;
import scs.util.tools.SSHTool;

import static scs.methods.Zyy.ZyyFaaS.DFSFunction;


public abstract class AbstractJobDriver {

	protected List<String> queryItemsList=new ArrayList<String>();//Query word list
	protected int queryItemListSize;
	public String queryItemsStr="";//Query link
	protected String jsonParmStr="";
	
	protected Random random=new Random();  
	protected CloseableHttpClient httpClient;
	protected long oldTime;
	protected boolean start = false;
	protected ArrayList<Long> timeList = new ArrayList<>();
	protected Double standard = 0.0;
	protected Double mean = 0.0;
	protected Double cv = 0.0;
	protected double preWarm = 0.0;
	protected double keepAlive = 1200000.0;
	protected int coldStartTime = 0;
	protected int invokeTime = 0;
	protected int outOfBound = 0;
	protected int outOfBoundTime = 0;
	protected ArrayList<Double> arimaList = new ArrayList<>();
	protected ArrayList<Integer> preList = new ArrayList<>();
	protected long nowTime = 0;
	protected long firstTime = 0;
	private SSHTool tool = new SSHTool("192.168.1.4", "root", "wnlof309b507", StandardCharsets.UTF_8);

	public static String createCmd[] = new String[]{
			"bash /home/zyy/INFless/developer/servingFunctions/resnet-50-create.sh",
			"bash /home/zyy/INFless/developer/servingFunctions/mobilenet-create.sh",
			"bash /home/zyy/BurstyServerlessBenchmark/DIC/WebServices/openfaas/python-code/hash-create.sh",
			"bash /home/zyy/BurstyServerlessBenchmark/DIC/WebServices/openfaas/python-code/Md5-create.sh",
			"bash /home/zyy/BurstyServerlessBenchmark/DIC/WebServices/openfaas/python-code/hello-create.sh",
			"bash /home/zyy/BurstyServerlessBenchmark/DIC/WebServices/openfaas/python-code/sort-create.sh",
			"bash /home/zyy/BurstyServerlessBenchmark/DIC/WebServices/openfaas/python-code/cryptography-create.sh",
			"bash /home/zyy/test.sh",
			"bash /home/zyy/test.sh",
			"bash /home/zyy/test.sh",
			"bash /home/zyy/test.sh",
			"bash /home/zyy/test.sh",
			"bash /home/zyy/test.sh",
			"bash /home/zyy/test.sh",
			"bash /home/zyy/test.sh",
			"bash /home/zyy/test.sh",
	};

	public static String deleteCmd[] = new String[]{
			"bash /home/zyy/INFless/developer/servingFunctions/resnet-50.sh",
			"bash /home/zyy/INFless/developer/servingFunctions/mobilenet.sh",
			"bash /home/zyy/BurstyServerlessBenchmark/DIC/WebServices/openfaas/python-code/hash.sh",
			"bash /home/zyy/BurstyServerlessBenchmark/DIC/WebServices/openfaas/python-code/Md5.sh",
			"bash /home/zyy/BurstyServerlessBenchmark/DIC/WebServices/openfaas/python-code/hello.sh",
			"bash /home/zyy/BurstyServerlessBenchmark/DIC/WebServices/openfaas/python-code/sort.sh",
			"bash /home/zyy/BurstyServerlessBenchmark/DIC/WebServices/openfaas/python-code/cryptography.sh",
			"bash /home/zyy/test.sh",
			"bash /home/zyy/test.sh",
			"bash /home/zyy/test.sh",
			"bash /home/zyy/test.sh",
			"bash /home/zyy/test.sh",
			"bash /home/zyy/test.sh",
			"bash /home/zyy/test.sh",
			"bash /home/zyy/test.sh",
			"bash /home/zyy/test.sh",
	};

	public static String FuncName[] = new String[]{
			"resnet-50",
			"mobilenet",
			"hash",
			"Md5",
			"hello",
			"sort",
			"cryptography",
			"nodeInfo",
			"curl",
			"cows",
			"sleep",
			"printer",
			"figlet",
			"nmap",
			"nslookup",
			"shasum"
	};
 
	protected abstract void initVariables();//init
	/**
	 * execute job
	 */
	public void executeJob(int serviceId, int type)
	{
		int sleepUnit=1000;
		try {
			System.out.println(FuncName[serviceId-1] + " request");
			FunctionExec functionExec = new FunctionExec(httpClient, queryItemsStr, serviceId, jsonParmStr, sleepUnit, "POST");

			if(ConfigPara.funcFlagArray[serviceId-1] == 0) {
				System.out.println("目前大小：" + ConfigPara.getRemainMemCapacity());
				ConfigPara.setMemoryCapacity(ConfigPara.getRemainMemCapacity() - ConfigPara.funcCapacity[serviceId - 1]);
				System.out.println("目前大小：" + ConfigPara.getRemainMemCapacity());
				ConfigPara.funcFlagArray[serviceId-1] = 2;
				coldStartTime++;
				System.out.println(tool.exec(createCmd[serviceId-1]));
				System.out.println(FuncName[serviceId-1] + " cold start time is " + coldStartTime);
			}
			else
				ConfigPara.funcFlagArray[serviceId-1] = 2;

			if(type == 3)
			{
				if(start == false)
				{
					oldTime = new Date().getTime();
					start = true;
				}else {
					long nowTime = new Date().getTime();
					System.out.println("now:" + nowTime + " ,old:" + oldTime);
					long t = nowTime - oldTime;
					if(t >= 7200000)
					{
						outOfBoundTime++;
					}
					timeList.add(t);
					oldTime = nowTime;
					if(mean == 0)
					{
						mean = (double)t;
					}else{
						double oldMean = mean;
						mean = oldMean + ((double)t - oldMean)/timeList.size();
						standard = standard + (t - oldMean)*(t - mean);
						cv = standard/mean/60000.0/invokeTime;
						System.out.println("mean:" + mean + ", " + "standard:" + standard + ", cv:" + cv);
						if(timeList.size() >= 50 && cv <= 2.0) //样本数目足够且直方图具有代表性，采用5%和99%的样本点
						{
							preWarm = (double)timeList.get(Math.min(timeList.size() - 1,((int)(timeList.size()*0.05) - 1)));
							keepAlive = (double)timeList.get(Math.max(0,((int)(timeList.size()*0.99) - 1)));
						} else if((double)outOfBoundTime/invokeTime >= 0.5)
						{
							arimaList = ARIMAReader.arimaList.get(serviceId);
							preWarm = arimaList.get(invokeTime)*0.85;
							keepAlive = arimaList.get(invokeTime)*0.3;
						}
						else { //样本不足或者直方图不具有代表性，pre-warm设置为0，keep-alive设置一个较长时间
							preWarm = 0.0;
							keepAlive = 1200000.0;
						}
					}
				}
				timeList.sort(Comparator.naturalOrder());
			}

			ConfigPara.kpArray[serviceId-1] = (int)keepAlive;        //Setting the keep-alive
			//ConfigPara.funcFlagArray[serviceId-1] = 2;
			functionExec.exec();
			//ConfigPara.funcFlagArray[serviceId-1] = 1;
			invokeTime++;
			System.out.println(FuncName[serviceId-1] + " Invoke time is " + invokeTime + ", cold start time is " + coldStartTime + ", cold start rate is " + ((double)coldStartTime/invokeTime)*100.0 + "%, preWarm time is " + preWarm + ", keepAive time is " + keepAlive);
			ConfigPara.funcFlagArray[serviceId-1] = 1;

			if(preWarm != 0.0) {
				Date now1 = new Date();
				Date preWarmTime = new Date(now1.getTime() + (long) preWarm);
				FunctionList.preMap.put(serviceId, preWarmTime);
				Timer timer1 = new Timer();
				TimerTask timerTask1 = new TimerTask() {
					@Override
					public void run() {
						Date now = new Date();
						System.out.println("prewarm start!!!!!!!!! " + ConfigPara.funcFlagArray[serviceId-1]);
						if (ConfigPara.funcFlagArray[serviceId-1] == 0) {
							try {
								System.out.println(FuncName[serviceId-1] + " prewarm now. pre-warm is " + preWarm);
								System.out.println(tool.exec(createCmd[serviceId-1]));
								ConfigPara.funcFlagArray[serviceId - 1] = 1;
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
				};
				timer1.schedule(timerTask1, (long) preWarm);
			}

			Date now = new Date();
			Date deleteTime = new Date(now.getTime() + (long) keepAlive);
			FunctionList.timeMap.put(serviceId, deleteTime);
			Timer timer = new Timer();
			int lastTime = invokeTime;
			TimerTask timerTask = new TimerTask() {
				@Override
				public void run() {
					Date now = new Date();
					System.out.println("delete start!!!!!!!!! " + ConfigPara.funcFlagArray[serviceId-1]);
					if(ConfigPara.funcFlagArray[serviceId-1] == 1 && invokeTime == lastTime)
					{
						try {
							ConfigPara.setMemoryCapacity(ConfigPara.getRemainMemCapacity() + ConfigPara.funcCapacity[serviceId-1]);
							ConfigPara.funcFlagArray[serviceId-1] = 0;
							System.out.println(FuncName[serviceId-1] + " keepAlive over. keepalive is " + keepAlive);
							System.out.println(tool.exec(deleteCmd[serviceId-1]));
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			};
			timer.schedule(timerTask, (long) keepAlive);

		}catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void exec(int serviceId)
	{
		int sleepUnit=1000;
		try {
			System.out.println(FuncName[serviceId-1] + " request");
			FunctionExec functionExec = new FunctionExec(httpClient, queryItemsStr, serviceId, jsonParmStr, sleepUnit, "POST");

			if(ConfigPara.funcFlagArray[serviceId-1] == 0) {
				System.out.println("目前大小：" + ConfigPara.getRemainMemCapacity());
				ConfigPara.setMemoryCapacity(ConfigPara.getRemainMemCapacity() - ConfigPara.funcCapacity[serviceId - 1]);
				System.out.println("目前大小：" + ConfigPara.getRemainMemCapacity());
				ConfigPara.funcFlagArray[serviceId-1] = 2;
				coldStartTime++;
				System.out.println(tool.exec(createCmd[serviceId-1]));
				System.out.println(FuncName[serviceId-1] + " cold start time is " + coldStartTime);
			}
			else
				ConfigPara.funcFlagArray[serviceId-1] = 2;

			if(start == false)
			{
				oldTime = new Date().getTime();
				start = true;
			}else {
				long nowTime = new Date().getTime();
				System.out.println("now:" + nowTime + " ,old:" + oldTime);
				long t = nowTime - oldTime;
				if(t >= 7200000)
				{
					outOfBoundTime++;
				}
				timeList.add(t);
				oldTime = nowTime;
				if(mean == 0)
				{
					mean = (double)t;
				}else{
					double oldMean = mean;
					mean = oldMean + ((double)t - oldMean)/timeList.size();
					standard = standard + (t - oldMean)*(t - mean);
					cv = standard/mean/60000.0/invokeTime;
					System.out.println("mean:" + mean + ", " + "standard:" + standard + ", cv:" + cv);
					if(timeList.size() >= 50 && cv <= 2.0) //样本数目足够且直方图具有代表性，采用5%和99%的样本点
					{
						preWarm = (double)timeList.get(Math.min(timeList.size() - 1,((int)(timeList.size()*0.05) - 1)));
						keepAlive = (double)timeList.get(Math.max(0,((int)(timeList.size()*0.99) - 1)));
					} else if((double)outOfBoundTime/invokeTime >= 0.5)
					{
						arimaList = ARIMAReader.arimaList.get(serviceId);
						preWarm = arimaList.get(invokeTime)*0.85;
						keepAlive = arimaList.get(invokeTime)*0.3;
					}
					else { //样本不足或者直方图不具有代表性，pre-warm设置为0，keep-alive设置一个较长时间
						preWarm = 0.0;
						keepAlive = 1200000.0;
					}
				}
			}
			timeList.sort(Comparator.naturalOrder());

			ConfigPara.kpArray[serviceId-1] = (int)keepAlive;        //Setting the keep-alive
			//ConfigPara.funcFlagArray[serviceId-1] = 2;
			functionExec.exec();
			//ConfigPara.funcFlagArray[serviceId-1] = 1;
			invokeTime++;
			System.out.println(FuncName[serviceId-1] + " Invoke time is " + invokeTime + ", cold start time is " + coldStartTime + ", cold start rate is " + ((double)coldStartTime/invokeTime)*100.0 + "%, preWarm time is " + preWarm + ", keepAive time is " + keepAlive);
			ConfigPara.funcFlagArray[serviceId-1] = 1;

			if(preWarm != 0.0) {
				Date now1 = new Date();
				Date preWarmTime = new Date(now1.getTime() + (long) preWarm);
				FunctionList.preMap.put(serviceId, preWarmTime);
				Timer timer1 = new Timer();
				TimerTask timerTask1 = new TimerTask() {
					@Override
					public void run() {
						Date now = new Date();
						System.out.println("prewarm start!!!!!!!!! " + ConfigPara.funcFlagArray[serviceId-1]);
						if (ConfigPara.funcFlagArray[serviceId-1] == 0) {
							try {
								FunctionList.funcMap.put(serviceId, true);
								System.out.println(FuncName[serviceId-1] + " prewarm now. pre-warm is " + preWarm);
								Double pri = Double.MAX_VALUE;
								Integer tempSid = 0;
								Hybrid hybrid = new Hybrid();
								while(ConfigPara.funcCapacity[serviceId - 1] > ConfigPara.getRemainMemCapacity()) {
									for (int i = 0; i < ConfigPara.funcFlagArray.length; i++) {
										if (hybrid.priority[i] < pri && ConfigPara.funcFlagArray[i] == 1) {
											pri = hybrid.priority[i];
											tempSid = i + 1;
										}
									}
									if (tempSid != 0) {
										System.out.println(tempSid + "-----------release-----------");
										System.out.println(tool.exec(deleteCmd[tempSid-1]));
										ConfigPara.funcFlagArray[serviceId - 1] = 0;
										ConfigPara.setMemoryCapacity(ConfigPara.getRemainMemCapacity() + ConfigPara.funcCapacity[tempSid-1]);
									}
								}
								ConfigPara.setMemoryCapacity(ConfigPara.getRemainMemCapacity() - ConfigPara.funcCapacity[serviceId-1]);
								ConfigPara.funcFlagArray[serviceId - 1] = 1;
								System.out.println(tool.exec(createCmd[serviceId-1]));
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
				};
				timer1.schedule(timerTask1, (long) preWarm);
			}

			Date now = new Date();
			Date deleteTime = new Date(now.getTime() + (long) keepAlive);
			FunctionList.timeMap.put(serviceId, deleteTime);
			Timer timer = new Timer();
			int lastTime = invokeTime;
			TimerTask timerTask = new TimerTask() {
				@Override
				public void run() {
					Date now = new Date();
					System.out.println("delete start!!!!!!!!! " + ConfigPara.funcFlagArray[serviceId-1]);
					if(ConfigPara.funcFlagArray[serviceId-1] == 1 && invokeTime == lastTime)
					{
						try {
							ConfigPara.setMemoryCapacity(ConfigPara.getRemainMemCapacity() + ConfigPara.funcCapacity[serviceId-1]);
							ConfigPara.funcFlagArray[serviceId-1] = 0;
							System.out.println(FuncName[serviceId-1] + " keepAlive over. keepalive is " + keepAlive);
							System.out.println(tool.exec(deleteCmd[serviceId-1]));
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			};
			timer.schedule(timerTask, (long) keepAlive);

		}catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void ZyyExec(int serviceId)
	{
		int sleepUnit=1000;
		System.out.println(FuncName[serviceId-1] + " request");
		FunctionExec functionExec = new FunctionExec(httpClient, queryItemsStr, serviceId, jsonParmStr, sleepUnit, "POST");

		if(firstTime == 0){
			firstTime = new Date().getTime();
		}
		if(ConfigPara.funcFlagArray[serviceId-1] == 0) {
			System.out.println("目前大小：" + ConfigPara.getRemainMemCapacity());
			ConfigPara.setMemoryCapacity(ConfigPara.getRemainMemCapacity() - ConfigPara.funcCapacity[serviceId - 1]);
			System.out.println("目前大小：" + ConfigPara.getRemainMemCapacity());
			ConfigPara.funcFlagArray[serviceId-1] = 2;
			coldStartTime++;
			//System.out.println(tool.exec(createCmd[serviceId-1]));
			System.out.println(FuncName[serviceId-1] + " cold start time is " + coldStartTime);
		}
		else
			ConfigPara.funcFlagArray[serviceId-1] = 2;

		if(!start)
		{
			oldTime = new Date().getTime();
			start = true;
		}else {
			nowTime = new Date().getTime();
			int intervalTime = (int) ((nowTime - firstTime) / 1000); //add
			preList = ARIMAReader.predictList.get(serviceId);
			System.out.println(preList.size() + "and" + invokeTime);
			for(int i = intervalTime + 1; i < preList.size();i++)
			{
				if(preList.get(i) != 0)
				{
					preWarm = (i - intervalTime - 1) * 1000;
				}
			}
			keepAlive = 1200000.0;
		}

		ConfigPara.kpArray[serviceId-1] = (int)keepAlive;        //Setting the keep-alive
		//ConfigPara.funcFlagArray[serviceId-1] = 2;
		functionExec.exec();
		//ConfigPara.funcFlagArray[serviceId-1] = 1;
		invokeTime++;
		System.out.println(FuncName[serviceId-1] + " Invoke time is " + invokeTime + ", cold start time is " + coldStartTime + ", cold start rate is " + ((double)coldStartTime/invokeTime)*100.0 + "%, preWarm time is " + preWarm + ", keepAive time is " + keepAlive);
		ConfigPara.funcFlagArray[serviceId-1] = 1;

		if(preWarm != 0.0) {
			Date now1 = new Date();
			Date preWarmTime = new Date(now1.getTime() + (long) preWarm);
			FunctionList.preMap.put(serviceId, preWarmTime);
			Timer timer1 = new Timer();
			TimerTask timerTask1 = new TimerTask() {
				@Override
				public void run() {
					Date now = new Date();
					System.out.println("prewarm start!!!!!!!!! " + ConfigPara.funcFlagArray[serviceId-1]);
					if (ConfigPara.funcFlagArray[serviceId-1] == 0) {
						FunctionList.funcMap.put(serviceId, true);
						System.out.println(FuncName[serviceId-1] + " prewarm now. pre-warm is " + preWarm);
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
			timer1.schedule(timerTask1, (long) preWarm);
		}

		Date now = new Date();
		Date deleteTime = new Date(now.getTime() + (long) keepAlive);
		FunctionList.timeMap.put(serviceId, deleteTime);
		Timer timer = new Timer();
		int lastTime = invokeTime;
		TimerTask timerTask = new TimerTask() {
			@Override
			public void run() {
				Date now = new Date();
				System.out.println("delete start!!!!!!!!! " + ConfigPara.funcFlagArray[serviceId-1]);
				if(ConfigPara.funcFlagArray[serviceId-1] == 1 && invokeTime == lastTime)
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
					System.out.println(FuncName[serviceId-1] + " keepAlive over. keepalive is " + keepAlive);
				}
			}
		};
		timer.schedule(timerTask, (long) keepAlive);

	}

	public abstract void coldStartManage(int serviceId);
	
}
