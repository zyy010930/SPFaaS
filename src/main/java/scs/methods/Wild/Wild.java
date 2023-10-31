package scs.methods.Wild;

import scs.util.repository.Repository;

public class Wild {
    public Wild(){}

    public static void run(Integer id){
        Repository.loaderMap.get(id).getAbstractJobDriver().executeJob(id, 3);
    }

}
