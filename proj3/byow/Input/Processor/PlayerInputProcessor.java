package byow.Input.Processor;

import byow.World.Vector2Int;
import edu.princeton.cs.introcs.StdDraw;

import java.lang.Math;

//process all possible input when player exploring the world,
//different kind of input should be checked at single "frame"
public class PlayerInputProcessor {
    private static final char COMMAND_CHAR=':';
    private static final char COMMAND_QUIT='q';
    private static final char COMMAND_UP='w';
    private static final char COMMAND_LEFT='a';
    private static final char COMMAND_DOWN='s';
    private static final char COMMAND_RIGHT='d';
    //used for :Q
    private Character leadingChar=null;
    //return the rounded position of the mouse,
    public Vector2Int getMousePosition(){
        //using the same coordinate system as set in the TERenderer
        int x= (int) Math.floor(StdDraw.mouseX());
        int y= (int) Math.floor(StdDraw.mouseY());
        return new Vector2Int(x,y);
    }

    /**
     * @return one of the unit move direction:
     * left(-1,0) right (1,0) up(0,1) down(0,-1),
     * (0,0) if no move input
     */
    public Vector2Int getMove(Character in){
        if (in==null){
            return Vector2Int.ZERO;
        }
        if (equalCommand(in,COMMAND_UP)){
            return Vector2Int.UP;
        }
        if (equalCommand(in,COMMAND_LEFT)){
            return Vector2Int.LEFT;
        }
        if (equalCommand(in,COMMAND_DOWN)){
            return Vector2Int.DOWN;
        }
        if (equalCommand(in,COMMAND_RIGHT)){
            return Vector2Int.RIGHT;
        }
        return Vector2Int.ZERO;
    }

    public boolean getQuit(Character in){
        if (in==null){
            return false;
        }
        if (in.equals(COMMAND_CHAR)){
            this.leadingChar=in;
            return false;
        }
        if (equalCommand(in,COMMAND_QUIT)&&
                leadingChar!=null&&leadingChar.equals(COMMAND_CHAR)){
            leadingChar=null;
           return true;
        }
        leadingChar=null;
        return false;
    }
    //insensitive
    private boolean equalCommand(Character in,char command){
        return Character.toLowerCase(in)==command;
    }
}
