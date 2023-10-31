package scs.pojo;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class FunctionList {
    private ArrayList<Function> functionArrayList;
    public static Map<Integer,Boolean> funcMap = new HashMap<Integer,Boolean>();
    static {
        funcMap.put(1, false);
        funcMap.put(2, false);
        funcMap.put(3, false);
        funcMap.put(4, false);
        funcMap.put(5, false);
        funcMap.put(6, false);
        funcMap.put(7, false);
    };

    public static Map<Integer, Date> timeMap = new HashMap<Integer,Date>();

    public static Map<Integer, Date> preMap = new HashMap<Integer,Date>();

    public FunctionList(){
        this.functionArrayList = new ArrayList<Function>();
    }

    public ArrayList<Function> getFunctionArrayList() {
        return functionArrayList;
    }

    public void setFunctionArrayList(ArrayList<Function> functionArrayList) {
        this.functionArrayList = functionArrayList;
    }

    public int getFunctionNum() {
        return this.functionArrayList.size();
    }
}
