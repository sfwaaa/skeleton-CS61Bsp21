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


        if (args.length==0){
            //TODO
            return;
        }
        // TODO: what if args is empty?
        String firstArg = args[0];
        switch(firstArg) {
            case "init":

                break;
            case "add":

                break;
            default:

                break;
        }
    }
}
