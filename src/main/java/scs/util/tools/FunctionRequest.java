package scs.util.tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FunctionRequest {
    public Map<Integer,ArrayList<Integer>> getMap(int p,List<Map.Entry<String, ArrayList<Integer>>> list)
    {
        Map<Integer,ArrayList<Integer>> mp = new HashMap<>();
        for(int i = p * 7,j = 1;i<(((p+1)*7)%list.size());i++,j++)
        {
            System.out.println(list.get(i).getKey());
            mp.put(j,list.get(i).getValue());
        }
        return mp;
    }

    public Map<Integer,ArrayList<Integer>> getMapTest(Map<Integer,ArrayList<Integer>> mp)
    {
        Map<Integer,ArrayList<Integer>> mp1 = new HashMap<>();
        for(int i = 1; i <= 300; i++)
        {
            mp1.put(i,mp.get(i));
        }
        return mp1;
    }
}
