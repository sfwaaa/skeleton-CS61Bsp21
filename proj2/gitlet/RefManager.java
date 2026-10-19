package gitlet;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.TreeMap;

/**
 * Manage the refs in the gitlet repository.
 * Refs includes HEAD and branches and remote refs
 * (which may be managed by a new class if it's complicated enough)
 */
public class RefManager {
    private final Path gitletDirectory;
    /**
     * The directory refs/ that stores all REFs
     */
    private final Path refs;
    /**
     * The directory refs/heads that stores local branches.
     */
    private final Path heads;
    /**
     * The config file that stores basic information about the repository,
     * including remote info
     */
    private final Path config;
    /**
     * The file of the pointer that points to current branch.
     */
    private Path HEAD;
    /**
     * The pointer that points to current branch,
     * it's always valid after set up.
     * Note that it stores the relative path of a branch, E.g. refs/heads/master
     */
    private String HEADContent;

    /**
     * The directory refs/remotes that stores all remote branches
     */
    private Path remotes;
    /**
     * mapping from a remote repository name to the path of the repository
     */
    private TreeMap<String,String> remotePaths;

    private final RepositoryIO io;


    public RefManager(Path gitletRepository,RepositoryIO io){
        this.gitletDirectory=gitletRepository;
        this.io=io;
        refs=io.joinPath(gitletRepository,"refs");
        heads=io.joinPath(refs,"heads");
        HEAD=io.joinPath(gitletRepository,"HEAD");
        config=io.joinPath(gitletRepository,"config");
        remotes=io.joinPath(refs,"remotes");
        retrieveRefs();
    }

    public void initRepositoryRefs(){
        io.mkdir(refs);
        io.mkdir(heads);
        io.mkdir(remotes);
    }
    /*Local*/
    public boolean exists(String branchName){
        return io.exists(io.joinPath(heads,branchName));
    }
    /**
     * Let HEAD points to the given branch.
     * Simply assume that there's no branch like feat/login.
     * @param branch branch name,assume it's valid.
     * @return true if branch exists and is updated.
     */
    public boolean updateHEAD(String branch){
        Path branchPath= io.joinPath(this.heads,branch);
        if (!io.exists(branchPath)){
            return false;
        }
        this.HEADContent=branchPath.toString();
        this.HEADContent=this.HEADContent.substring
                (this.gitletDirectory.toString().length()+1);
        io.writeToFile(io.stringToByteArray(HEADContent),HEAD);
        return true;
    }
    /**
     * Let the given branch points to the given commit,create a new branch if necessary
     * @param branch branch name
     * @param sha1 the sha1 of the commit
     */
    public void updateBranch(String branch,String sha1){
        Path branchFile=io.joinPath(this.heads,branch);
        io.writeToFile(io.stringToByteArray(sha1),branchFile);
    }
    /**
     * Let CURRENT branch points to the given commit
     * @param sha1 the sha1 of the commit
     */
    public void updateBranch(String sha1){
        Path branchFile=io.joinPath(gitletDirectory,HEADContent);
        io.writeToFile(io.stringToByteArray(sha1),branchFile);
    }

    /**
     * Get all existing branch name
     * @return all branch name
     */
    public String[] getAllBranch(){
        return io.plainFileNamesIn(heads);
    }
    /**
     * Get all existing branch name,marking the current branch.
     * @param mark prefix of the current branch
     * @return all branch name with the current branch marked.
     */
    public String[] getAllBranch(String mark){
        //the param "new String[0]" is rather weird to a c# programmer...
        String[] branches= io.plainFileNamesIn(heads);
        String currentBranch=currentBranch();
        for (int i = 0; i < branches.length; i++) {
            if (branches[i].equals(currentBranch)){
                branches[i]=mark+currentBranch;
                return branches;
            }
        }
        return branches;
    }
    /**
     * ONLY delete the branch pointer.Since there is no gc like real git,
     * the commits only reachable from the branch will not be collected.
     * @param branchName branch name.The branch should exist.
     * @return true if the branch is deleted,otherwise,false
     */
    public boolean removeBranch(String branchName){
        if (!exists(branchName)){
            return false;
        }
        if (branchName.equals(currentBranch())){
            return false;
        }
        return io.deleteFile(io.joinPath(heads,branchName));
    }
    /**
     * Get current branch name
     * @return current branch name
     */
    public String currentBranch(){
        return io.joinPath(HEADContent).getFileName().toString();
    }

    /**
     * Get the full sha1 of the commit that CURRENT branch points to
     * @return full sha1 of the commit
     */
    public String getLatestCommitSha1(){
        Path branchPath=io.joinPath(gitletDirectory,HEADContent);
        String commitSha1=io.byteArrayToString(io.read(branchPath));
        return commitSha1;
    }
    /**
     * Get the full sha1 of the commit that the given branch points to
     * @return full sha1 of the commit,null if the branch doesn't exist
     */
    public String getLatestCommitSha1(String branchName){
        Path branchPath=io.joinPath(heads,branchName);
        if (!io.exists(branchPath)){
            return null;
        }
        String commitSha1=io.byteArrayToString(io.read(branchPath));
        return commitSha1;
    }

    /*Remote*/

    /**
     * Save the given remote information
     * @param remoteName remote name
     * @param remotePath remote repository relative/absolute path
     * @return true if added,otherwise false
     */
    public boolean createRemote(String remoteName,Path remotePath){
        if (this.remotePaths==null){
            retrieveRemotes();
        }
        if (this.remotePaths.containsKey(remoteName)){
            return false;
        }
        this.remotePaths.put(remoteName,remotePath.toString());
        saveConfig();
        io.mkdir(io.joinPath(remotes,remoteName));
        return true;
    }

    /**
     * Remove the given remote information
     * @param remoteName remote name
     * @return true if the remote deleted,otherwise false
     */
    public boolean removeRemote(String remoteName){
        if (this.remotePaths==null){
            retrieveRemotes();
        }
        if (!remotePaths.containsKey(remoteName)){
            return false;
        }
        remotePaths.remove(remoteName);
        saveConfig();
        io.deleteDirectoryWithFiles(io.joinPath(remotes,remoteName));
        return true;
    }

    /**
     * Initialize all refs,call it only when the repo is initialized.
     */
    private void retrieveRefs(){
        if (!io.exists(HEAD)){
            return;
        }
        this.HEADContent=io.byteArrayToString(io.read(HEAD));
    }

    private void retrieveRemotes(){
        if (!io.exists(config))
        {
            this.remotePaths=new TreeMap<String,String>();
            return;
        }
        this.remotePaths=io.deserialize(io.read(config),TreeMap.class);
    }
    private void saveConfig(){
        this.io.writeToFile(io.serialize(remotePaths),config);
    }

}
