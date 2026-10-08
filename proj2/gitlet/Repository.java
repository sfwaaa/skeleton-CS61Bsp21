package gitlet;

import java.io.File;
import java.nio.file.Path;
import java.util.*;

// TODO: any imports you need here

/*Note : Stage area is complicated enough to have a separate class manage it.
But in fact we can also create more classes to handle io,refs etc.
And let this class Repository be a Facade that just calls other classes' interface
to provide the abstraction(concept) of the "repository".
But here I just mixed up nearly all things(except stage area) in this class,
so this is not a good design...
Anyway,that's why we need to REFACTOR our programs...But now I'm lazy:>
*/
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
    /**
     * Latest commit of the current branch
     */
    private Commit currentCommit;
    private String currentCommitSha1;

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
     * Note that it stores the relative path of a branch, E.g. refs/heads/master
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
        initialCommit.map=new TreeMap<>();
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

    /**
     * Get all file name in commit,index and working directory
     * @param worktree working directory
     * @return all sorted file name
     */
    public String[] getAllFiles(File worktree){
        HashSet<String> fileSet=new HashSet<>();
        if (currentCommit==null){
            retrieveLatestCommit();
        }

        //REALLY UGLY...
        fileSet.addAll(this.currentCommit.map.keySet());
        fileSet.addAll(Utils.plainFilenamesIn(worktree));
        fileSet.addAll(this.stageArea.getStagedContent().keySet());

        String[] fileNames=fileSet.toArray(new String[0]);
        Arrays.sort(fileNames);
        return fileNames;
    }
    /*Blob manipulation*/

    //we only store blobs as "loose object" :
    //just a file named by sha1,pointed by some commit

    /**
     * Write a file in the working directory to the repository,returns its sha1
     * @param file the file in the working directory
     * @return the sha1 of the blob
     */
    public String writeFile(File file){
        if (file==null||!file.exists()){
            return null;
        }
        byte[] content=Utils.readContents(file);
        String sha1=Utils.sha1((Object) content);
        this.writeFile(Utils.join(objects,sha1),content);
        return sha1;
    }

    /**
     * Write the given content to the given path
     * @param filePath
     * @param content
     */
    public void writeFile(File filePath,byte[] content){
        Utils.writeContents(filePath, (Object) content);
    }

    /**
     * Remove a file(not directory) with the given path.
     * DON'T call this unless you know what you're doing!
     * @param file file path
     * @return true if deleted
     */
    public boolean removeFile(File file){
        if (file==null||!file.exists()||file.isDirectory()){
            return false;
        }
        return file.delete();
    }

    /**
     * Get the status info of the file
     * @param file file path
     * @param fileName file name
     * @return the status
     */
    public FileStatus getStatus(File file,String fileName){
        FileStatus status=new FileStatus();
        if (this.currentCommit==null){
            retrieveLatestCommit();
        }
        TreeMap<String,String> stagedContent=this.stageArea.getStagedContent();
        TreeMap<String,String> trackedContent=this.currentCommit.map;
        //quite ugly...
        String stagedSha1=stagedContent.get(fileName);

        //boolean for determining the status.
        boolean fileExist=file.exists();
        boolean stagedForDeletion=false;
        boolean stagedForAddition=false;
        boolean trackedByCommit=trackedContent.containsKey(fileName);

        //for staged content
        if (stagedSha1!=null){
            //for deletion
            if (stagedSha1.isBlank()){
                status.removalStaged=true;
                stagedForDeletion=true;
            }
            else {//for addition
                status.staged=true;
               stagedForAddition=true;
            }
        }

        //for content in the working directory
        if (!fileExist){
            //just a direct translation of the spec.
            status.removalUnstaged=stagedForAddition||
                    (!stagedForDeletion && trackedByCommit);
        }
        else {
            //just a direct translation of the spec.
            //Even more ugly...
            String fileSha1=Utils.sha1((Object) Utils.readContents(file));
            boolean differFormCommit=!fileSha1.
                    equals(trackedContent.get(fileName));
            boolean differFormIndex=!fileSha1.
                    equals(stagedContent.get(fileName));
            status.unstaged=
                    (trackedByCommit && differFormCommit && !stagedForAddition)||
                            (stagedForAddition && differFormIndex);
            status.untracked=stagedForDeletion||
                    ( !stagedForAddition && !trackedByCommit );
        }
        return status;
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
     * Return a new commit with the given message and current time.
     * It also contains the staged file in the stage area.
     * Its parent is the current commit.
     * DON'T modify it unless you know what you're doing.
     * @param message commit message
     * @return the new commit,null if nothing staged
     */
    public Commit createCommitFromIndex(String message){
        TreeMap<String,String> stagedFiles= this.stageArea.getStagedContent();
        if (stagedFiles.isEmpty()){
            return null;
        }
        if (this.currentCommit==null){
            retrieveLatestCommit();
        }
        Commit parent=this.currentCommit;

        Commit commit=new Commit(message);
        commit.parentSha1=this.currentCommitSha1;
        commit.map=new TreeMap<>();

        String fileName;
        String stagedSha1;
        //well,I don't know whether this breaks the runtime requirement...
        for(Map.Entry<String, String> parentMapping : parent.map.entrySet()){
            fileName=parentMapping.getKey();

            stagedSha1=stagedFiles.remove(fileName);
            //Not staged,stay consistent with parent commit
            if (stagedSha1==null){
                commit.map.put(fileName,parentMapping.getValue());
                continue;
            }
            //Staged for deletion,no longer track it
            if(stagedSha1.isEmpty()){
                continue;
            }
            commit.map.put(fileName,stagedSha1);
        }
        commit.map.putAll(stagedFiles);
        return commit;
    }

    /**
     * Get the latest commit of the current branch.
     * @return
     */
    public Commit getLatestCommit(){
        if (this.currentCommit !=null){
            return this.currentCommit;
        }
        retrieveLatestCommit();
        return currentCommit;
    }


    /*REF manipulation,including REFs in commits and branches*/

    /**
     * Let HEAD points to the given branch.
     * Simply assume that there's no branch like feat/login.
     * @param branch branch name,assume it's valid.
     */
    public void updateHEAD(String branch){
        this.HEADContent=Utils.join(this.heads,branch).toString();
        this.HEADContent=this.HEADContent.substring
                (this.gitletDirectory.toString().length()+1);
        //To be frank,due to the necessity to use Utils,the code in this class seems somehow strange
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
    /**
     * Let CURRENT branch points to the given commit
     * @param sha1 the sha1 of the commit
     */
    public void updateBranch(String sha1){
        File branchFile=Utils.join(this.gitletDirectory,HEADContent);
        Utils.writeContents(branchFile,sha1);
    }

    /**
     * Get current branch name
     * @return
     */
    public String getCurrentBranch(){
        return Path.of(HEADContent).getFileName().toString();
    }

    /**
     * Get all existing branched
     * @return all branch name
     */
    public String[] getAllBranch(){
        //should not be null
        return (String[]) Utils.plainFilenamesIn(this.heads).toArray();
    }
    /**
     * Get all existing branched,marking the current branch.
     * @param mark prefix of the current branch
     * @return all branch name with the current branch marked.
     */
    public String[] getAllBranch(String mark){
        //should not be null
        List<String> fileNames=Utils.plainFilenamesIn(this.heads);
        //the param "new String[0]" is rather weird to a c# programmer...
        String[] branches= fileNames.toArray(new String[0]);
        String currentBranch=this.getCurrentBranch();
        for (int i = 0; i < branches.length; i++) {
            if (branches[i].equals(currentBranch)){
                branches[i]=mark+currentBranch;
                return branches;
            }
        }
        return branches;
    }

    /*Stage Area manipulation,listed below for convenience*/

    /**
     * Stage the "modification"(add or delete) of the file
     * in the working directory.
     * If stage for deletion,delete the file before staging.
     * @param file the path of the file
     * @param fileName the name of file in the working directory.
     */
    public void stage(File file, String fileName){
        if (this.currentCommit ==null){
            retrieveLatestCommit();
        }
        String trackedSha1="";
        if (this.currentCommit.map.containsKey(fileName)){
            trackedSha1=this.currentCommit.map.get(fileName);
        }
        this.stageArea.add(file,fileName,trackedSha1);
    }

    /**
     * Try to unstage file from the stage area
     * @param fileName the name of the file in the stage area
     * @return true if unstage the file
     */
    public boolean tryUnstage(String fileName){
        return this.stageArea.restore(fileName);
    }

    /**
     * Clear stage area.
     * Don't call it unless you know what you're doing!
     */
    public void clearStageArea(){
        this.stageArea.clear();
    }

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

    /**
     * Retrieve the latest commit of the CURRENT branch,set to member variables
     */
    private void retrieveLatestCommit(){
        File branchPath=Utils.join(gitletDirectory,HEADContent);
        String commitSha1=Utils.readContentsAsString(branchPath);
        File commitPath=Utils.join(this.objects,commitSha1);
        this.currentCommit= Utils.readObject(commitPath,Commit.class);
        this.currentCommitSha1=commitSha1;
    }

}
