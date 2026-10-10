package gitlet;


import java.io.File;
import java.nio.file.Path;
import java.util.*;


/**
 * Provides commands to manipulate the .gitlet repository.
 * It should only interact with class Repository.java
 * and doesn't care much about how thing works in the repository.
 */
public class RepositoryManager {

    private final Path workingDirectory;

    private final Repository repository;

    /**
     * Initialize a repository manager
     * @param workingDirectory The workingDirectory where
     *                         gitlet commands are carried out.
     */
    public RepositoryManager(Path workingDirectory){
        this.workingDirectory=workingDirectory;
        this.repository=new Repository(workingDirectory);
    }

    /**
     * Initialize a repository if there isn't a repository
     * @return true if succeed
     */
    public boolean Init(){
        if(existRepository()){
            return false;
        }
        this.repository.Init();
        return true;
    }

    /**
     * Adds a copy of the file as it currently exists to the staging area.
     * @param fileName file name of the working directory.
     * @return false if the file doesn't exist.
     */
    public boolean add(String fileName){
        //remember we don't deal with subdirectories.
        repository.stage(fileName);
        return true;
    }

    /**
     * Unstage the file or stage it for deletion(and delete it)
     * @param fileName file name
     * @return True if unstaged or stage it for deletion
     */
    public boolean rm(String fileName){
        if (this.repository.tryUnstage(fileName)){
            return true;
        }
        Commit commit=this.repository.getLatestCommit();
        //neither staged nor tracked
        if (!commit.map.containsKey(fileName)){
            return false;
        }
        this.repository.removeFile(fileName);
        this.repository.stage(fileName);
        return true;
    }
    /**
     * Create a new branch (but not let HEAD point to it)
     * that points to the current commit
     * @param branchName new branch name
     * @return true if the given branch doesn't exist and the new branch is created.
     */
    public boolean createBranch(String branchName){
        return this.repository.createBranch(branchName);
    }

    /**
     * ONLY delete the branch pointer.Since there is no gc like real git,
     * the commits only reachable from the branch will not be collected.
     * @param branchName branch name.The branch should exist.
     * @return true if the branch is deleted,otherwise,false
     */
    public boolean removeBranch(String branchName){
        return this.repository.removeBranch(branchName);
    }

    /**
     * Does the branch exist?
     * @param branch branch name
     * @return true if existing
     */
    public boolean existsBranch(String branch){
        return this.repository.existsBranch(branch);
    }

    /**
     * Make a commit to the repository
     * @param message commit message
     * @return true if there is something to be committed in the stage area
     */
    public boolean commit(String message){
        Commit commit=this.repository.createCommitFromIndex(message);
        if(commit==null){
            return false;
        }
        String sha1 = this.repository.writeCommit(commit);
        this.repository.updateBranch(sha1);
        this.repository.clearStageArea();
        return true;
    }
    public void log(){
        Commit commit=this.repository.getLatestCommit();
        String sha1=this.repository.getLatestCommitSha1();
        do {
            logCommit(commit,sha1);

            sha1=commit.parentSha1;
            commit=this.repository.getParentCommit(commit,0);
        }while (commit!=null);
    }

    /**
     * print all commits in random order
     */
    public void logAll(){
        CommitIterator iterator=this.repository.commitIterator();
        String sha1;
        Commit commit;
        while (iterator.hasNext()){
            sha1=iterator.nextSha1();
            commit=iterator.next();
            logCommit(commit,sha1);
        }
    }

    public boolean existRepository(){
        return this.repository.exists();
    }
    public void printStatus(){
        //Note : real Git is really efficient due to its .git/index,
        //which stores much info about the file that helps to tell
        //whether the file is modified.
        //The following code is rather UGLY and INEFFICIENT due to my inability...
        //The only strength of it is just "correct"...
        ArrayList<String> staged=new ArrayList<>();
        ArrayList<String> removed=new ArrayList<>();
        ArrayList<String> unstaged=new ArrayList<>();
        ArrayList<String> untracked=new ArrayList<>();

        String[] fileNames=this.repository.getAllFiles();

        FileStatus status;
        for(String fileName : fileNames){
            status=repository.getStatus(fileName);
            if (status.staged){
                staged.add(fileName);
            }
            if (status.removalStaged){
                removed.add(fileName);
            }
            if (status.removalUnstaged){
                unstaged.add(fileName+" (deleted)");
            }
            if (status.unstaged){
                unstaged.add(fileName+" (modified)");
            }
            if (status.untracked){
                untracked.add(fileName);
            }
        }
        showStatusArray("Branches",
                List.of(this.repository.getAllBranch("*")));
        showStatusArray("Staged Files", staged);
        showStatusArray("Removed Files",removed);
        showStatusArray("Modifications Not Staged For Commit",unstaged);
        showStatusArray("Untracked Files",untracked);
    }

    private void showStatusArray(String section, Collection<String> infos){
        System.out.println("== "+section+" ==");
        for(String info : infos){
            System.out.println(info);
        }
        System.out.println();
    }
    /**
     * Display the commit.
     * @param commit commit
     * @param sha1 commit sha1
     */
    private void logCommit(Commit commit,String sha1){
        if (commit==null){
            return;
        }
        System.out.println("===");
        System.out.println("commit "+sha1);
        if (commit.secondParentSha1!=null){
            System.out.println
                    ("Merge: "+commit.parentSha1+" "+commit.secondParentSha1);
        }
        System.out.println("Date: "+commit.getTimestamp());
        System.out.println(commit.message);
        System.out.println();
    }
}
