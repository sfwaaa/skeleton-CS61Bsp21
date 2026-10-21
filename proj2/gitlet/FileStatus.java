package gitlet;

/**
 * Mark the status of the file
 */
public class FileStatus {

    /**
     * True if it's in the stage area.
     * It doesn't matter whether the file is being tracked.
     */
    public boolean staged;
    /**
     * True if the file is staged for deletion
     */
    public boolean removalStaged;
    /**
     * true if an existing file has some content different from commit or stage area,that is:
     * tracked in the current commit, changed in the working directory, but not staged;
     * staged for addition, but with different contents than in the working directory;
     */
    public boolean unstaged;

    /**
     * True if the file doesn't exist(deleted) and
     * the deletion is different from what commit or stage area record,that is:
     * staged for addition, but deleted in the working directory;
     * not staged for removal, but tracked in the current commit and deleted from the working directory.
     */
    public boolean removalUnstaged;
    /**
     * True if the file exists but is:
     * not staged for addition and not being tracked;
     * staged for removal.
     * It is NOT equivalent to "not tracked by the current commit"
     */
    public boolean untracked;

    /**
     * Create a status obj with all value set to false.
     */
    public FileStatus(){
        this.untracked=false;
        this.unstaged=false;
        this.removalStaged=false;
        this.removalUnstaged=false;
        this.staged=false;
    }
}
