package gitlet;

import java.io.File;
import java.nio.file.Path;
import java.util.*;


/*
Note1 : Stage area is complicated enough to have a separate class manage it.
But in fact we can also create more classes to handle io,refs etc.
And let this class Repository be a Facade that just calls other classes' interface
to provide the abstraction(concept) of the "repository".
But here I just mixed up nearly all things(except stage area) in this class,
so this is not a good design...
Anyway,that's why we need to REFACTOR our programs...But now I'm lazy:>

Note2 : when implementing checkout command, it's inevitable to refactor the code...
So I have to bite the bullet >:(

Note3 : when refactoring, it's necessary to test its correctness,
which requires thorough testing,however here
I don't know how to write a test for this program (even the unit test)...

*/
/**
 * Represents a gitlet repository that stores objects(including commits & blobs)
 * and REFs.
 * This class is just a Facade of Index,RefManager and RepositoryIO
 * NOTE : IN GITLET WE DON'T DEAL WITH SUBDIRECTORIES!!!
 *  @author TODO
 */
public class Repository {

    /*IO*/
    /**
     * The .gitlet directory.
     * Let this be public when debugging.
     * */
    private final Path gitletDirectory;
    private final Path workingDirectory;
    /**
     * Is the repository initialized (whether there exists .gitlet).
     */
    private Index stageArea;
    private RepositoryIO io;
    private RefManager refs;

    /**
     * Latest commit of the current branch
     */
    private Commit currentCommit;
    private String currentCommitSha1;


    /**
     * Initialize a repository that is ready to be manipulated.
     * @param workingDirectory the .gitlet directory
     */
    public Repository(Path workingDirectory){
        this.workingDirectory=workingDirectory;
        this.io=new RepositoryIO(workingDirectory);
        this.gitletDirectory=io.joinPath(workingDirectory,".gitlet");
        this.refs=new RefManager(gitletDirectory,io);


        this.stageArea=new Index(gitletDirectory,this.io);

    }


    /**
     * Determines whether the gitlet repository exists.
     * @return true if existing.
     */
    public boolean exists(){
        return io.existsRepository();
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
        io.initRepository();
        refs.initRepositoryRefs();
        //add initial commit

        Commit initialCommit=new Commit(0);
        initialCommit.map=new TreeMap<>();
        initialCommit.message="initial commit";//just be lazy to hard-code.
        String sha1 = writeCommit(initialCommit);

        //set up REFs(master branch and HEAD)
        refs.updateBranch("master",sha1);//just be lazy to hard-code.
        refs.updateHEAD("master");
    }

    /*Files manipulation*/

    /**
     * Get all file name in commit,index and working directory
     * @return all sorted file name
     */
    public String[] getAllFiles(){
        HashSet<String> fileSet=new HashSet<>();
        if (currentCommit==null){
            retrieveLatestCommit();
        }

        //REALLY UGLY...
        fileSet.addAll(this.currentCommit.map.keySet());
        fileSet.addAll(Utils.plainFilenamesIn(workingDirectory.toFile()));
        fileSet.addAll(this.stageArea.getStagedContent().keySet());

        String[] fileNames=fileSet.toArray(new String[0]);
        Arrays.sort(fileNames);
        return fileNames;
    }

