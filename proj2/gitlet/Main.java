package gitlet;


import java.io.File;
/*
* SUMMARY(REFLECTION):
* 1.The driver class is a little complex.
*  Had better encapsulate the switch statement into a new class
* 2.The current command error is based on enum,resulting in complexity
* 3.This gitlet implementation doesn't support multiword branch like feat/login
* due to the lack consideration, and now I don't have much to refactor my code :(
* 4.The representation of the "commit" is vague.
* The difference between the "runtime commit (info)"
* and the "commit blob (in the file system)" should be discriminated more clearly,
* rather than just a class commit.java
* */


/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author sfwaaa
 */
public class Main {
    /**
     * The current working directory.
     * */
    public static final File CWD = new File(System.getProperty("user.dir"));

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) {

        //E:\csLearn\dataStructure\gitletTest
//        RepositoryManager repoManager=new RepositoryManager
//                (Utils.join("E:","csLearn","dataStructure","gitletTest").toPath());
//        if (!repoManager.existRepository()){
//            repoManager.Init();
//            //System.out.println("A Gitlet version-control system " +
//              //      "already exists in the current directory.");
//        }
//
//        RepositoryManager remoteRepo=new RepositoryManager
//                (Utils.join("E:","csLearn",
//                        "dataStructure","remoteGitlet").toPath());
//        if (!remoteRepo.existRepository()){
//            remoteRepo.Init();
//            //System.out.println("A Gitlet version-control system " +
//            //      "already exists in the current directory.");
//        }

//        repoManager.add("f.txt");
//        repoManager.add("g.txt");
//        repoManager.commit("two files");

//        repoManager.createBranch("other");

//        repoManager.add("f.txt");
//        repoManager.rm("g.txt");
//        repoManager.add("h.txt");
//        repoManager.printStatus();
//        repoManager.commit("Add h.txt, remove g.txt, and change f.txt");
//        repoManager.log();


        //repoManager.checkoutBranch("other");
//        repoManager.add("f.txt");
//        repoManager.add("k.txt");
//        repoManager.printStatus();
//        repoManager.commit("Add k.txt and modify f.txt");
//        repoManager.log();
//
//        repoManager.checkoutBranch("master");
//        repoManager.log();

//        merge(repoManager,"other");



        //NOT BELOW
        //repoManager.createBranch("B1");
        //repoManager.createBranch("B2");

        //repoManager.checkoutBranch("B1");
        //repoManager.add("h.txt");
        //repoManager.commit("add h.txt");

//        repoManager.checkoutBranch("B2");
//        repoManager.add("f.txt");
//        repoManager.commit("add f.txt");

        //repoManager.createBranch("C1");// only f.txt
//        repoManager.rm("f.txt");
//        repoManager.add("g.txt");
//        repoManager.printStatus();

        //repoManager.commit("add g.txt,rm f.txt");
        //repoManager.checkoutBranch("B1");

        //merge(repoManager,"C1");
        //merge(repoManager,"B2");

//        repoManager.add("f1.txt");
//        repoManager.add("f2.txt");
//        repoManager.add("f3.txt");
//        repoManager.add("f4.txt");
//        repoManager.commit("first commit");

//        repoManager.createBranch("develop");
//        repoManager.checkoutBranch("develop");
//        repoManager.printStatus();

//        repoManager.add("dev01.txt");
//        repoManager.add("dev02.txt");
//        repoManager.rm("f2.txt");
//        repoManager.add("f1.txt");
//        repoManager.printStatus();

        //checkout(repoManager,"master");
//        repoManager.commit("Modified in master");
//        repoManager.log();
//        repoManager.add("f4.txt");
//        repoManager.add("dev01.txt");
//        repoManager.add("dev02.txt");
//        repoManager.printStatus();
        //checkout(repoManager,"develop");

        //repoManager.log();
        //after merge f1= f1 in dev
        //f2 deleted
        //f3 remains the same
        //f4 remains in master
        //dev01 modified the same
        //dev02 conflict

        //merge(repoManager,"develop");

        //NOT BELOW
//        repoManager.printStatus();
//        repoManager.add("f1.txt");
//        repoManager.add("f2.txt");
//        repoManager.add("f3.txt");
//        repoManager.add("f4.txt");
//        repoManager.rm("f3.txt");
//        repoManager.rm("f4.txt");
//        repoManager.printStatus();
//        repoManager.commit("Third commit");
//        repoManager.printStatus();

        /*
        repoManager.add("f1.txt");
        repoManager.add("f2.txt");
        repoManager.add("f3.txt");
        repoManager.commit("second commit");
        */
        //repoManager.add("f1.txt");
        //repoManager.rm("f6.txt");
        //repoManager.add("f6.txt");
        //repoManager.commit("Fourth commit");
        /*
        repoManager.add("f4.txt");
        repoManager.add("f1.txt");
        repoManager.add("f2.txt");
        repoManager.add("f3.txt");
        repoManager.add("f5.txt");
        repoManager.commit("First commit");
        */
        //repoManager.add("f7.txt");
        //repoManager.add("f1.txt");
        //repoManager.printStatus();
        //repoManager.createBranch("develop");
        /*
        repoManager.add("f1.txt");
        repoManager.add("f2.txt");
        repoManager.add("f3.txt");
        repoManager.add("f4.txt");
        repoManager.commit("First commit");
         */
        /*
        repoManager.add("f2.txt");
        repoManager.add("f5.txt");
        repoManager.add("f6.txt");
        repoManager.commit("Third Commit");
         */
        //modify f1,f3 in cmt2(second commit) (develop now at)
        //add f2(modified) f5&f6(untracked) in cmt3(master now at)
        //repoManager.printStatus();
        //repoManager.checkoutBranch("master");
        //repoManager.checkoutBranch("master");
        //repoManager.reset("c7026adeabac40");
        //repoManager.add("f8");
        //repoManager.printStatus();
        //repoManager.log();

        //f3 merge conflict
        //f2 deleted in develop
        //f1 only modified in dev
        //f8 added in dev but deleted in master

        //repoManager.printStatus();
        //repoManager.printStatus();
        //repoManager.log();
        //repoManager.merge("develop");

//        repoManager.addRemote
//                ("origin",
//                        "E:/csLearn/dataStructure/remoteGitlet/.gitlet");

        //repoManager.push("origin","feat");
//        repoManager.add("dev02.txt");
//        repoManager.add("f1.txt");
//        repoManager.rm("f3.txt");
//        repoManager.printStatus();

        //repoManager.push("origin","feat");
        //merge(remoteRepo,"feat");

        //remoteRepo.log();
        //repoManager.checkoutBranch("master");
        //repoManager.log();

        //pull(repoManager,"origin","master");

//        remoteRepo.add("h.txt");
//        remoteRepo.commit("add h.txt");

//        repoManager.add("k.txt");
//        repoManager.commit("add k.txt");

        //pull(repoManager,"origin","master");
//        if (true){//to make compilation succeed
//            return;
//        }
        /*Real program*/
        RepositoryManager repoManager=new RepositoryManager(CWD.toPath());
        if (args.length==0){
            System.out.println("Please enter a command.");
            return;
        }

        String command = args[0];//make it easy,just hard-code it
        switch(command) {
            case "init":
                if(!validRepoAndOperandCount(args,repoManager,false,0)){
                    return;
                }
                if (!repoManager.Init()){
                    System.out.println("A Gitlet version-control system " +
                            "already exists in the current directory.");
                }
                break;
            case "add":
                if(!validRepoAndOperandCount(args,repoManager,true,1)){
                    return;
                }
                if(!repoManager.add(args[1])){
                    System.out.println("File does not exist.");
                    return;
                }
                break;
            case "commit":
                if(!validRepoAndOperandCount(args,repoManager,true,1)){
                    return;
                }
                if (args[1].isBlank()){
                    System.out.println("Please enter a commit message.");
                    return;
                }
                if(!repoManager.commit(args[1])){
                    System.out.println("No changes added to the commit.");
                    return;
                }
                break;
            case "rm":
                if(!validRepoAndOperandCount(args,repoManager,true,1)){
                    return;
                }
                if(!repoManager.rm(args[1])){
                    System.out.println("No reason to remove the file.");
                    return;
                }
                break;
            case "status":
                if(!validRepoAndOperandCount(args,repoManager,true,0)){
                    return;
                }
                repoManager.printStatus();
                break;
            case "log":
                if(!validRepoAndOperandCount(args,repoManager,true,0)){
                    return;
                }
                repoManager.log();
                break;
            case "global-log":
                if(!validRepoAndOperandCount(args,repoManager,true,0)){
                    return;
                }
                repoManager.logAll();
                break;
            case "branch":
                if(!validRepoAndOperandCount(args,repoManager,true,1)){
                    return;
                }
                if (!repoManager.createBranch(args[1])){
                    System.out.println("A branch with that name already exists.");
                    return;
                }
                break;
            case "rm-branch":
                if(!validRepoAndOperandCount(args,repoManager,true,1)){
                    return;
                }
                if (!repoManager.existsBranch(args[1])){
                    System.out.println("A branch with that name does not exist.");
                    return;
                }
                if (!repoManager.removeBranch(args[1])){
                    System.out.println("Cannot remove the current branch.");
                    return;
                }
                break;
            case "checkout":
                checkout(args,repoManager);
                break;
            case "reset":
                if(!validRepoAndOperandCount(args,repoManager,true,1)){
                    return;
                }
                reset(repoManager,args[1]);
                break;
            case "find":
                if (!validRepoAndOperandCount(args,repoManager,true,1)){
                    return;
                }
                if (!repoManager.find(args[1])){
                    System.out.println("Found no commit with that message.");
                }
                break;
            case "merge":
                if (!validRepoAndOperandCount(args,repoManager,true,1)){
                    return;
                }
                merge(repoManager,args[1]);
                break;
            case"add-remote":
                if (!validRepoAndOperandCount(args,repoManager,true,2)){
                    return;
                }
                if(!repoManager.addRemote(args[1],args[2])){
                    System.out.println("A remote with that name already exists.");
                }
                break;
            case"rm-remote":
                if (!validRepoAndOperandCount(args,repoManager,true,1)){
                    return;
                }
                if(!repoManager.removeRemote(args[1])){
                    System.out.println("A remote with that name does not exist.");
                }
                break;
            case"push":
                if(!validRepoAndOperandCount(args,repoManager,true,2)){
                    return;
                }
                push(repoManager,args[1],args[2]);
                break;
            case"fetch":
                if(!validRepoAndOperandCount(args,repoManager,true,2)){
                    return;
                }
                fetch(repoManager,args[1],args[2]);
                break;
            case"pull":
                if(!validRepoAndOperandCount(args,repoManager,true,2)){
                    return;
                }
                pull(repoManager,args[1],args[2]);
                break;
            default:
                System.out.println("No command with that name exists.");
                break;
        }
    }

    /**
     * Check the user input to see if the number of the operand satisfies the command
     * @param args cmd line args
     * @param requireRepo Does the command require ./gitlet?
     * @param operandCount The number of the operands that the command needs.
     * @return true if the input is correct.
     */
    private static boolean validRepoAndOperandCount
            (String[]args, RepositoryManager manager, boolean requireRepo, int operandCount){
        if (requireRepo&&!manager.existRepository()){
            System.out.println("Not in an initialized Gitlet directory.");
            return false;
        }
        if ((args.length-1)!=operandCount){
            System.out.println("Incorrect operands.");
            return false;
        }
        return true;
    }
    //handle all checkout command
    private static void checkout(String[]args, RepositoryManager manager){
        if (!manager.existRepository()){
            System.out.println("Not in an initialized Gitlet directory.");
        }
        switch (args.length-1){
            case 1:
                checkout(manager,args[1]);
                break;
            case 2:
                if (!manager.checkout(args[2])){
                    System.out.println("File does not exist in that commit.");
                }
                break;
            case 3:
                if (!args[2].equals("--")){
                    System.out.println("Incorrect operands.");
                    return;
                }
                checkout(manager,args[1],args[3]);
                break;
            default:
                System.out.println("Incorrect operands.");
                break;
        }
    }
    //handle checkout [commit id] -- [file name]
    private static void checkout(RepositoryManager manager,String commitId,String fileName){
        CheckoutFailure failure =manager.checkout(commitId,fileName);
        switch (failure){
            case COMMIT_NOT_EXIST :
                System.out.println("No commit with that id exists.");
                break;
            case FILE_NOT_EXIST:
                System.out.println("File does not exist in that commit.");
                break;
        }

    }
    //handle checkout [branch name]
    private static void checkout(RepositoryManager manager,String branchName){
        CheckoutFailure failure=manager.checkoutBranch(branchName);
        switch (failure){
            case BRANCH_NOT_EXIST :
                System.out.println("No such branch exists.");
                break;
            case CHECKOUT_CURRENT_BRANCH:
                System.out.println("No need to checkout the current branch.");
                break;
            case OVERWRITTEN:
                System.out.println("There is an untracked file in the way;" +
                        " delete it, or add and commit it first.");
                break;
        }
    }
    private static void reset(RepositoryManager manager,String commitId){
        CheckoutFailure failure=manager.reset(commitId);
        switch (failure){
            case COMMIT_NOT_EXIST:
                System.out.println("No commit with that id exists.");
                break;
            case OVERWRITTEN:
                System.out.println("There is an untracked file in the way; " +
                        "delete it, or add and commit it first.");
                break;
        }
    }
    private static void merge(RepositoryManager manager,String branchName){
        MergeStatus status=manager.merge(branchName);
        switch (status){
            case UNCOMMITTED_CHANGE_EXIST :
                System.out.println("You have uncommitted changes.");
                break;
            case BRANCH_NOT_EXIST:
                System.out.println("A branch with that name does not exist.");
                break;
            case MERGE_ITSELF:
                System.out.println("Cannot merge a branch with itself.");
                break;
            case OVERWRITTEN:
                System.out.println("There is an untracked file in the way; " +
                        "delete it, or add and commit it first.");
                break;
            case MERGE_ANCESTOR:
                System.out.println("Given branch is an ancestor " +
                        "of the current branch.");
                break;
            case FAST_FORWARD:
                System.out.println("Current branch fast-forwarded.");
                break;
            case CONFLICT:
                System.out.println("Encountered a merge conflict.");
                break;
        }
    }
    private static void push(RepositoryManager manager,String remoteName,String remoteBranchName){
        PushFailure failure=manager.push(remoteName,remoteBranchName);
        switch (failure){
            case NEED_PULL:
                System.out.println("Please pull down remote changes before pushing.");
                break;
            case REMOTE_NOT_FOUND:
                System.out.println("Remote directory not found.");
                break;
        }
    }
    private static void fetch(RepositoryManager manager,String remoteName,String remoteBranchName){
        FetchFailure failure=manager.fetch(remoteName,remoteBranchName);
        switch (failure){
            case BRANCH_NOT_EXIST:
                System.out.println("That remote does not have that branch.");
                break;
            case REMOTE_NOT_FOUND:
                System.out.println("Remote directory not found.");
                break;
        }
    }
    private static void pull(RepositoryManager manager,String remoteName,String remoteBranchName){
        //trash code ,but I don't care...
        FetchFailure failure=manager.fetch(remoteName,remoteBranchName);
        switch (failure){
            case BRANCH_NOT_EXIST:
                System.out.println("That remote does not have that branch.");
                return;
            case REMOTE_NOT_FOUND:
                System.out.println("Remote directory not found.");
                return;
        }
        MergeStatus status= manager.merge(remoteName,remoteBranchName);
        switch (status){
            case UNCOMMITTED_CHANGE_EXIST :
                System.out.println("You have uncommitted changes.");
                break;
            case BRANCH_NOT_EXIST:
                System.out.println("A branch with that name does not exist.");
                break;
            case MERGE_ITSELF:
                System.out.println("Cannot merge a branch with itself.");
                break;
            case OVERWRITTEN:
                System.out.println("There is an untracked file in the way; " +
                        "delete it, or add and commit it first.");
                break;
            case MERGE_ANCESTOR:
                System.out.println("Given branch is an ancestor " +
                        "of the current branch.");
                break;
            case FAST_FORWARD:
                System.out.println("Current branch fast-forwarded.");
                break;
            case CONFLICT:
                System.out.println("Encountered a merge conflict.");
                break;
        }
    }
}
