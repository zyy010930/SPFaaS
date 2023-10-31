package scs.util.loadGen.driver.serverless;

import scs.util.loadGen.driver.AbstractJobDriver;
import scs.util.repository.Repository;
import scs.util.tools.HttpClientPool;
import scs.util.tools.SSHTool;

import java.nio.charset.StandardCharsets;

public class FigLetFaasServingDriver extends AbstractJobDriver {
    /**a
     * Singleton code block
     */
    private static FigLetFaasServingDriver driver=null;
    private SSHTool tool = new SSHTool("192.168.1.4", "root", "wnlof309b507", StandardCharsets.UTF_8);
    public FigLetFaasServingDriver(){initVariables();}
    public synchronized static FigLetFaasServingDriver getInstance() {
        if (driver == null) {
            driver = new FigLetFaasServingDriver();
        }
        return driver;
    }

    @Override
    protected void initVariables() {
        httpClient= HttpClientPool.getInstance().getConnection();
        queryItemsStr= Repository.CryptographyFaasBaseURL;
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
