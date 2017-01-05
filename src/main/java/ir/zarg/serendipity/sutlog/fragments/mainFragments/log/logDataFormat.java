package ir.zarg.serendipity.sutlog.fragments.mainFragments.log;

import java.io.Serializable;

/**
 * Created by serendipity on 7/10/16.
 */

public class logDataFormat implements Serializable{
     String login_success;
    String download;
    String upload;
    String login_time;
    String logout_time;
    String duration;
    public String ip;
    String mac;
    String kill_reason;
    Boolean new_date;


}
