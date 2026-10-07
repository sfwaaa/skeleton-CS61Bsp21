package gitlet;

import jdk.jshell.Snippet;
import org.checkerframework.checker.units.qual.A;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 * Provides commands to manipulate the .gitlet repository.
 * It should only interact with class Repository.java
 * and only care about working directory.
 */
public class RepositoryManager {



    private final File workingDirectory;

    private Repository repository;

    /**
     * Initialize a repository manager
     * @param workingDirectory The workingDirectory where
     *                         gitlet commands are carried out.
     */
    public RepositoryManager(File workingDirectory){
        this.workingDirectory=workingDirectory;
        this.repository=new Repository
                (Utils.join(workingDirectory, ".gitlet"));
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
        File path=Utils.join(workingDirectory,fileName);
        if (!path.exists()){
            return false;//here we don't handle "stage for deletion"
        }
        repository.stage(path,fileName);
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
        File file=Utils.join(this.workingDirectory,fileName);
        this.repository.removeFile(file);
        this.repository.stage(file,fileName);
        return true;
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

    public boolean existRepository(){
        return this.repository.exists();
    }
    public void printStatus(){
        //Note : real Git is really efficient due to its .git/index,
        //which stores much info about the file that helps to tell
        //whether the file is modified.
        //The following code is rather UGLY and INEFFICIENT due to my inability...
        ArrayList<String> staged=new ArrayList<>();
        ArrayList<String> removed=new ArrayList<>();
        ArrayList<String> unstaged=new ArrayList<>();
        ArrayList<String> untracked=new ArrayList<>();

        FileStatus status;
        for(String fileName : Utils.plainFilenamesIn(workingDirectory)){
            status=repository.getStatus
                    (Utils.join(workingDirectory,fileName),fileName);
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
        showStatusArray("Staged Files",staged);
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
}
