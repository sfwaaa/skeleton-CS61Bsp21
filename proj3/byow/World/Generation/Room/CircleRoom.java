package byow.World.Generation.Room;

import byow.Core.RandomUtils;
import byow.TileEngine.TETile;
import byow.World.Generation.RoomBoundary;
import byow.World.Vector2Int;

public class CircleRoom extends Room{
    private static final Vector2Int MIN_ROOM_SIZE =new Vector2Int(4,4);


    @Override
    public boolean buildRoomTiles(TETile wall) {
        RoomBoundary trimmedBoundary=trimBoundary(this.boundary);
        if (trimmedBoundary==null){
            return false;
        }
        putCircle(trimmedBoundary,wall);
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
    public Room clone() {
        return new CircleRoom();
    }

    /**
     * trim boundary so that the size is always even*even
     * @return null if invalid boundary,otherwise trimmed boundary
     */
    private RoomBoundary trimBoundary(RoomBoundary boundary){
        if (boundary.width<MIN_ROOM_SIZE.x||boundary.height<MIN_ROOM_SIZE.y){
            return null;
        }
        RoomBoundary trimmed=new RoomBoundary
                (boundary.bottomLeft,boundary.width,boundary.height);
        if (boundary.height%2==1){
            trimmed.height--;
            trimmed.bottomLeft.y+= RandomUtils.uniform(this.random,2);
        }
        if (boundary.width%2==1){
            trimmed.width--;
            trimmed.bottomLeft.x+= RandomUtils.uniform(this.random,2);
        }
        if (trimmed.width>trimmed.height){
            trimmed.bottomLeft.x+=RandomUtils.uniform
                    (this.random,trimmed.width-trimmed.height);
            trimmed.width=trimmed.height;
        }
        else if (trimmed.height>trimmed.width){
            trimmed.bottomLeft.y+=RandomUtils.uniform
                    (this.random,trimmed.height-trimmed.width);
            trimmed.height=trimmed.width;
        }
        return trimmed;
    }
    private void putCircle(RoomBoundary boundary,TETile wall){
        Vector2Int middleLeft=Vector2Int.plus
                (boundary.bottomLeft,new Vector2Int(0,boundary.height/2));
        world.putColumn(middleLeft,-1,2,wall);

        Vector2Int middleRight=Vector2Int.plus
                (middleLeft,new Vector2Int(boundary.width-1,0));
        world.putColumn(middleRight,-1,2,wall);

        Vector2Int middleDown=Vector2Int.plus
                (boundary.bottomLeft,new Vector2Int(boundary.width/2,0));
        world.putRow(middleDown,-1,2,wall);

        Vector2Int middleUp=Vector2Int.plus
                (middleDown,new Vector2Int(0,boundary.height-1));
        world.putRow(middleUp,-1,2,wall);

//        System.out.println(TETile.toString(world.getFrame()));
        //draw arcs
        int arcCount=middleUp.y-middleLeft.y-1;
        Vector2Int upArcLeftPos=Vector2Int.plus(middleLeft,Vector2Int.UP_RIGHT);
        Vector2Int downArcLeftPos=Vector2Int.plus(middleLeft,Vector2Int.DOWN_RIGHT);
        downArcLeftPos.y--;
        int offset=boundary.width-3;
        for (int i = 0; i < arcCount; i++) {
            world.put(upArcLeftPos,wall);
            world.put(downArcLeftPos,wall);
            world.put(new Vector2Int(upArcLeftPos.x+offset,upArcLeftPos.y),wall);
            world.put(new Vector2Int(downArcLeftPos.x+offset,downArcLeftPos.y),wall);

            upArcLeftPos=Vector2Int.plus(upArcLeftPos,Vector2Int.UP_RIGHT);
            downArcLeftPos=Vector2Int.plus(downArcLeftPos,Vector2Int.DOWN_RIGHT);
            offset-=2;
        }
    }
}
