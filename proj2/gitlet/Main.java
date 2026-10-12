package gitlet;


import java.io.File;

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
        RepositoryManager repoManager=new RepositoryManager
                (Utils.join("E:","csLearn","dataStructure","gitletTest").toPath());
        if (!repoManager.existRepository()){
            repoManager.Init();
            //System.out.println("A Gitlet version-control system " +
              //      "already exists in the current directory.");
        }
        //repoManager.printStatus();
        //repoManager.add("f1.txt");
        //repoManager.add("f2.txt");
        //repoManager.add("f3.txt");
        //repoManager.add("f4.txt");
        //repoManager.rm("f3.txt");
        //repoManager.rm("f4.txt");
        //repoManager.printStatus();
        //repoManager.commit("Third commit");
        //repoManager.printStatus();
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
        repoManager.reset("c7026adeabac40");
        //repoManager.log();
        if (true){//to make compilation succeed
            return;
        }

        /*Real program*/
        if (args.length==0){
            System.out.println("Please enter a command.");
            return;
        }

        String command = args[0];//make it easy,just hard-code it
        switch(command) {
            case "init":
                if(!validArgs(args,repoManager,false,0)){
                    return;
                }
                if (!repoManager.Init()){
                    System.out.println("A Gitlet version-control system " +
                            "already exists in the current directory.");
                }
                break;
            case "add":
                if(!validArgs(args,repoManager,true,1)){
                    return;
                }
                if(!repoManager.add(args[1])){
                    System.out.println("File does not exist.");
                    return;
                }
                break;
            case "commit":
                if(!validArgs(args,repoManager,true,1)){
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
                if(!validArgs(args,repoManager,true,1)){
                    return;
                }
                if(!repoManager.rm(args[1])){
                    System.out.println("No reason to remove the file.");
                    return;
                }
                break;
            case "status":
                if(!validArgs(args,repoManager,true,0)){
                    return;
                }
                repoManager.printStatus();
                break;
            case "log":
                if(!validArgs(args,repoManager,true,0)){
                    return;
                }
                repoManager.log();
                break;
            case "global-log":
                if(!validArgs(args,repoManager,true,0)){
                    return;
                }
                repoManager.logAll();
                break;
            case "branch":
                if(!validArgs(args,repoManager,true,1)){
                    return;
                }
                if (!repoManager.createBranch(args[1])){
                    System.out.println("A branch with that name already exists.");
                    return;
                }
                break;
            case "rm-branch":
                if(!validArgs(args,repoManager,true,1)){
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
                if(!validArgs(args,repoManager,true,1)){
                    return;
                }
                reset(repoManager,args[1]);
                break;
            case "find":
                if (!validArgs(args,repoManager,true,1)){
                    return;
                }
                if (!repoManager.find(args[1])){
                    System.out.println("Found no commit with that message.");
                }
                break;
            default:
                System.out.println("No command with that name exists.");
                break;
        }
    }

    /**
     * Check the user input
     * @param args cmd line args
     * @param requireRepo Does the command require ./gitlet?
     * @param operandCount The number of the operands that the command needs.
     * @return true if the input is correct.
     */
    private static boolean validArgs
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
}
