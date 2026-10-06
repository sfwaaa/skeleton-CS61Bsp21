package gitlet;

import java.io.File;


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
        repository.stageFile(path,fileName);
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
}
