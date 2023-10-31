package scs.util.tools;

import java.util.Hashtable;
import java.util.Vector;

public class ARIMAiFlex {


    int             count=0;
    int []          model=new int[2];
    int[][]      modelOri=new int[][]{{0,1},{1,0},{1,1},{0,2},{2,0},{2,2},{1,2},{2,1},{3,0},{0,3},{3,1},{1,3},{3,2},{2,3},{3,3}};

    modelandpara       mp=null;
    int  predictValuetemp=0;
    int   avgpredictValue=0;
    int[]       bestmodel=new int[2];
    double[][] predictErr=new double[7][modelOri.length];
    double  minpreDicterr=9999999;
    int  bestpreDictValue=0;
    int           bestDif=0;
    int            memory=10;
    double[] traindataArray=null;
    double         validate=0;
    double[]   predataArray=null;

    double[]     dataArrayPredict=null;
    Hashtable<String,Integer> ht=new Hashtable<String,Integer>();
    Hashtable<String,Integer> ht2=new Hashtable<String,Integer>();

    double thresvalue=0;

    public ARIMAiFlex(double []dataArray)
    {
        //模型训练
        System.out.println("begin to train...");
        Vector<int[]> trainResult=this.Train(dataArray);
        //预测数据初始化
        int tempPredict=0;
        System.out.println("begin to predict...");
        for(int i=0;i<trainResult.size();i++)
        {
            thresvalue=0;
            System.out.println("predict..."+i+"/"+trainResult.size());
            tempPredict+=this.Predict(dataArray,memory,trainResult.get(i),0);
        }
        tempPredict=tempPredict/trainResult.size();
        System.out.println("tempPredict="+tempPredict);
    }

    public void preData(double[] dataArray,int type,int memory)
    {
        //      ++
        //**********
        //**********
        this.traindataArray=new double[dataArray.length-memory];
        System.arraycopy(dataArray, type, traindataArray, 0, traindataArray.length);
        this.validate=dataArray[traindataArray.length+type];//最后一个值作为训练时候的验证值。

    }

    public int Predict(double[] dataArray,int memory,int[] trainResult,double fanwei)
    {
        if(memory<0)
            return (int)(dataArray[dataArray.length-1]+dataArray[dataArray.length-2])/2;

        this.predataArray=new double[dataArray.length-memory];
        System.arraycopy(dataArray, memory, predataArray, 0, predataArray.length);
        ARIMA arima=new ARIMA(predataArray,trainResult[0]); //对原始数据做几阶差分处理0,1,2,7
        //参数初始化
        int count=100;
        int predictValuetemp=0;

        //统计每种模型的预测平均值
        while(count-->0)
        {
            mp=arima.getARIMAmodel(modelOri[trainResult[1]]);
            predictValuetemp+=arima.aftDeal(arima.predictValue(mp.model[0],mp.model[1],mp.para));
        }

        predictValuetemp/=100;
        //System.out.println("Predict value is:"+predictValuetemp);

        if(Math.abs(predictValuetemp-predataArray[predataArray.length-1])/predataArray[predataArray.length-1]>(0.3+fanwei))
        {
            thresvalue++;
            System.out.println("thresvalue="+thresvalue);
            //重新训练和预测
            //模型训练
            Vector<int[]> trainResult2=this.Train(dataArray);
            //预测数据初始化
            int tempPredict=0;
            for(int i=0;i<trainResult2.size();i++)
            {
                tempPredict+=this.Predict(dataArray,(memory-5),trainResult2.get(i),0.1*thresvalue);
            }
            tempPredict=tempPredict/trainResult2.size();
            //System.out.println("tempPredict="+tempPredict);
            return tempPredict;
        }
        else
        {
            return predictValuetemp;
        }
    }

    public Vector<int[]> Train(double[] dataArray)
    {
        int memory=60;//训练的时候预测的值的个数

        for(int datai=0;datai<memory;datai++)
        {
            //System.out.println("train... "+datai+"/"+memory);
            this.preData(dataArray, datai,memory);//准备训练数据

            for(int diedai=0;diedai<7;diedai++)
            {
                ARIMA arima=new ARIMA(traindataArray,diedai); //对原始数据做几阶差分处理0,1,2,7

                //统计每种模型的预测平均值
                for(int modeli=0;modeli<modelOri.length;modeli++)
                {
                    //参数初始化
                    count=100;
                    predictValuetemp=0;

                    while(count-->0)
                    {
                        mp=arima.getARIMAmodel(modelOri[modeli]);
                        predictValuetemp+=arima.aftDeal(arima.predictValue(mp.model[0],mp.model[1],mp.para));
                        //System.out.println("predictValuetemp"+predictValuetemp);
                    }
                    predictValuetemp/=100;
                    //计算训练误差
                    predictErr[diedai][modeli]+=Math.abs(100*(predictValuetemp-validate)/validate);
                }
            }
        }

        double minvalue=10000000;
        int tempi=0;
        int tempj=0;
        Vector<int[]> bestmodelVector=new Vector<int[]>();
        int[][] flag=new int[7][modelOri.length];

        for(int ii=0;ii<5;ii++)
        {	minvalue=10000000;
            for(int i=0;i<predictErr.length;i++)
            {
                for(int j=0;j<predictErr[i].length;j++)
                {
                    if(flag[i][j]==0)
                    {
                        if(predictErr[i][j]<minvalue)
                        {
                            minvalue=predictErr[i][j];
                            tempi=i;
                            tempj=j;
                            flag[i][j]=1;
                        }
                    }
                }
            }
            bestmodelVector.add(new int[]{tempi,tempj});

            //System.out.println("best model:Dif="+tempi+"..."+"index of model="+tempj);
            System.out.println("ARIMAAvgPredictErr="+minvalue/memory);
        }

//		for(int i=0;i<predictErr.length;i++)
//			for(int j=0;j<predictErr[i].length;j++)
//			{
//				System.out.println("Dif "+i+" Model index"+j+"= "+predictErr[i][j]/memory);
//			}

        //System.out.println("--tempi="+tempi+"~~~"+"tempj="+tempj);
        System.out.println("----------------------------------------");

        return bestmodelVector;
    }

}