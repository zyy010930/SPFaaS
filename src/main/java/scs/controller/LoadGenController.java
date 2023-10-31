package scs.controller;

import java.io.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import net.sf.json.JSONArray;
import scs.methods.LRFU.LFU;
import scs.methods.LRFU.LRU;
import scs.methods.OverFramework;
import scs.pojo.PageQueryData;
import scs.pojo.QueryData;
import scs.util.format.DataFormats;
import scs.util.repository.Repository;

/**
 * Load generator controller class, it includes interfaces as follows:
 * 1.Control the open/close of load generator
 * 2.Support the dynamic QPS setting
 * 3.support GPI for user to view the realtime latency and QPS
 * @author YananYang 
 * @date 2019-11-12
 * @email ynyang@tju.edu.cn
 */
@Controller
public class LoadGenController {
	private DataFormats dataFormat=DataFormats.getInstance();
	private Repository instance=Repository.getInstance();
	private static Map<Integer,Integer> mp;
	{
		mp = new HashMap<Integer, Integer>();
		mp.put(1, 1);
		mp.put(2, 2);
		mp.put(3, 3);
		mp.put(4, 4);
		mp.put(5, 5);
		mp.put(6, 6);
		mp.put(7, 7);
		mp.put(8, 8);
		mp.put(9, 9);
		mp.put(10, 10);
		mp.put(11, 11);
		mp.put(12, 12);
		mp.put(13, 13);
		mp.put(14, 14);
		mp.put(15, 15);
		mp.put(16, 16);
	}

