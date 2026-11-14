package byow.Entity;

import byow.TileEngine.*;
import byow.World.Vector2Int;
import byow.World.World;

/**
 * representing a movable object in the world
 */
public final class MovableObject {
    private final World world;
    private final TETile[] obstacles;

    private Vector2Int currentPosition;
    private TETile underFootTile;
    private final TETile avatar;
    public MovableObject(World world, TETile[] obstacles,TETile avatar){
        this.world=world;
        this.obstacles=obstacles;
        this.avatar=avatar;
    }

    /**
     * add the object into the world,call this before any movement!
     * @param position the initial position,should be a valid position!
     */
    public void setInitialPosition(Vector2Int position){
        underFootTile=world.put(position,avatar);
        this.currentPosition=position;
    }

    /**
     * move 1 step in the four direction
     * @param direction one of the left(-1,0) right (1,0) up(0,1) down(0,-1)
     * @return true if it actually moved
     */
    public boolean move(Vector2Int direction) {
        if (direction==null||Vector2Int.equal(Vector2Int.ZERO,direction)){
            return false;
        }
        Vector2Int nextPosition = Vector2Int.plus
                (this.currentPosition, direction);
        if (isObstacle(world.getTile(nextPosition))) {
            return false;
        }
        world.put(currentPosition,underFootTile);
        underFootTile=world.put(nextPosition,avatar);
        this.currentPosition=nextPosition;
        return true;
    }
    public Vector2Int getCurrentPosition(){
        return currentPosition;
    }
    private boolean isObstacle(TETile tile){
        //since the class TETile is not defined by me,
        //I can only use this naive loop to check tiles
        for (int i = 0; i < obstacles.length; i++) {
            if (tile.character()==obstacles[i].character()){
                return true;
            }
        }
        return false;
    }

}
