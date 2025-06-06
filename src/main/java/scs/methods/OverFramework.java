package scs.methods;

import scs.controller.OperWaitQueue;
import scs.methods.Ensemble.Ensemble;
import scs.methods.FaaSCache.GreedyDual;
import scs.methods.Ice.IceBreak;
import scs.methods.LCS.Lcs;
import scs.methods.LRFU.LFU;
import scs.methods.LRFU.LRU;
import scs.methods.MultiContainer.SPFaaS;
import scs.methods.Spes.Spes;
import scs.methods.TCN.Tcn;
import scs.methods.Wild.Wild;
import scs.methods.Zyy.ZyyFaaS;
import scs.methods.ZyyCache.Hybrid;

/**
 * @ClassName OverFramework
 * @Description ...
 * @Author @WZhang
 * @Date 2023/3/8 18:49
 * @Version 1.0
 */
public class OverFramework {
    private static Integer flag;

    public OverFramework() {
        flag = 0;
    }

    public static void setFlag(Integer flag1) {
        flag = flag1;
    }

    public Integer getFlag() {
        return flag;
    }

    public static void run(Integer sid, Integer methodId) throws InterruptedException {
        setFlag(methodId);

        /**
         * flag = 0: base: release all functions with the keep-alive state
         * flag = 1: LFU
         * flag = 2: LRU
         */
        switch (flag) {
            case 0:
                OperWaitQueue.execQueueFunc();
                OperWaitQueue.execRequests(sid);
                break;
            case 1:
                LFU.run(sid);
                break;
            case 2:
                LRU.run(sid);
                break;
            case 3:
                Wild.run(sid);
                break;
            case 4:
                GreedyDual.run(sid);
                break;
            case 5:
                Hybrid.run(sid);
                break;
            case 6:
                ZyyFaaS.run(sid);
                break;
            case 7:
                IceBreak.run(sid);
                break;
            case 8:
                Spes.run(sid);
                break;
            case 9:
                Lcs.run(sid);
                break;
            case 10:
                Ensemble.run(sid);
                break;
            case 11:
                Tcn.run(sid);
                break;
            case 12:
                SPFaaS.run(sid);
                break;
            default:
                System.out.println("Method id is Chosen Error!");
        }
    }
}
