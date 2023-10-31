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

public class CowsFaasServingDriver extends AbstractJobDriver{
    /**
     * Singleton code block
     */
    private static CowsFaasServingDriver driver=null;
    private SSHTool tool = new SSHTool("192.168.1.4", "root", "wnlof309b507", StandardCharsets.UTF_8);
    public CowsFaasServingDriver(){initVariables();}
    public synchronized static CowsFaasServingDriver getInstance() {
        if (driver == null) {
            driver = new CowsFaasServingDriver();
        }
        return driver;
    }

    @Override
    protected void initVariables() {
        httpClient= HttpClientPool.getInstance().getConnection();
        queryItemsStr= Repository.HashFaasBaseURL;
        jsonParmStr=Repository.resNet50ParmStr;
        queryItemsStr=queryItemsStr.replace("Ip","192.168.1.4");
        queryItemsStr=queryItemsStr.replace("Port","31112");
    }


    @Override
    public void coldStartManage(int serviceId) {

    }
}
