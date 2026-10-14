package gitlet;

import javax.swing.*;
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
        fileSet.addAll(List.of(io.plainFileNamesIn(workingDirectory)));
        fileSet.addAll(this.stageArea.getStagedContent().keySet());

        String[] fileNames=fileSet.toArray(new String[0]);
        Arrays.sort(fileNames);
        return fileNames;
    }
    public String[] filesInWorkingDirectory(){
        return this.io.plainFileNamesIn(workingDirectory);
    }

    /**
     * Does the given file exists in the working directory?
     * @param fileName file name
     * @return true if existing.
     */
    public boolean exists(String fileName){
        return io.exists(io.joinPath(workingDirectory,fileName));
    }
    /**
     * remove the file in the working directory
     * @param fileName file name
     * @return true if deleted
     */
    public boolean removeFile(String fileName){
        return this.io.deleteFile(io.joinPath(workingDirectory,fileName));
    }

    /**
     * Only write merging conflict to the given file
     * @param fileName
     * @param currentSha1 sha1 of the blob in the current commit,null for untracked
     * @param otherSha1 sha1 of the blob in the current commit,null for untracked
     */
    public void writeConflict(String fileName,String currentSha1,String otherSha1){
        byte[] current;
        if (currentSha1==null){
            current=new byte[0];
        }
        else {
            current=io.readBlob(currentSha1);
        }
        byte[] other;
        if (otherSha1==null){
            other=new byte[0];
        }
        else {
            other=io.readBlob(otherSha1);
        }
        io.writeToFile(io.joinPath(workingDirectory,fileName),
                "<<<<<<< HEAD",System.lineSeparator(),
                current,
                System.lineSeparator(),"=======",System.lineSeparator(),
                other,
                System.lineSeparator(),">>>>>>>");
    }

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

    /**
     * Retrieve the latest version of the file and put/overwrite it in the working directory
     * @param fileName name of the file in the working directory
     * @return true the file is tracked by the commit and is retrieved ,otherwise false
     */
    public boolean checkout(String fileName){
        if (this.currentCommit==null){
            retrieveLatestCommit();
        }
        return checkout(this.currentCommit,fileName);
    }
    /**
     * Retrieve version of the file in the given commit
     * and put/overwrite it in the working directory
     * @param commit the given commit,should not be null
     * @param fileName name of the file in the working directory
     * @return true the file is tracked by the given commit and is retrieved ,otherwise false
     */
    public boolean checkout(Commit commit,String fileName){
        TreeMap<String,String> trackedFiles=commit.map;
        if (!trackedFiles.containsKey(fileName)){
            return false;
        }
        byte[] blob=io.readBlob(trackedFiles.get(fileName));
        if (blob==null){
            return false;
        }
        io.writeToFile(blob,io.joinPath(workingDirectory,fileName));
        return true;

    }

    /**
     * Retrieve all files tracked in the given commit,
     * and delete all files untracked in the given commit.
     * CALL IT AFTER CHECKING IF THERE ARE UNTRACKED FILES
     * SINCE THIS CALLING WOULD OVERWRITE THEM.
     * @param targetCommit target commit to check out
     */
    public void checkout(Commit targetCommit){
        if (targetCommit==null){
            return;
        }
        if (currentCommit==null){
            retrieveLatestCommit();
        }
        //This seems the same ugly as implementation of status command
        HashSet<String> fileNames=new HashSet<>
                (List.of(io.plainFileNamesIn(workingDirectory)));
        fileNames.addAll(currentCommit.map.keySet());
        fileNames.addAll(targetCommit.map.keySet());
        for(String file : fileNames){
            //tracked in the current branch but untracked in the checked-out branch
            if(!checkout(targetCommit,file)&&currentCommit.map.containsKey(file)){
                this.io.deleteFile(io.joinPath(workingDirectory,file));
            }
        }
    }

    /**
     * Abbreviate the sha1
     */
    public String abbreviate(String sha1){
        return this.io.abbreviateSha1(sha1);
    }
    /*Commit manipulation*/

    /**
     * Write the given commit to file
     * @param commit the new commit,shouldn't be null.
     * @return the sha1 of the new commit for reference.
     */
    public String writeCommit(Commit commit){
        String sha1=io.writeCommit(commit);
        this.currentCommit=commit;
        this.currentCommitSha1=sha1;
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
     * @return the current commit of the branch
     */
    public Commit getLatestCommit(){
        if (this.currentCommit !=null){
            return this.currentCommit;
        }
        retrieveLatestCommit();
        return currentCommit;
    }
    /**
     * Get the commit that the given branch points to
     * @return the commit,null if the branch doesn't exist
     */
    public Commit getLatestCommit(String branchName){
        if (!refs.exists(branchName)){
            return null;
        }
        return io.readCommit(refs.getLatestCommitSha1(branchName));
    }
    /**
     * Get the latest commit and its sha1 of the current branch.
     * @return the current commit of the branch
     */
    public CommitSha1Pair getLatestCommitSha1Pair(){
        if (currentCommit==null){
            retrieveLatestCommit();
        }
        return new CommitSha1Pair(currentCommit,currentCommitSha1);
    }
    /**
     * Get the latest commit and its sha1 of the given branch.
     * @return the commit,null if the branch doesn't exist
     */
    public CommitSha1Pair getLatestCommitSha1Pair(String branchName){
        if (!refs.exists(branchName)){
            return null;
        }
        CommitSha1Pair pair=new CommitSha1Pair();
        pair.sha1=refs.getLatestCommitSha1(branchName);
        pair.commit= io.readCommit(pair.sha1);
        return pair;
    }
    /**
     * Get sha1 of the latest commit of the current branch.
     * @return sha1 of the commit of the current branch
     */
    public String getLatestCommitSha1(){
        if (this.currentCommit==null){
            retrieveLatestCommit();
        }
        return this.currentCommitSha1;
    }

    /**
     * Return the commit with the given sha1
     * @param abbrSha1 the abbreviated sha1
     * @return the commit,null if not found
     */
    public Commit getCommitWithAbbr(String abbrSha1){
        return io.readCommitWithAbbr(abbrSha1);
    }

    /**
     * Get the full sha1 of the commit
     * @param abbrCommitSha1 abbreviated sha1 of the commit
     * @return full sha1 of the commit,null if not found
     */
    public String fullSha1OfCommit(String abbrCommitSha1){
        return io.fullSha1OfCommit(abbrCommitSha1);
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
        return io.commitIterator();
    }

    /**
     * Get the split point of the two branch
     * @param branchHead the latest branch of a branch
     * @param otherBranchHead the latest branch of the other branch
     * @return the split point,shouldn't be null
     */
    public CommitSha1Pair getSplitPoint(CommitSha1Pair branchHead,CommitSha1Pair otherBranchHead){
        HashSet<String> sha1Marks=new HashSet<>();

        Commit current=branchHead.commit;
        String currentSha1=branchHead.sha1;

        Commit other=otherBranchHead.commit;
        String otherSha1=otherBranchHead.sha1;

        //Traverse through the DAG parallelly
        while (currentSha1!=null&&otherSha1!=null){
            if (!sha1Marks.add(currentSha1)){
                return new CommitSha1Pair(current,currentSha1);
            }
            //check second parent if existing
            if (current.secondParentSha1!=null&&!sha1Marks.add(current.secondParentSha1)){
                return new CommitSha1Pair
                        (getParentCommit(current,1),current.secondParentSha1);
            }

            if (!sha1Marks.add(otherSha1)){
                return new CommitSha1Pair(other,otherSha1);
            }
            if (other.secondParentSha1!=null&&!sha1Marks.add(other.secondParentSha1)){
                return new CommitSha1Pair
                        (getParentCommit(other,1),other.secondParentSha1);
            }
            currentSha1=current.parentSha1;
            otherSha1=other.parentSha1;
            current=this.getParentCommit(current,0);
            other=this.getParentCommit(other,0);
        }
        //finish remaining commits...
        while (currentSha1!=null){
            if (!sha1Marks.add(currentSha1)){
                return new CommitSha1Pair(current,currentSha1);
            }
            if (current.secondParentSha1!=null&&!sha1Marks.add(current.secondParentSha1)){
                return new CommitSha1Pair
                        (getParentCommit(current,1),current.secondParentSha1);
            }
            currentSha1=current.parentSha1;
            current=this.getParentCommit(current,0);
        }
        while (otherSha1!=null){
            if (!sha1Marks.add(otherSha1)){
                return new CommitSha1Pair(other,otherSha1);
            }
            if (other.secondParentSha1!=null&&!sha1Marks.add(other.secondParentSha1)){
                return new CommitSha1Pair
                        (getParentCommit(other,1),other.secondParentSha1);
            }
            otherSha1=other.parentSha1;
            other=this.getParentCommit(other,0);
        }

        return null;
    }

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
     * Let the given branch points to the given commit if the branch exists
     * @param branch branch name
     * @param sha1 the sha1 of the commit
     */
    public void updateBranch(String branch,String sha1){
        if (!refs.exists(branch)){
            return;
        }
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
        refs.updateBranch(branchName,this.currentCommitSha1);
        return true;
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
     * if the file is currently staged for addition or deletion
     * @param fileName the name of the file in the stage area
     * @return true if unstage the file
     */
    public boolean tryUnstage(String fileName){
        return this.stageArea.restore(fileName);
    }

    /**
     * Is the given file staged for deletion?
     * @param fileName file name in the working directory
     * @return true if it's staged for deletion,otherwise false
     */
    public boolean isStagedForDeletion(String fileName){
        TreeMap<String,String> stagedFiles=this.stageArea.getStagedContent();
        String sha1= stagedFiles.get(fileName);
        return "".equals(sha1);
    }
    /**
     * Clear stage area.
     * Don't call it unless you know what you're doing!
     */
    public void clearStageArea(){
        this.stageArea.clear();
    }

    /**
     * Are there any staged Content
     * @return true if there are
     */
    public boolean hasStagedContent(){
        return stageArea.hasStagedContent();
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
