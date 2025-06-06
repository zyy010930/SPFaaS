package scs.util.tools;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ARIMAReader {
    public static Map<Integer,ArrayList<Double>> arimaList = new HashMap<>();

    public static Map<Integer,ArrayList<Integer>> predictList = new HashMap<>();

    public static Map<Integer,ArrayList<Integer>> IceList = new HashMap<>();

    public static Map<Integer,ArrayList<Integer>> SpesList = new HashMap<>();

    public static Map<Integer,ArrayList<Integer>> EnsembleList = new HashMap<>();

    public static Map<Integer,ArrayList<Integer>> TcnList = new HashMap<>();

    public static Map<Integer,ArrayList<Integer>> SPFaaSList = new HashMap<>();

    public ARIMAReader(){}

    public void getARIMA() throws FileNotFoundException {
        String csvFile = "/home/dmy/person.csv";
        String line = "";
        String cvsSplitBy = ",";
        int num = 1;
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))){
            while ((line = br.readLine()) != null) {
                String[] country = line.split(cvsSplitBy);
                if(country.length == 0)
                {
                    continue;
                }
                else
                {
                    if(country.length == 1 && country[0] != "0")
                        continue;
                    ArrayList<Double> list = new ArrayList<>();
                    for (int i = 0; i < country.length; i++) {
                        list.add(Double.parseDouble(country[i]));
                    }
                    arimaList.put(num,list);
                    num++;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void getGRU() throws FileNotFoundException {
        String csvFile = "/home/zyy/predict.csv";
        String line = "";
        String cvsSplitBy = ",";
        int num = 1;
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))){
            while ((line = br.readLine()) != null) {
                String[] country = line.split(cvsSplitBy);
                ArrayList<Integer> list = new ArrayList<>();
                //ArrayList<Integer> preList = new ArrayList<>();
                for (int i = 0; i < country.length; i++) {
                    list.add(Integer.parseInt(country[i]));
                }
//                int old = -1;
//                for(int i = 0;i<list.size();i++)
//                {
//                    if(list.get(i) != 0)
//                    {
//                        preList.add(i - old);
//                        old = i;
//                    }
//                }
                predictList.put(num, list);
                num++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void getIce() {
        String csvFile = "/home/zyy/Icebreaker_pre.csv";
        String line = "";
        String cvsSplitBy = ",";
        int num = 1;
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))){
            while ((line = br.readLine()) != null) {
                String[] country = line.split(cvsSplitBy);
                ArrayList<Integer> list = new ArrayList<>();
                for (int i = 0; i < country.length; i++) {
                    list.add(Integer.parseInt(country[i]));
                }
                IceList.put(num, list);
                num++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void getSpes() {
        String csvFile = "/home/zyy/Spes_pre.csv";
        String line = "";
        String cvsSplitBy = ",";
        int num = 1;
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))){
            while ((line = br.readLine()) != null) {
                String[] country = line.split(cvsSplitBy);
                ArrayList<Integer> list = new ArrayList<>();
                for (int i = 0; i < country.length; i++) {
                    list.add(Integer.parseInt(country[i]));
                }
                SpesList.put(num, list);
                num++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void getEnsemble() {
        String csvFile = "/home/zyy/ensemble_pre.csv";
        String line = "";
        String cvsSplitBy = ",";
        int num = 1;
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))){
            while ((line = br.readLine()) != null) {
                String[] country = line.split(cvsSplitBy);
                ArrayList<Integer> list = new ArrayList<>();
                for (int i = 0; i < country.length; i++) {
                    list.add(Integer.parseInt(country[i]));
                }
                EnsembleList.put(num, list);
                num++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void getTCN() {
        String csvFile = "/home/zyy/TCN_pre.csv";
        String line = "";
        String cvsSplitBy = ",";
        int num = 1;
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))){
            while ((line = br.readLine()) != null) {
                String[] country = line.split(cvsSplitBy);
                ArrayList<Integer> list = new ArrayList<>();
                for (int i = 0; i < country.length; i++) {
                    list.add(Integer.parseInt(country[i]));
                }
                TcnList.put(num, list);
                num++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void getSPFaaS() {
        String csvFile = "/home/zyy/func300_SPFaaS_pre.csv";
        //String csvFile = "/home/zyy/func300_spfaas_pre_huawei.csv";
        String line = "";
        String cvsSplitBy = ",";
        int num = 1;
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))){
            while ((line = br.readLine()) != null) {
                String[] country = line.split(cvsSplitBy);
                ArrayList<Integer> list = new ArrayList<>();
                for (int i = 0; i < country.length; i++) {
                    list.add(Integer.parseInt(country[i]));
                }
                SPFaaSList.put(num, list);
                num++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
