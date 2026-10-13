package gitlet;


public class CommitSha1Pair{
    public Commit commit;
    public String sha1;//full sha1 of the commit
    public CommitSha1Pair(){
        commit=null;
        sha1=null;
    }
    public CommitSha1Pair(Commit commit,String sha1){
        this.commit=commit;
        this.sha1=sha1;
    }
}