package scs.util.tools;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class CSVReader {
    public static Map<String,Integer> FuncIdMp = new HashMap<>();

    public static Map<Integer,String> hashMap = new HashMap<>();
    static {
        hashMap.put(1,"37fce695333914294e4b8e37ed76e0f1687cb9f6fce96b926af03220906015a9");
        hashMap.put(2,"37fce695333914294e4b8e37ed76e0f1687cb9f6fce96b926af03220906015a9");
        hashMap.put(3,"c3aa7afd1c1edd5c917c93d4bfd068fe8075b4273781ba600de7b47c14fdf850");
        hashMap.put(4,"258863698dfd81126b99dd40fd6d0a6b87025715fb27b03f45a6750bef2b6e22");
        hashMap.put(5,"2c49d9a4b65e58013336245056141fac5abdf25848bc983c2cd6e0fd06384d8b");
        hashMap.put(6,"ca0ff2ece04b7b9bbe0274254b6785c9db4280f04b55cbc9a75b92cb5998f6ef");
        hashMap.put(7,"077d12c552f426ec9eb73230e63e2297508e6e8b9f208e69295f35217d274e30");
        hashMap.put(8,"8f53f546ca3f37ae8dd4e30699f69f91bbe697a02e1a5678760a4a0c60dde541");
        hashMap.put(9,"f682a5fdf8ca1209408017760311ff0906f2ebfbf6e793d1143b7a6f5d0ac826");
        hashMap.put(10,"683bba5f017ca6bf1d62d4d342f6eee4c6913449ab14eb5dc16781fba44b279b");
        hashMap.put(11,"e828e13427e7baa34b9b627d7e6d36a31c3477610b13f4b9fdb26e73d42d1a0c");
        hashMap.put(12,"e4a14811bf2f86260ee64a48b04f9cbc9fc1923b32926c7b7c9530de6a0e9f93");
        hashMap.put(13,"573352d660b69284b70920162ba565f45120ee3f800c19eba9724ac0ca940dfe");
        hashMap.put(14,"431ee10ad4371d925addfa68f80c72447991135fb9cefdc71524ec1f5050142f");
        hashMap.put(15,"4f5ad3b9492e63743ea87ca52c465da118a1e211da97d9092b276dfa707def58");
        hashMap.put(16,"c92b8efe535bccd0987056ab8bcb3344b1cc59d755955c050808344f3993c58e");
    }
 
    public List<Map.Entry<String, ArrayList<Integer>>> getAzure() {

        String csvFile = "/home/dmy/azureFunction/invocations_per_function_md.anon.d01.csv";
        String line = "";
        String cvsSplitBy = ",";
        Map<String,ArrayList<Integer>> InvokeMap = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {

            while ((line = br.readLine()) != null) {

                // use comma as separator
                String[] country = line.split(cvsSplitBy);
                if(country[3].compareTo("http") == 0) {
                    ArrayList<Integer> list = new ArrayList<>();
                    for (int i = 4; i <= 1443; i++) {
                        list.add(Integer.parseInt(country[i]));
                    }
                    if (InvokeMap.containsKey(country[2]) == false) {
                        InvokeMap.put(country[2], list);
                    }
                    System.out.println("[function= " + country[2] + " , trigger=" + country[3] + "]" + " num:" + list.size());
                }

            }
            List<Map.Entry<String, ArrayList<Integer>>> entryList2 = new ArrayList<>(InvokeMap.entrySet());
            /*
            Collections.sort(entryList2, new Comparator<Map.Entry<String, ArrayList<Integer>>>() {
                @Override
                public int compare(Map.Entry<String, ArrayList<Integer>> me1, Map.Entry<String, ArrayList<Integer>> me2) {
                    return Integer.valueOf(me1.getValue().size()).compareTo(me2.getValue().size()); // 升序排序
                    //return me2.getValue().compareTo(me1.getValue()); // 降序排序
                }
            });*/
            return entryList2;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Map<String,ArrayList<Integer>> getAzureTest() {
        String csvFile = "/home/dmy/azureFunction/invocations_per_function_md.anon.d01.csv";
        String line = "";
        String cvsSplitBy = ",";
        Map<String,ArrayList<Integer>> InvokeMap = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {

            int num = 1;
            while ((line = br.readLine()) != null) {

                // use comma as separator
                String[] country = line.split(cvsSplitBy);
                if(country[3].compareTo("http") == 0) {
                    ArrayList<Integer> list = new ArrayList<>();
                    for (int i = 4; i <= 1443; i++) {
                        list.add(Integer.parseInt(country[i]));
                    }
                    if (InvokeMap.containsKey(country[2]) == false) {
                        InvokeMap.put(country[2], list);
                    }
                    FuncIdMp.put(country[2],num);
                    System.out.println("[function= " + country[2] + " , trigger=" + country[3] + "]" + " num:" + list.size());
                }
                num++;

            }
            return InvokeMap;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}