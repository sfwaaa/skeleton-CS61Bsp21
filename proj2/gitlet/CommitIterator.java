package gitlet;

import java.io.File;
import java.util.Iterator;

/**
 * Iterate through all commits
 */
public class CommitIterator implements Iterator<Commit> {
    private String[] sha1s;
    private File commitsDirectory;
    private int i;
    public CommitIterator(File commitsDirectory){
        sha1s=Utils.plainFilenamesIn(commitsDirectory).toArray(new String[0]);
        i=0;
        this.commitsDirectory=commitsDirectory;
    }

    @Override
    public boolean hasNext() {
        return i<sha1s.length;
    }

    @Override
    public Commit next() {
        File fcommit=Utils.join(commitsDirectory,sha1s[i]);
        Commit commit=Utils.readObject(fcommit,Commit.class);
        i++;
        return commit;
    }

    /**
     * sha1 paired with next(),call it before calling sha1
     * @return sha1 of the next()
     */
    public String nextSha1(){
        return sha1s[i];//of course, we should declare a new class "commit sha1 pair"
        // to get a better abstraction(encapsulation),but ... I'm lazy:<
    }
}
