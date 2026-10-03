package gitlet;

// TODO: any imports you need here


import java.util.Date;
import java.util.Formatter;
import java.util.Locale;

/**
 * Represents a gitlet commit object
 * that just contains what a gitlet commit should have.
 * It's immutable since created.
 *  @author TODO
 */
public class Commit {
    /** The message of this Commit. */
    public String message;
    public String sha1;
    /**
     * Timestamp , don't directly set it
     */
    public Date time;
    public String parentSha1;

    /**
     * Assign current time to this commit.
     */
    public void setCurrentTime(){
        //make it immutable
        if (time==null){
            time = new Date();
        }
    }

    /**
     * Get the timestamp string to display.
     * @return the proper timestamp string
     */
    public String getTimestamp(){
        Formatter formatter=new Formatter(Locale.ENGLISH);
        //eg Sat Oct 3 15:07:58 2026 +0800
        formatter.format
                ("%ta %tb %tm %tT %tY %tz",time,time,time,time,time,time);
        String displayString=formatter.toString();
        formatter.close();
        return displayString;
    }
}
