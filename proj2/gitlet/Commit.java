package gitlet;


import java.io.Serializable;
import java.util.Date;
import java.util.Formatter;
import java.util.Locale;
import java.util.TreeMap;

/**
 * Represents a gitlet commit object
 * that just contains what a gitlet commit should have.
 * It's immutable since created.
 *  @author TODO
 */
public class Commit implements Serializable,Dumpable {
    /* The needed message of this Commit. */
    public String message;
    /**
     * Timestamp , don't directly set it unless you know what you're doing.
     */
    public Date timestamp;

    /*parent refs*/
    public String parentSha1;
    public String secondParentSha1;/*Only consider two-branch merging*/

    //in real git,commit contains the sha1 of the "tree",
    //which stores the mapping from file name to its sha1
    /**
     * A mapping from file name to sha1.
     * note that in real git, this is responsible for "tree",
     * but IN GITLET WE DON'T DEAL WITH SUBDIRECTORIES
     */
    public TreeMap<String,String> map;

    /**
     * Create a new commit with the given time.
     * @param time the milliseconds since January 1, 1970, 00:00:00 GMT
     */
    public Commit(long time){
        this.timestamp=new Date(time);
        this.parentSha1=null;
        this.secondParentSha1=null;
    }
    /**
     * Create a new commit with current Time.
     */
    public Commit(){
        this(System.currentTimeMillis());
    }
    /**
     * Create a new commit with current Time and given commit message.
     */
    public Commit(String message){
        this();
        this.message=message;
    }
    /**
     * Create a new commit with current Time, given commit message and given parent.
     */
    public Commit(String message,String parentSha1){
        this(message);
        this.parentSha1=parentSha1;
    }


    /*
     * Assign current time to this commit.

    public void setCurrentTime(){
        //make it immutable
        if (timestamp ==null){
            timestamp = new Date();
        }
    }
     */

    /**
     * Get the timestamp string to display.
     * @return the proper timestamp string
     */
    public String getTimestamp(){
        Formatter formatter=new Formatter(Locale.ENGLISH);
        //eg Sat Oct 3 15:07:58 2026 +0800
        formatter.format
                ("%ta %tb %tm %tT %tY %tz", timestamp, timestamp, timestamp, timestamp, timestamp, timestamp);
        String displayString=formatter.toString();
        formatter.close();
        return displayString;
    }
    public long getTime(){
        return this.timestamp.getTime();
    }

    @Override
    public void dump() {
        //helpful for debugging
        System.out.println(this.getTimestamp());
        System.out.println(this.message);
        System.out.println();
        System.out.println("parent sha1:"+parentSha1);
        System.out.println("secondParentSha1 sha1:"+secondParentSha1);
        System.out.println();
        System.out.println(map.toString());
    }
}
