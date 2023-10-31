package scs.controller;

import scs.util.tools.ARIMAReader;
import scs.util.tools.CSVReader;
import scs.util.tools.FunctionRequest;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * @ClassName FunExecTimeGen
 * @Description ...
 * @Author @WZhang
 * @Date 2023/3/3 9:16
 * @Version 1.0
 */
public class FuncExecTimeGen {

    Map<Integer, ArrayList<Integer>> funcExecTimeGenAver() throws FileNotFoundException {
        CSVReader reader = new CSVReader();
        /*
        List<Map.Entry<String, ArrayList<Integer>>> list = reader.getAzure();

        System.out.println("build request!!!!!!!");
        FunctionRequest functionRequest = new FunctionRequest();

        System.out.println("Invoke Map Build------");
        Map<Integer,ArrayList<Integer>> InvokeMap = functionRequest.getMap(0,list); //obtain 7 groups records */

        Map<String,ArrayList<Integer>> mp = reader.getAzureTest();
        System.out.println("build request!!!!!!!");
        FunctionRequest functionRequest = new FunctionRequest();
        System.out.println("Invoke Map Build------");
        Map<Integer,ArrayList<Integer>> InvokeMap = functionRequest.getMapTest(mp);
        ARIMAReader arimaReader = new ARIMAReader();
        arimaReader.getARIMA();
        arimaReader.getGRU();
        System.out.println("Arima read------");

        Map<Integer,ArrayList<Integer>> funcMap = new TreeMap<>();
        for(int i = 1; i <= 16; i++)
        {
            ArrayList<Integer> timeList = new ArrayList<>();

            int lastIndex = 0;
            for(int j = 0; j < InvokeMap.get(i).size(); j++)
            //for(int j = 540; j < 720; j++)
            {
                if(InvokeMap.get(i).get(j) == 0) {
                    lastIndex = lastIndex + 60000;
                } else if (InvokeMap.get(i).get(j) == 1) {
                    lastIndex = lastIndex + 60000;
                    timeList.add(lastIndex);
                    lastIndex = 0;
                }else {
                    Double functionTime = 60000.0/InvokeMap.get(i).get(j);
                    lastIndex = lastIndex + functionTime.intValue();
                    timeList.add(lastIndex);
                    //lastIndex = 0;

                    for(int k = 1; k < InvokeMap.get(i).get(j)-1; k++) {
                        lastIndex = functionTime.intValue();
                        timeList.add(lastIndex);
                    }

                    Double d = new Double((InvokeMap.get(i).get(j)-1)*functionTime);
                    lastIndex = 60000 - d.intValue();
                    timeList.add(lastIndex);
                    lastIndex = 0;
                }
            }
            funcMap.put(LoadGenController.getMp().get(i), timeList);
        }
        return funcMap;
    }
}