	private ArrayList<Map<Integer,Integer>> functionList = new ArrayList<Map<Integer, Integer>>();
	/**
	 * Start the load generator for latency-critical services
	 * @param intensity The concurrent request number per second (RPS)
	 * @param serviceId The index id of web inference service, started from 0 by default
	 */
	@RequestMapping("/startOnlineQuery.do")
	public void startOnlineQuery(HttpServletRequest request,HttpServletResponse response,
			@RequestParam(value="intensity",required=true) int intensity,
			@RequestParam(value="serviceId",required=true) int serviceId,
			@RequestParam(value="concurrency",required=true) int concurrency){
		try{
			if (serviceId < 0 || serviceId >= Repository.NUMBER_LC){
				response.getWriter().write("serviceId="+serviceId+" does not exist with service number="+Repository.NUMBER_LC);
			} else {
				if (concurrency > 0) {
					Repository.concurrency[serviceId]=1;
				} else {
					Repository.concurrency[serviceId]=0;
				}
				intensity=intensity<=0?1:intensity;//validation
				Repository.realRequestIntensity[serviceId]=intensity;
				
				if(Repository.onlineQueryThreadRunning[serviceId]==true){
					response.getWriter().write("online query threads"+serviceId+" are already running");
				}else{
					Repository.onlineDataFlag[serviceId]=true; 
					Repository.statisticsCount[serviceId]=0;//init statisticsCount
					Repository.totalQueryCount[serviceId]=0;//init totalQueryCount
					Repository.totalRequestCount[serviceId]=0;//init totalRequestCount
					Repository.onlineDataList.get(serviceId).clear();//clear onlineDataList
					Repository.windowOnlineDataList.get(serviceId).clear();//clear windowOnlineDataList
					if(true) {
						//obtaining functions time internals
						FuncExecTimeGen funcExecTimeGen = new FuncExecTimeGen();
						Map<Integer, ArrayList<Integer>> funcMap = funcExecTimeGen.funcExecTimeGenAver();

						//Setting Memory Capacity
						ConfigPara configPara = new ConfigPara();
						configPara.setMemoryCapacity(60.0);

						System.out.println("start thread");

						ExecutorService executor = Executors.newFixedThreadPool(17);
						FunctionThread thread1 = new FunctionThread(1, funcMap.get(1));
						FunctionThread thread2 = new FunctionThread(2, funcMap.get(2));
						FunctionThread thread3 = new FunctionThread(3, funcMap.get(3));
						FunctionThread thread4 = new FunctionThread(4, funcMap.get(4));
						FunctionThread thread5 = new FunctionThread(5, funcMap.get(5));
						FunctionThread thread6 = new FunctionThread(6, funcMap.get(6));
						FunctionThread thread7 = new FunctionThread(7, funcMap.get(7));
						FunctionThread thread8 = new FunctionThread(8, funcMap.get(8));
						FunctionThread thread9 = new FunctionThread(9, funcMap.get(9));
						FunctionThread thread10 = new FunctionThread(10, funcMap.get(10));
						FunctionThread thread11 = new FunctionThread(11, funcMap.get(11));
						FunctionThread thread12 = new FunctionThread(12, funcMap.get(12));
						FunctionThread thread13 = new FunctionThread(13, funcMap.get(13));
						FunctionThread thread14 = new FunctionThread(14, funcMap.get(14));
						FunctionThread thread15 = new FunctionThread(15, funcMap.get(15));
						FunctionThread thread16 = new FunctionThread(16, funcMap.get(16));
						CapacityThread thread17 = new CapacityThread();
						executor.execute(thread1);
						executor.execute(thread2);
						executor.execute(thread3);
						executor.execute(thread4);
						executor.execute(thread5);
						executor.execute(thread6);
						executor.execute(thread7);
						executor.execute(thread8);
						executor.execute(thread9);
						executor.execute(thread10);
						executor.execute(thread11);
						executor.execute(thread12);
						executor.execute(thread13);
						executor.execute(thread14);
						executor.execute(thread15);
						executor.execute(thread16);
						executor.execute(thread17);
						executor.shutdown();
					} else {
						response.getWriter().write("serviceId="+serviceId+"doesnot has loaderDriver instance with LC number="+Repository.NUMBER_LC);
					}
				}
			}


		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public static Map<Integer,Integer> getMp() {
		return mp;
	}

	/**
	 * dynamically set the RPS of web-inference service
	 * @param request
	 * @param response
	 * @param intensity The concurrent request number per second (RPS)
	 */
	@RequestMapping("/setIntensity.do")
	public void setIntensity(HttpServletRequest request,HttpServletResponse response,
			@RequestParam(value="intensity",required=true) int intensity,
			@RequestParam(value="serviceId",required=true) int serviceId){
		try{ 
			intensity=intensity<0?0:intensity;// legitimacy verification
			Repository.realRequestIntensity[serviceId]=intensity;
			response.getWriter().write("serviceId="+serviceId+" realRequestIntensity is set to "+Repository.realRequestIntensity[serviceId]);
		}catch(Exception e){
			e.printStackTrace();
		}

	}
	/**
	 * Stop the load generator for latency-critical services
	 * @param request
	 * @param response
	 */
	@RequestMapping("/stopOnlineQuery.do")
	public void stopOnlineQuery(HttpServletRequest request, HttpServletResponse response,
			@RequestParam(value="serviceId",required=true) int serviceId){
		try{
			
			Repository.realRequestIntensity[serviceId]=0;
			Repository.onlineDataFlag[serviceId]=false; 
			if(serviceId<Repository.NUMBER_LC && serviceId>=0) { 
				if(Repository.loaderMap.get(serviceId).getLoaderName().toLowerCase().contains("redis")){
					Repository.loaderMap.get(serviceId).getAbstractJobDriver().executeJob(serviceId,0);
				}
			}
			response.getWriter().write("serviceId="+serviceId+" stopped loader");
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	/**
	 * Turn into the GPI page to see the real-time request latency line
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping("/goOnlineQuery.do")
	public String goOnlineQuery(HttpServletRequest request,HttpServletResponse response,Model model,
			@RequestParam(value="serviceId",required=true) int serviceId){
		StringBuffer strName0=new StringBuffer();
		StringBuffer strData0=new StringBuffer();
		StringBuffer strName1=new StringBuffer();
		StringBuffer strData1=new StringBuffer();
		StringBuffer HSeries=new StringBuffer();

		strName0.append("{name:'queryTime99th',");
		strData0.append("data:[");

		strName1.append("{name:'queryTimeAvg',");
		strData1.append("data:[");

		List<QueryData> list=new ArrayList<QueryData>();
		list.addAll(Repository.windowOnlineDataList.get(serviceId));
		while(list.size()==0){
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			list.clear();
			list.addAll(Repository.windowOnlineDataList.get(serviceId));
		}
		int curSize=list.size();
		if(curSize<Repository.windowSize){
			int differ=Repository.windowSize-curSize;
			for(int i=0;i<differ;i++){
				list.add(list.get(curSize-1));
			}
		} 
		int size=list.size();
		for(int i=0;i<size-1;i++){
			strData0.append("[").append(list.get(i).getGenerateTime()).append(",").append(list.get(i).getQueryTime99th()).append("],");
			strData1.append("[").append(list.get(i).getGenerateTime()).append(",").append(list.get(i).getQueryTimeAvg()).append("],");

		}
		strData0.append("[").append(list.get(size-1).getGenerateTime()).append(",").append(list.get(size-1).getQueryTime99th()).append("]]}");
		strData1.append("[").append(list.get(size-1).getGenerateTime()).append(",").append(list.get(size-1).getQueryTimeAvg()).append("]]}");

		HSeries.append(strName0).append(strData0).append(",").append(strName1).append(strData1);

		model.addAttribute("seriesStr",HSeries.toString());  
		model.addAttribute("serviceId",serviceId);

		return "onlineData";
	}

	/**
	 * obtain the latest 99th latency of last second
	 * this is done by Ajax, no pages switch
	 * @param request
	 * @param response
	 */
	@RequestMapping("/getOnlineWindowAvgQueryTime.do")
	public void getOnlineQueryTime(HttpServletRequest request,HttpServletResponse response,
			@RequestParam(value="serviceId",required=true) int serviceId){
		try{
			PageQueryData pqd=new PageQueryData(Repository.latestOnlineData[serviceId]);
			float[] res=instance.getOnlineWindowAvgQueryTime(serviceId);
			pqd.setRealRps(Repository.realRequestIntensity[serviceId]);
			pqd.setWindowAvg99thQueryTime(dataFormat.subFloat(res[0],2));
			pqd.setWindowAvgAvgQueryTime(dataFormat.subFloat(res[1],2));
		
			response.getWriter().write(JSONArray.fromObject(pqd).toString());
			//response.getWriter().write(JSONArray.fromObject(Repository.latestOnlineData[serviceId]).toString().replace("}",",\"OnlineAvgQueryTime\":"+dataFormat.subFloat(instance.getOnlineAvgQueryTime(serviceId),2)+"}"));
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	/**
	 * obtain the latest 99th latency of last second
	 * this is done by Ajax, no pages switch
	 * @param request
	 * @param response
	 */
	@RequestMapping("/getLoaderGenQuery.do")
	public void getOnlineQueryTime(HttpServletRequest request,HttpServletResponse response) {
		try{
			List<PageQueryData> list=new ArrayList<PageQueryData>();
			for(int i=0; i<Repository.NUMBER_LC; i++){
				PageQueryData pqd=null;
				if(Repository.latestOnlineData[i]==null){
					pqd=new PageQueryData();
					pqd.setRealRps(Repository.realRequestIntensity[i]);
					pqd.setRealQps(Repository.realQueryIntensity[i]);
				} else {
					pqd=new PageQueryData(Repository.latestOnlineData[i]);
					float[] res=instance.getOnlineWindowAvgQueryTime(i);
					pqd.setRealRps(Repository.realRequestIntensity[i]);
					pqd.setWindowAvg99thQueryTime(dataFormat.subFloat(res[0],2));
					pqd.setWindowAvgAvgQueryTime(dataFormat.subFloat(res[1],2));
				}
				pqd.setLoaderName(Repository.loaderMap.get(i).getLoaderName());
				list.add(pqd);
			}
			response.getWriter().write(JSONArray.fromObject(list).toString());
			//response.getWriter().write(JSONArray.fromObject(Repository.latestOnlineData[serviceId]).toString().replace("}",",\"OnlineAvgQueryTime\":"+dataFormat.subFloat(instance.getOnlineAvgQueryTime(serviceId),2)+"}"));
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	static class FunctionThread extends Thread{
		private int serviceId;
		private ArrayList<Integer> list;

		public FunctionThread(){}

		public FunctionThread(int id, ArrayList<Integer> list)
		{
			this.serviceId = id;
			this.list = list;
		}

		public void run(){
			System.out.println("new start");
			for(Integer time : this.list)
			{
				System.out.println("function:" + serviceId + "sleep:" + time);
				try {
					Thread.sleep(time);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				OverFramework.run(serviceId, 6);
			}
		}
	}

	static class CapacityThread extends Thread {
		public CapacityThread(){}

		public void run(){
			try {
				Thread.sleep(120000);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
			ArrayList<Double> list = new ArrayList<>();
			for(int i = 1; i <= 720; i++) //add
			{
				try {
					Thread.sleep(60000);
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
				double d = 0;
				for(int j = 0;j < ConfigPara.funcFlagArray.length; j++)
				{
					if(ConfigPara.funcFlagArray[j] != 0)
					{
						d += ConfigPara.funcCapacity[j];
					}
				}
				list.add(d);
				System.out.println("将内存数据载入list,list长度:" + list.size() + " size:" + d);
				if((60.0 - d) != ConfigPara.getRemainMemCapacity())
				{
					ConfigPara.setMemoryCapacity(60.0 - d);
				}
			}

//			File writeFile = new File("/home/zyy/capacity.csv");
//			try{
//				BufferedWriter writeText = new BufferedWriter(new FileWriter(writeFile,true));
//				for(int i = 0;i < list.size(); i++) {
//					writeText.write(String.valueOf(list.get(i)));
//					writeText.newLine();
//					System.out.println(list.get(i));
//				}
//				//使用缓冲区的刷新方法将数据刷到目的地中
//				writeText.flush();
//				//关闭缓冲区，缓冲区没有调用系统底层资源，真正调用底层资源的是FileWriter对象，缓冲区仅仅是一个提高效率的作用
//				//因此，此处的close()方法关闭的是被缓存的流对象
//				writeText.close();
//			}catch (FileNotFoundException e){
//				System.out.println("没有找到指定文件");
//			}catch (IOException e){
//				System.out.println("文件读写出错");
//			}
			for(int i = 0;i < list.size(); i++) {
				//writeText.write(String.valueOf(list.get(i)));
				//writeText.newLine();
				System.out.println(list.get(i));
			}
		}
	}
}