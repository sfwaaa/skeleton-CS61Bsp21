package gitlet;

import java.io.File;

// TODO: any imports you need here

/**
 * Represents a gitlet repository that stores objects(including commits & blobs)
 * and REFs. It also provides methods
 * to write/read these objects and modify these REFs.
 * NOTE : IN GITLET WE DON'T DEAL WITH SUBDIRECTORIES!!!
 *  @author TODO
 */
public class Repository {


    /*IO*/

    /**
     * The .gitlet directory.
     * Let this be public when debugging.
     * */
    private final File gitletDirectory;
    /**
     * Is the repository initialized (whether there exists .gitlet).
     */
    private Index stageArea;

    /*Objects*/

    /**
     * The directory objects/ that stores all objects, including blobs and commits.
     * Note that files are simply named by its sha1
     */
    private final File objects;

    /*REFs*/
    /**
     * The directory refs/ that stores all REFs
     */
    private final File refs;
    /**
     * The directory refs/heads that stores local branches.
     */
    private final File heads;
    /**
     * The file of the pointer that points to current branch.
     */
    private final File HEAD;
    /**
     * The pointer that points to current branch,
     * it's always valid after set up.
     * Note : it stores the relative path of a branch, E.g. refs/heads/master
     */
    private String HEADContent;


    /**
     * Initialize a repository that is ready to be manipulated.
     * @param repoDir the .gitlet directory
     */
    public Repository(File repoDir){
        this.gitletDirectory=repoDir;
        this.stageArea=new Index(Utils.join(repoDir,"index"),this);

        objects=Utils.join(repoDir,"objects");
        refs=Utils.join(repoDir,"refs");
        heads=Utils.join(refs,"heads");
        HEAD=Utils.join(repoDir,"HEAD");

        if (this.exists()){
            retrieveRefs();
        }
    }


    /**
     * Determines whether the gitlet repository exists.
     * @return true if existing.
     */
    public boolean exists(){
        return this.gitletDirectory.exists();
    }

    /**
     * Create a gitlet repository with a default commit.
     * If a gitlet repository has already existed here, do nothing.
     */
    public void Init(){
        if(this.exists()){
            return;
        }

        //set up .gitlet directories
        this.gitletDirectory.mkdir();
        objects.mkdir();
        refs.mkdir();
        heads.mkdir();
        //add initial commit

        Commit initialCommit=new Commit(0);
        initialCommit.message="initial commit";//just be lazy to hard-code.
        String sha1 = writeCommit(initialCommit);

        //set up REFs(master branch and HEAD)
        updateBranch("master",sha1);//just be lazy to hard-code.
        updateHEAD("master");
    }

    /**
     * Get the directory in which blobs are stored.
     * @return the objects directory
     */
    public File getObjectsDirectory(){
        return this.objects;
    }

    /*Blob manipulation*/

    //we only store blobs as "loose object" :
    //just a file named by sha1,pointed by some commit

    /**
     * Write a file to the repository,returns its sha1
     * @param file the file to write
     * @return the sha1 of the blob
     */
    public String writeFile(File file){
        return null;
    }
    public void removeFile(File file){
        //Utils.restrictedDelete("aa");
    }

    /*Commit manipulation*/

    /**
     * Write the given commit to file
     * @param commit the new commit,shouldn't be null.
     * @return the sha1 of the new commit for reference.
     */
    public String writeCommit(Commit commit){
        byte[] serializedCommit=Utils.serialize(commit);
        String sha1=Utils.sha1((Object) serializedCommit);
        File commitFile=Utils.join(this.objects,sha1);
        Utils.writeContents(commitFile, (Object) serializedCommit);
        return sha1;
    }

    /**
     * Return a new commit with the given message and current time,
     * DON'T modify it unless you know what you're doing.
     * @param message commit message
     * @return the new commit
     */
    public Commit createCommit(String message){
        return new Commit(message);
    }

    /*REF manipulation,including REFs in commits and branches*/

    /**
     * Let HEAD points to the given branch.
     * Simply assume that there's no branch like feat/login.
     * @param branch name,assume it's valid.
     */
    public void updateHEAD(String branch){
        this.HEADContent=Utils.join(this.heads,branch).toString();
        this.HEADContent=this.HEADContent.substring
                (this.gitletDirectory.toString().length()+1);
        Utils.writeContents(HEAD,this.HEADContent);
    }

    /**
     * Let the given branch points to the given commit
     * @param branch branch name
     * @param sha1 the sha1 of the commit
     */
    public void updateBranch(String branch,String sha1){
        File branchFile=Utils.join(this.heads,branch);
        Utils.writeContents(branchFile,sha1);
    }

    /*Stage Area manipulation,listed below for convenience*/


    /*Private*/

    /**
     * Initialize all refs,call it only when the repo is initialized.
     */
    private void retrieveRefs(){
        if (!this.HEAD.exists()){
            return;
        }
        this.HEADContent=Utils.readContentsAsString(this.HEAD);
    }
}
