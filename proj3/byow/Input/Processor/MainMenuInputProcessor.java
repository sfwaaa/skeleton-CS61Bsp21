package byow.Input.Processor;

//process all possible main menu input,
//different kind of input should be checked at single "frame"
public class MainMenuInputProcessor {

    private static final char COMMAND_NEW_WORLD='n';
    private static final char COMMAND_END_NEW_WORLD='s';
    private static final char COMMAND_LOAD_WORLD='l';
    private static final char COMMAND_QUIT='q';

    /**
     * @return whether player requests generating a new world
     */
    public boolean requestNewWorld(Character in){
       return in!=null&&equalCommand(in,COMMAND_NEW_WORLD);
    }

    /**
     * @return whether player finishes entering the seed
     */
    public boolean doneEnteringSeed(Character in){
        return in!=null&&equalCommand(in,COMMAND_END_NEW_WORLD);
    }

    public boolean requestLoadWorld(Character in){
        return in!=null&&equalCommand(in,COMMAND_LOAD_WORLD);
    }
    public boolean requestQuit(Character in){
        return in!=null&&equalCommand(in,COMMAND_QUIT);
    }
    //character insensitive compare
    private boolean equalCommand(Character in,char command){
        return Character.toLowerCase(in)==command;
    }
}