    /**
     * remove the file in the working directory
     * @param fileName file name
     * @return true if deleted
     */
    public boolean removeFile(String fileName){
        return this.io.deleteFile(io.joinPath(workingDirectory,fileName));
    }
    //see 6f
    /**
     * Get the status info of the file in the working directory
     * @param fileName file name
     * @return the status
     */
    public FileStatus getStatus(String fileName){
        FileStatus status=new FileStatus();
        if (this.currentCommit==null){
            retrieveLatestCommit();
        }
        TreeMap<String,String> stagedContent=this.stageArea.getStagedContent();
        TreeMap<String,String> trackedContent=this.currentCommit.map;
        //quite ugly...
        String stagedSha1=stagedContent.get(fileName);

        //boolean for determining the status.
        Path filePath=io.joinPath(workingDirectory,fileName);
        boolean fileExist=io.exists(filePath);
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
            String fileSha1=io.sha1(io.read(filePath));
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
        return io.writeCommit(commit);
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
    /**
     * Get sha1 of the latest commit of the current branch.
     * @return
     */
    public String getLatestCommitSha1(){
        if (this.currentCommit==null){
            retrieveLatestCommit();
        }
        return this.currentCommitSha1;
    }

    /**
     * Get the specified parent of the current commit
     * @param commit commit
     * @param parent index of the parent,0(or "not 1") for the first parent,1 for the second parent
     * @return the parent commit if parent exists, null if not exist
     */
    public Commit getParentCommit(Commit commit,int parent){
        String parentSha1=commit.parentSha1;
        if (parent==1){
            parentSha1=commit.secondParentSha1;
        }
        if (parentSha1==null){
            return null;
        }
        return io.readCommit(parentSha1);
    }

    public CommitIterator commitIterator(){
        return null;
    }//TODO


    /*REF manipulation,including REFs in commits and branches*/

    /**
     * Let HEAD points to the given branch.
     * Simply assume that there's no branch like feat/login.
     * @param branch branch name,assume it's valid.
     */
    public void updateHEAD(String branch){
        refs.updateHEAD(branch);
    }

    /**
     * Let the given branch points to the given commit,create a new branch if necessary
     * @param branch branch name
     * @param sha1 the sha1 of the commit
     */
    public void updateBranch(String branch,String sha1){
        refs.updateBranch(branch,sha1);
    }
    /**
     * Let CURRENT branch points to the given commit
     * @param sha1 the sha1 of the commit
     */
    public void updateBranch(String sha1){
        refs.updateBranch(sha1);
    }

    /**
     * Get current branch name
     * @return current branch name
     */
    public String getCurrentBranch(){
        return refs.currentBranch();
    }

    /**
     * Get all existing branch name
     * @return all branch name
     */
    public String[] getAllBranch(){
        //should not be null
        return refs.getAllBranch();
    }
    /**
     * Get all existing branch name,marking the current branch.
     * @param mark prefix of the current branch
     * @return all branch name with the current branch marked.
     */
    public String[] getAllBranch(String mark){
        return refs.getAllBranch(mark);
    }

    /**
     * Create a new branch (but not let HEAD point to it)
     * that points to the current commit
     * @param branchName new branch name
     * @return true if the given branch doesn't exist and the new branch is created.
     */
    public boolean createBranch(String branchName){
        if (refs.exists(branchName)){
            return false;
        }
        if(currentCommit==null){
            retrieveLatestCommit();
        }
        updateBranch(branchName,this.currentCommitSha1);
        return false;
    }

    public boolean existsBranch(String branchName){
        return refs.exists(branchName);
    }
    /**
     * ONLY delete the branch pointer.Since there is no gc like real git,
     * the commits only reachable from the branch will not be collected and removed.
     * @param branchName branch name.The branch should exist.
     * @return true if the branch is deleted,otherwise,false
     */
    public boolean removeBranch(String branchName){
        return refs.removeBranch(branchName);
    }

    /*Stage Area manipulation,listed below for convenience*/

    /**
     * Stage the "modification"(add or delete) of the file
     * in the working directory.
     * If stage for deletion,delete the file before staging.
     * @param fileName the name of file in the working directory.
     */
    public void stage(String fileName){
        if (this.currentCommit ==null){
            retrieveLatestCommit();
        }
        String trackedSha1="";
        if (this.currentCommit.map.containsKey(fileName)){
            trackedSha1=this.currentCommit.map.get(fileName);
        }
        this.stageArea.add(fileName,trackedSha1);
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
     * Retrieve the latest commit of the CURRENT branch,set to member variables
     */
    private void retrieveLatestCommit(){
        this.currentCommitSha1=refs.getLatestCommitSha1();
        this.currentCommit= io.readCommit(currentCommitSha1);
    }

}
