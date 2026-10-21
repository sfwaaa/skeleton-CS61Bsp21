package gitlet;

/**
 * Denote the failure case of the checkout command.
 * Use it to print failure message
 */
public enum CheckoutFailure {
    SUCCEED,
    //file not tracked by commit
    FILE_NOT_EXIST,
    //Given commit doesn't exist
    COMMIT_NOT_EXIST,
    //Given branch doesn't exist
    BRANCH_NOT_EXIST,
    //Given branch is the current branch
    CHECKOUT_CURRENT_BRANCH,
    //Checkout command would overwrite some untracked files
    OVERWRITTEN
}
