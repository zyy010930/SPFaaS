package scs.util.loadGen.driver.serverless;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Comparator;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import scs.controller.ConfigPara;
import scs.pojo.FunctionList;
import scs.util.loadGen.driver.AbstractJobDriver;
import scs.util.loadGen.threads.FunctionExec;
import scs.util.loadGen.threads.LoadExecThread;
import scs.util.loadGen.threads.LoadExecThreadRandom;
import scs.util.repository.Repository;
import scs.util.tools.ARIMAReader;
import scs.util.tools.HttpClientPool;
import scs.util.tools.SSHTool;

/**
 * Image recognition service request class
 * GPU inference
 * @author Yanan Yang
 *
 */
public class ResNet50FaasTFServingDriver extends AbstractJobDriver{
	/**
	 * Singleton code block
	 */
	private static ResNet50FaasTFServingDriver driver=null;
	private SSHTool tool = new SSHTool("192.168.1.4", "root", "wnlof309b507", StandardCharsets.UTF_8);


	public ResNet50FaasTFServingDriver(){initVariables();}
	public synchronized static ResNet50FaasTFServingDriver getInstance() {
		if (driver == null) {
			driver = new ResNet50FaasTFServingDriver();
		}
		return driver;
	}

	@Override
	protected void initVariables() {
		httpClient=HttpClientPool.getInstance().getConnection();
		queryItemsStr=Repository.resNet50FaasBaseURL;
		jsonParmStr=Repository.resNet50ParmStr; 
		queryItemsStr=queryItemsStr.replace("Ip","192.168.1.4");
		queryItemsStr=queryItemsStr.replace("Port","31112");
	}

	/**
	 * using countDown to send requests in open-loop
	 */


	@Override
	public void coldStartManage(int serviceId) {

	}

}
