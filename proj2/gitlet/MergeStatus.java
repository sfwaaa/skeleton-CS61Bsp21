package gitlet;

public enum MergeStatus {
    /**
     *  There exists staged content in index
     */
    UNCOMMITTED_CHANGE_EXIST,
    BRANCH_NOT_EXIST,
    MERGE_ITSELF,
    OVERWRITTEN,
    /**
     * Given branch is an ancestor of the current branch.
     */
    MERGE_ANCESTOR,
    FAST_FORWARD,
    MERGE_WITHOUT_CONFLICT,
    /**
     * Encounter merge conflict
     */
    CONFLICT,
    SUCCEED
}
