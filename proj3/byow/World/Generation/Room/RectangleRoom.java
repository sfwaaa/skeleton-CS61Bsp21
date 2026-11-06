package byow.World.Generation.Room;


import byow.Core.RandomUtils;
import byow.TileEngine.TETile;
import byow.World.Generation.RoomBoundary;
import byow.World.Vector2Int;
import byow.World.World;

import java.util.Random;

public class RectangleRoom extends Room {
    private static final Vector2Int MIN_ROOM_SIZE =new Vector2Int(3,3);

    private boolean canBulid=false;
    @Override
    public void initializeBoundary(World world, RoomBoundary boundary, Random random) {
        super.initializeBoundary(world, boundary,random);
        if (world==null||boundary==null||
                boundary.width<MIN_ROOM_SIZE.x||boundary.height<MIN_ROOM_SIZE.y){
            canBulid=false;
        }
        else {
            canBulid=true;
        }
    }

    @Override
    public boolean buildRoomTiles(TETile wall) {
        if(!canBulid){
            return false;
        }
        RoomBoundary shrunkBoundary=randomShrink(this.boundary);
        fillBoundary(shrunkBoundary,wall);
        return true;
    }

    @Override
    public Vector2Int buildEntrance(Vector2Int direction) {
        return null;
    }

    @Override
    public Vector2Int minRequiredSize() {
        return MIN_ROOM_SIZE;
    }

    @Override
    public Room getEmptyRoom() {
        return new RectangleRoom();
    }

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
    private void fillBoundary(RoomBoundary boundary,TETile tile){
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
