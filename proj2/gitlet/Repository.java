package gitlet;

import java.io.File;

// TODO: any imports you need here

/**
 * Represents a gitlet repository that stores objects(including commit & blobs)
 * and REFs. It also provides methods
 * to write/read these objects and modify these REFs.
 * NOTE : IN GITLET WE DON'T DEAL WITH SUBDIRECTORIES!!!
 *  @author TODO
 */
public class Repository {

    /**
     * Represents Stage Area, provides methods to manipulate the stage area.
     */
    private class Index{

    }

    /**
     * The .gitlet directory.
     * Let this be public when debugging.
     * */
    private final File gitletDirectory;
    /**
     * Is the repository initialized (whether there exists .gitlet).
     */
    private boolean initialized;

    /**
     * Initialize a repository that is ready to be manipulated.
     * @param repoDir the .gitlet directory
     */
    public Repository(File repoDir){
        this.gitletDirectory=repoDir;
    }


    /*Blob manipulation*/


    /*Commit manipulation*/


    /*REF manipulation,including REFs in commits and branches*/


    /*Stage Area manipulation,listed below for convenience*/


}
