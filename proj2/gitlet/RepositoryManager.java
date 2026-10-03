package gitlet;

import java.io.File;


/**
 * Provides commands to manipulate the .gitlet repository.
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

}
