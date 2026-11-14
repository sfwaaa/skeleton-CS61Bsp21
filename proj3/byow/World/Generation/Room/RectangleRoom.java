package byow.World.Generation.Room;


import byow.Core.RandomUtils;
import byow.TileEngine.*;
import byow.World.Generation.RoomBoundary;
import byow.World.Vector2Int;
import byow.World.World;

import java.util.Random;

/**
 * Only pass a single floor when initializing this room
 */
public class RectangleRoom extends Room {
    private static final Vector2Int MIN_ROOM_SIZE =new Vector2Int(3,3);
    private TETile floor;
    private boolean canBuild =false;
    private Vector2Int center=Vector2Int.ZERO;
    private RoomBoundary shrunkBoundary;
    @Override
    public void initialize(World world, RoomBoundary boundary, Random random,TETile[] floors) {
        super.initialize(world, boundary,random,floors);
        if (world==null||boundary==null||
                boundary.width<MIN_ROOM_SIZE.x||boundary.height<MIN_ROOM_SIZE.y){
            canBuild =false;
        }
        else {
            canBuild =true;
        }
        this.floor=floors[0];
    }

    @Override
    public boolean buildRoomFloor() {
        if(!canBuild){
            return false;
        }
        shrunkBoundary=randomShrink(this.boundary);
        center=Vector2Int.plus(shrunkBoundary.bottomLeft,
                new Vector2Int(shrunkBoundary.width/2,shrunkBoundary.height/2));
//        putBoundary(shrunkBoundary, Tileset.WALL);//previous implementation,can be used for debugging
        putFloor(shrunkBoundary,this.floor);
        return true;
    }

    @Override
    public Vector2Int minRequiredSize() {
        return MIN_ROOM_SIZE;
    }

    @Override
    public void trim(TETile wall) {
        return;//basic shape need not be trimmed
    }

    @Override
    public Vector2Int centerPosition() {
        return center;
    }

    @Override
    public Room getEmptyRoom() {
        return new RectangleRoom();
    }

    @Override
    public Vector2Int randomValidPosition() {
        if (this.shrunkBoundary==null){
            return null;
        }
        int x=RandomUtils.uniform(random,shrunkBoundary.bottomLeft.x+1,
                shrunkBoundary.bottomLeft.x+shrunkBoundary.width-1);
        int y=RandomUtils.uniform(random,shrunkBoundary.bottomLeft.y+1,
                shrunkBoundary.bottomLeft.y+shrunkBoundary.height-1);
        return new Vector2Int(x,y);
    }

    //shrink the given boundary(inclusive),and return it
    private RoomBoundary randomShrink(RoomBoundary boundary){
        int shrinkableWidth=boundary.width-MIN_ROOM_SIZE.x;
        int shrinkWidth= RandomUtils.uniform(random,shrinkableWidth+1);
        int leftShrink =0;
        int rightShrink=0;
        if (shrinkWidth>0){
            leftShrink =RandomUtils.uniform(random,0,shrinkWidth);
            rightShrink=RandomUtils.uniform(random,0,shrinkWidth-leftShrink);
        }

        //just repeat some code, I don't care :P :) :>
        int shrinkableHeight=boundary.height-MIN_ROOM_SIZE.y;
        int shrinkHeight= RandomUtils.uniform(random,shrinkableHeight+1);
        int upShrink =0;
        int downShrink=0;
        if(shrinkHeight>0){
            upShrink =RandomUtils.uniform(random,0,shrinkHeight);
            downShrink=RandomUtils.uniform(random,0,shrinkHeight-upShrink);
        }
        return boundary.shrink(leftShrink,rightShrink,upShrink,downShrink);
    }
    private void putFloor(RoomBoundary boundary,TETile floor){
        //put columns
        Vector2Int bottomPos=Vector2Int.
                plus(Vector2Int.UP_RIGHT,boundary.bottomLeft);
        int targetX=boundary.width+boundary.bottomLeft.x-1;
        for(;bottomPos.x<targetX;bottomPos.x++){
            world.putColumn(bottomPos,1,boundary.height-2,floor);
        }
    }
    private void putBoundary(RoomBoundary boundary, TETile tile){
        this.world.putRow(boundary.bottomLeft,1,
                boundary.width,tile);

        Vector2Int topLeft=new Vector2Int
                (boundary.bottomLeft.x,boundary.bottomLeft.y+boundary.height-1);
        this.world.putRow(topLeft,1,boundary.width,tile);

        this.world.putColumn
                (boundary.bottomLeft,1,boundary.height,tile);

        Vector2Int bottomRight=new Vector2Int
                (boundary.bottomLeft.x+boundary.width-1,boundary.bottomLeft.y);
        this.world.putColumn
                (bottomRight,1,boundary.height,tile);
    }
}
