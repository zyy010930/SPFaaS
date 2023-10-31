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

    //                "a57bac412d613fe8bf26af57e2e4fe2054274e6fe66d120b1ef3a9315c38a5cc",
//                "c108b4864b866b38b80d0e4594cc6d038f39668b804a1ba88d2b95d682a8ab20",
//                "e4750c990ae62c562798f2556ffb69dc24f7a7e4e685fcba05824f8885bdd604",
//                "b84c86b95ba7f7e1c0ea58ab7f3e9f685c138d1009789ef20ba93f7f5342149e",
//                "7054706e8b0cbf30c40e65a8eefb438bd11ea21593d95d49e1d3f44a02d037a7",
//                "8b492f4d307e34921e662e08b071ed15bee2ad67bcd4d302e70a425edcf767ac",
//                "6ddf9d84df9ed32bb7ab4c51b5cd849dbaf46eaf63601aaea42adeafbe51f5db",
    public Map<Integer,ArrayList<Integer>> getMapTest(Map<String,ArrayList<Integer>> mp)
    {
        Map<Integer,ArrayList<Integer>> mp1 = new HashMap<>();
        String str[] = new String[]{
                "37fce695333914294e4b8e37ed76e0f1687cb9f6fce96b926af03220906015a9",
                "f017b51270e6729f69b46f6193f026712dedb5797ff5baf61e8c68434b108e5c",
                "c3aa7afd1c1edd5c917c93d4bfd068fe8075b4273781ba600de7b47c14fdf850",
                "258863698dfd81126b99dd40fd6d0a6b87025715fb27b03f45a6750bef2b6e22",
                "2c49d9a4b65e58013336245056141fac5abdf25848bc983c2cd6e0fd06384d8b",
                "ca0ff2ece04b7b9bbe0274254b6785c9db4280f04b55cbc9a75b92cb5998f6ef",
                "077d12c552f426ec9eb73230e63e2297508e6e8b9f208e69295f35217d274e30",
                "8f53f546ca3f37ae8dd4e30699f69f91bbe697a02e1a5678760a4a0c60dde541",
                "f682a5fdf8ca1209408017760311ff0906f2ebfbf6e793d1143b7a6f5d0ac826",
                "683bba5f017ca6bf1d62d4d342f6eee4c6913449ab14eb5dc16781fba44b279b",
                "e828e13427e7baa34b9b627d7e6d36a31c3477610b13f4b9fdb26e73d42d1a0c",
                "e4a14811bf2f86260ee64a48b04f9cbc9fc1923b32926c7b7c9530de6a0e9f93",
                "573352d660b69284b70920162ba565f45120ee3f800c19eba9724ac0ca940dfe",
                "431ee10ad4371d925addfa68f80c72447991135fb9cefdc71524ec1f5050142f",
                "4f5ad3b9492e63743ea87ca52c465da118a1e211da97d9092b276dfa707def58",
                "c92b8efe535bccd0987056ab8bcb3344b1cc59d755955c050808344f3993c58e"
        };
        for(int i = 1; i <= 16; i++)
        {
            System.out.println(str[i-1]);
            mp1.put(i,mp.get(str[i-1]));
        }
        return mp1;
    }
}
