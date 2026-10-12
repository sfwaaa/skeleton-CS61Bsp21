package gitlet;

import java.io.File;
import java.nio.file.Path;
import java.util.Iterator;

/**
 * Iterate through all commits
 */
public class CommitIterator implements Iterator<CommitSha1Pair> {

    private Path commitsDirectory;
    private RepositoryIO io;
    private boolean done;

    private int dirIndex;
    private String[] sha1DirecotryNames;

    private int sha1Index=0;
    private String[] commitsNames;

    public CommitIterator(Path commits, RepositoryIO io){
        this.io=io;
        sha1DirecotryNames =io.namesIn(commits);
        dirIndex =0;
        sha1Index=0;
        this.commitsDirectory=commits;
        done= sha1DirecotryNames.length==0;
    }

    @Override
    public boolean hasNext() {
        return !done;
    }

    @Override
    public CommitSha1Pair next() {
        if (commitsNames==null){
            commitsNames=io.namesIn(
                    io.joinPath(commitsDirectory,sha1DirecotryNames[dirIndex]));
            sha1Index=0;
        }
        CommitSha1Pair pair=new CommitSha1Pair();
        pair.sha1=sha1DirecotryNames[dirIndex]+commitsNames[sha1Index];
        pair.commit=io.readCommit(pair.sha1);
        sha1Index++;
        if (sha1Index>=commitsNames.length){
            dirIndex++;
            if (dirIndex>=sha1DirecotryNames.length){
                this.done=true;
            }
            commitsNames=null;
        }
        return pair;
    }


}
