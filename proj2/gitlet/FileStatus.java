package gitlet;

/**
 * Mark the status of the file
 */
public class FileStatus {
    /**
     * True if the file is being tracked by the current commit.
     */
    public boolean tracked;
    /**
     * true if it's in the stage area
     */
    public boolean staged;
    /**
     * true if the file is tracked and staged for deletion
     */
    public boolean removalStaged;
    /**
     * true if some contents of it not staged
     */
    public boolean unstaged;
    /**
     * true if the file is tracked and deleted but not staged for deletion
     */
    public boolean removalUnstaged;

}
