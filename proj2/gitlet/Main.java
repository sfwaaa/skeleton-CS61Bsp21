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
                (Utils.join("E:","csLearn","dataStructure","gitletTest"));
        if (!repoManager.existRepository()){
            repoManager.Init();
            //System.out.println("A Gitlet version-control system " +
              //      "already exists in the current directory.");
        }

        //repoManager.add("f1.txt");
        //repoManager.add("f2.txt");
        //repoManager.printStatus();
        //repoManager.commit("first commit");

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

        if (true){
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
                //
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
            (String[]args,
             RepositoryManager manager,
             boolean requireRepo,
             int operandCount){
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


}
