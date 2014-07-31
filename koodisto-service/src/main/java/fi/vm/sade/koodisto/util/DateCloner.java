package fi.vm.sade.koodisto.util;

import java.util.Date;

public class DateCloner {

    public static Date clone(Date d) {
        if(d != null){
            return new Date(d.getTime());
        }
        return null;
    }

}
