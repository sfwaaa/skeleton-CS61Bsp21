package gitlet;

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
        if (!repository.exists(fileName)){//don't stage for deletion here
            return false;
        }
        repository.stage(fileName);
        return true;
    }

    /**
     * Unstage the file or stage it for deletion(and delete it)
     * @param fileName file name
     * @return True if unstaging it or staging it for deletion or it has been staged for deletion.
     */
    public boolean rm(String fileName){
        if (repository.isStagedForDeletion(fileName)){
            return true;
        }
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
     * Retrieve the latest version of the file and put/overwrite it in the working directory
     * @param fileName name of the file in the working directory
     * @return true the file is tracked by the commit and is retrieved ,otherwise false
     */
    public boolean checkout(String fileName){
        return this.repository.checkout(fileName);
    }
    /**
     * Retrieve version of the file in the given commit
     * and put/overwrite it in the working directory
     * @param abbrSha1 the sha1 of given commit
     * @param fileName name of the file in the working directory
     * @return true the file is tracked by the given commit and is retrieved ,otherwise false
     */
    public CheckoutFailure checkout(String abbrSha1, String fileName){
        Commit commit=this.repository.getCommitWithAbbr(abbrSha1);
        if (commit==null){
            return CheckoutFailure.COMMIT_NOT_EXIST;
        }
        if (!this.repository.checkout(commit,fileName)){
            return CheckoutFailure.FILE_NOT_EXIST;
        }
        return CheckoutFailure.SUCCEED;
    }
    /**
     * Retrieve all files tracked in the given commit,
     * delete all files untracked in the given commit
     * and let HEAD point to the branch
     * @param branchName the branch to check out
     */
    public CheckoutFailure checkoutBranch(String branchName){
        if (this.repository.getCurrentBranch().equals(branchName)){
            return CheckoutFailure.CHECKOUT_CURRENT_BRANCH;
        }
        Commit checkoutCommit=this.repository.getLatestCommit(branchName);
        if (checkoutCommit==null){
            return CheckoutFailure.BRANCH_NOT_EXIST;
        }

        String[] fileNames=this.repository.filesInWorkingDirectory();
        Commit commit =this.repository.getLatestCommit();

        if (!canApply(fileNames,commit,checkoutCommit)){
            return CheckoutFailure.OVERWRITTEN;
        }
        this.repository.checkout(checkoutCommit);
        this.repository.clearStageArea();
        this.repository.updateHEAD(branchName);
        return CheckoutFailure.SUCCEED;
    }

    /**
     * Retrieve all files tracked in the given commit,
     * delete all files untracked in the given commit
     * and let branch pointer point to this sha1
     * @param abbrSha1 abbreviated sha1 of the commit
     */
    public CheckoutFailure reset(String abbrSha1){
        Commit checkoutCommit=this.repository.getCommitWithAbbr(abbrSha1);
        if (checkoutCommit==null){
            return CheckoutFailure.COMMIT_NOT_EXIST;
        }
        String[] fileNames=this.repository.filesInWorkingDirectory();
        Commit commit =this.repository.getLatestCommit();
        if (!canApply(fileNames,commit,checkoutCommit)){
            return CheckoutFailure.OVERWRITTEN;
        }
        this.repository.checkout(checkoutCommit);
        this.repository.clearStageArea();
        this.repository.updateBranch(repository.fullSha1OfCommit(abbrSha1));
        return CheckoutFailure.SUCCEED;
    }

    /**
     * Merge current branch with the given branch
     */
    public MergeStatus merge(String branchName){
        //Retrieve commits
        if (repository.hasStagedContent()){
            return MergeStatus.UNCOMMITTED_CHANGE_EXIST;
        }
        CommitSha1Pair branchPair=this.repository.getLatestCommitSha1Pair(branchName);
        if (branchPair==null){
            return MergeStatus.BRANCH_NOT_EXIST;
        }
        return merge(branchPair,branchName);
    }

    /**
     * Merge current branch with the given remote branch
     */
    public MergeStatus merge(String remoteName,String remoteBranchName){
        if (repository.hasStagedContent()){
            return MergeStatus.UNCOMMITTED_CHANGE_EXIST;
        }
        CommitSha1Pair branchPair=this.repository.
                getLatestCommitSha1Pair(remoteName,remoteBranchName);
        if (branchPair==null){
            return MergeStatus.BRANCH_NOT_EXIST;
        }
        return merge(branchPair,remoteName+"/"+remoteBranchName);
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
        commit(commit);
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
        CommitSha1Pair pair=null;
        while (iterator.hasNext()){
            pair=iterator.next();
            logCommit(pair.commit,pair.sha1);
        }
    }
    public boolean find(String message){
        boolean found=false;
        CommitIterator iterator=this.repository.commitIterator();
        CommitSha1Pair pair=null;
        while (iterator.hasNext()){
            pair=iterator.next();
            if (pair.commit.message.equals(message)){
                System.out.println(pair.sha1);
                found=true;
            }
        }
        return found;
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

    /**
     * Add remote information
     * @return false if remote name already exists
     */
    public boolean addRemote(String remoteName,String remotePath){
        return repository.addRemote(remoteName,remotePath);
    }
    /**
     * remove remote information
     * @param remoteName
     * @return false if remote name doesn't exist
     */
    public boolean removeRemote(String remoteName){
        return repository.removeRemote(remoteName);
    }

    /**
     * Push the current to the remote branch(created if necessary)
     */
    public PushFailure push(String remoteName,String remoteBranch){
        //three things to do in push:
        //Get commits to push,push the commits,"reset" remote
        Path remoteRepositoryPath=this.repository.
                getRemoteRepositoryPath(remoteName);
        if (remoteRepositoryPath==null){
            return PushFailure.REMOTE_NOT_FOUND;
        }
        Repository remote=new Repository(remoteRepositoryPath.getParent());
        if(!remote.exists()){
            return PushFailure.REMOTE_NOT_FOUND;
        }
        Commit[] futureCommits=this.repository.
                futureCommitsOf(remote,remoteBranch);
        if (futureCommits==null){
            return PushFailure.NEED_PULL;
        }
        this.repository.pushCommits(futureCommits,remote);
        //fast-forward
        if (!remote.existsBlob(remoteBranch)){
            remote.createBranch(remoteBranch);
        }
        else if (remoteBranch.equals(remote.getCurrentBranch())){
            remote.checkout(futureCommits[0]);//reset if push to the remote HEAD
        }
        remote.updateBranch(remoteBranch,
                this.repository.getLatestCommitSha1());
        return PushFailure.SUCCEED;
    }

    /**
     * Fetch remote commit to the local repository
     * @param remoteName
     * @param remoteBranchName
     * @return
     */
    public FetchFailure fetch(String remoteName,String remoteBranchName){
        Path remotePath=this.repository.getRemotePath(remoteName);
        if (remotePath==null){
            return FetchFailure.REMOTE_NOT_FOUND;
        }
        Repository remote=new Repository(remotePath.getParent());
        if (!remote.exists()){
            return FetchFailure.REMOTE_NOT_FOUND;
        }

        Commit[] fetchedCommits=getFetchCommits(remote,remoteBranchName);
        if (fetchedCommits==null){
            return FetchFailure.BRANCH_NOT_EXIST;
        }
        remote.pushCommits(fetchedCommits,this.repository);
        this.repository.updateBranch(remoteName,remoteBranchName,
                remote.getLatestCommitSha1Pair(remoteBranchName).sha1);
        return FetchFailure.SUCCEED;
    }

    private void showStatusArray(String section, Collection<String> infos){
        System.out.println("=== "+section+" ===");
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
                    ("Merge: "+
                            repository.abbreviate(commit.parentSha1)+
                            " "+
                            repository.abbreviate(commit.secondParentSha1));
        }
        System.out.println("Date: "+commit.getTimestamp());
        System.out.println(commit.message);
        System.out.println();
    }

    private void fastForward(CommitSha1Pair targetCommitSha1Pair){
        this.repository.checkout(targetCommitSha1Pair.commit);
        this.repository.updateBranch(targetCommitSha1Pair.sha1);
    }

    /**
     * Compare the file between the split point,current branch and branch.
     * And do the proper merging operation and update stage area
     * @return true if there is no merge conflict
     */
    private boolean mergeFile(String fileName,Commit splitPoint,Commit current,Commit branchCommit){
        String splitSha1=splitPoint.map.get(fileName);
        String currentSha1=current.map.get(fileName);
        String branchSha1=branchCommit.map.get(fileName);
        //empty for deletion
        if (splitSha1==null){
            splitSha1="";
        }
        if (currentSha1==null){
            currentSha1="";
        }
        boolean branchDeleted=false;
        if (branchSha1==null){
            branchDeleted=true;
            branchSha1="";
        }
        //below "modified" means update/create/delete
        boolean currentModifiedSinceSplit=!splitSha1.equals(currentSha1);
        boolean branchModifiedSinceSplit=!splitSha1.equals(branchSha1);
        boolean sameModification=branchSha1.equals(currentSha1);
        if (currentModifiedSinceSplit){
            if (branchModifiedSinceSplit){
                if (!sameModification){
                    //conflict
                    return false;
                }
            }
            //else only modified in current ,do nothing
        }
        else {//!currentModifiedSinceSplit
            if (branchModifiedSinceSplit){
                if (branchDeleted){// deleted in the given branch
                    repository.removeFile(fileName);
                    repository.stage(fileName);//stage for deletion
                }
                else {//updated only in the given branch
                    repository.checkout(branchCommit,fileName);
                    repository.stage(fileName);
                }
            }
            //else both no modification,do nothing
        }
        return true;
        /*pseudocode,"modified" means update/create/delete
         if "cur" modified since "sp"{
            if "bra" modified since "sp"{
                 if not modified equivalently{
                    conflict
                    }
                 else{
                    modified equivalently,do nothing
                 }
             }
             else {
                  do nothing
             }
         }
         else{"cur" NOT modified since "sp"
              if "bra"  modified since "sp"{
                  if "bra" delete it{
                      stage it for deletion
                  }
                  else{
                      checkout "bra",stage it
                  }
             }
             else {
                both not modified,
                do nothing
             }
         }

         */
    }

    /**
     * Check if there are some untracked files that are not present here
     * but exist in the target commit
     * @param fileNames all file to check
     * @param currentCommit
     * @param targetCommit
     * @return true if there won't be any untracked files
     * that would be overwritten when apply changes in the target commit
     * to the working directory
     */
    private boolean canApply(String[] fileNames, Commit currentCommit, Commit targetCommit){
        for(String file : fileNames){
            if (!currentCommit.map.containsKey(file)&&targetCommit.map.containsKey(file)){
                return false;
            }
        }
        return true;
    }

    private void commit(Commit commit){
        String sha1 = this.repository.writeCommit(commit);
        this.repository.updateBranch(sha1);
        this.repository.clearStageArea();
    }
    /**
     * Merge the current branch with the given branch commit
     * @param branchPair branch commit sha1 pair
     * @param branchName branch name,only used in commit message
     */
    private MergeStatus merge(CommitSha1Pair branchPair,String branchName){
        Commit branchCommit=branchPair.commit;
        String currentBranchName=repository.getCurrentBranch();
        if (currentBranchName.equals(branchName)){
            return MergeStatus.MERGE_ITSELF;
        }
        CommitSha1Pair currentPair=repository.getLatestCommitSha1Pair();
        Commit currentCommit=currentPair.commit;
        if(!canApply(repository.filesInWorkingDirectory(),currentCommit,branchCommit)){
            return MergeStatus.OVERWRITTEN;
        }
        //check ancestor or fast-forward
        CommitSha1Pair splitPair=this.repository.getSplitPoint(branchPair,currentPair);
        Commit splitPoint =splitPair.commit;
        String splitSha1=splitPair.sha1;
        if (splitSha1.equals(branchPair.sha1)){
            return MergeStatus.MERGE_ANCESTOR;
        }
        else if (splitSha1.equals(currentPair.sha1)){
            this.fastForward(branchPair);
            return MergeStatus.FAST_FORWARD;
        }
        //Merging
        MergeStatus status=MergeStatus.SUCCEED;
        HashSet<String> fileNames=new HashSet<>(splitPoint.map.keySet());
        fileNames.addAll(currentCommit.map.keySet());
        fileNames.addAll(branchCommit.map.keySet());
        for(String fileName : fileNames){
            if(!mergeFile(fileName,splitPoint,currentCommit,branchCommit)){
                status=MergeStatus.CONFLICT;
                repository.writeConflict
                        (fileName,currentCommit.map.get(fileName),branchCommit.map.get(fileName));
                repository.stage(fileName);
            }
        }
        Commit commit=this.repository.createCommitFromIndex
                ("Merged "+branchName+" into "+currentBranchName+".");
        commit.secondParentSha1=branchPair.sha1;
        commit(commit);
        return status;
    }

    /**
     * Get all commits that need to be fetched from the remote
     * compared to the current repository
     * @param remote remote repo
     * @param remoteBranchName remote branch name
     * @return the commits need to be fetched,descending by timestamp,
     * null if not found
     */
    private Commit[] getFetchCommits(Repository remote,String remoteBranchName){
        ArrayList<Commit> fetchedCommits=new ArrayList<>();
        if (!remote.existsBranch(remoteBranchName)){
            return null;
        }
        CommitSha1Pair pair=remote.getLatestCommitSha1Pair(remoteBranchName);
        Commit current =pair.commit;
        String currentSha1=pair.sha1;
        while (!this.repository.existsCommit(currentSha1)){
            fetchedCommits.add(current);
            currentSha1=current.parentSha1;
            current=remote.getParentCommit(current,0);
        }
        return fetchedCommits.toArray(new Commit[0]);
    }
}
