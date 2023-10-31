package scs.util.loadGen.threads;

import org.apache.http.impl.client.CloseableHttpClient;
import scs.util.repository.Repository;
import scs.util.tools.HttpClientPool;
import scs.util.tools.SSHTool;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

public class FunctionExec {
    private CloseableHttpClient httpclient;//httpclient对象
    private String url;//请求的url
    private CountDownLatch begin;
    private int serviceId;
    private String jsonObjectStr;
    private int sendDelay;
    private String requestType;

    public FunctionExec(CloseableHttpClient httpclient, String url, int serviceId, String jsonObjectStr, int sendDelay, String requestType){
        this.httpclient=httpclient;
        this.url=url;
        this.serviceId=serviceId;
        this.jsonObjectStr=jsonObjectStr;
        this.sendDelay=sendDelay;
        this.requestType=requestType;
    }

    public void exec() {
        try{
            if(requestType!=null && requestType.startsWith("G")){
                //int time=HttpClientPool.getResponseTime(httpclient, url);
                int time=new Random().nextInt(100);
                synchronized (Repository.onlineDataList.get(serviceId)) {
                    Repository.onlineDataList.get(serviceId).add(time);
                }
            } else {
                //int time=new Random().nextInt(100);
                Date date = new Date();
                SimpleDateFormat dateFormat= new SimpleDateFormat("yyyy-MM-dd :hh:mm:ss");

                int time= HttpClientPool.postResponseTime(httpclient, url, jsonObjectStr);
/*
                InputStream in = null;
                try {
                    Process pro = Runtime.getRuntime().exec(new String[]{"faas-cli remove -f /home/zyy/INFless/developer/servingFunctions/mobilenet.yml"});
                    pro.waitFor();
                    in = pro.getInputStream();
                    BufferedReader read = new BufferedReader(new InputStreamReader(in));
                    String result = read.readLine();
                    System.out.println("INFO:"+result);
                } catch (Exception e) {
                    e.printStackTrace();
                }*/
                //SSHTool tool = new SSHTool("192.168.3.154", "root", "wnlof309b507", StandardCharsets.UTF_8);


                System.out.println("serviceId:" + serviceId + " time:" + dateFormat.format(date) + " response:" + time);
                synchronized (Repository.onlineDataList.get(serviceId)) {
                    Repository.onlineDataList.get(serviceId).add(time);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
