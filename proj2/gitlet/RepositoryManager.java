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
     * @return true if success
     */
    public boolean Init(){
        if(this.repository.exists()){
            return false;
        }
        this.repository.Init();
        return true;
    }
}
