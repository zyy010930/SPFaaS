package scs.pojo;


public class Function {
    private static int waitMin = 10000;
    private int functionId;
    private int invokeNum = 0;

    public Function(){}
    public Function(int functionId)
    {
        this.functionId = functionId;
        this.invokeNum = 0;
    }

    public int getFunctionId() {
        return functionId;
    }

    public void setFunctionId(int functionId) {
        this.functionId = functionId;
    }

    public int getInvokeNum() {
        return invokeNum;
    }

    public void setInvokeNum(int invokeNum) {
        this.invokeNum = invokeNum;
    }

    public void addInvoke(){
        this.invokeNum++;
    }
}
