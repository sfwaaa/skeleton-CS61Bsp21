package byow.World.Generation.Room;

import byow.Core.RandomUtils;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import byow.World.Generation.RoomBoundary;
import byow.World.Vector2Int;
import byow.World.World;

import java.util.Random;

/**
 * Only pass a single floor when initializing this room
 */
public class CircleRoom extends Room{
    private static final Vector2Int MIN_ROOM_SIZE =new Vector2Int(4,4);//should always be n*n
    private TETile floor;
    private RoomBoundary trimmedBoundary=null;
    @Override
    public void initialize(World world, RoomBoundary boundary, Random random, TETile[] floors) {
        super.initialize(world, boundary, random, floors);
        this.floor=floors[0];
    }

    @Override
    public boolean buildRoomFloor() {
        trimmedBoundary=trimBoundary(this.boundary);
        if (trimmedBoundary==null){
            return false;
        }
//        center=Vector2Int.plus(trimmedBoundary.bottomLeft,
//                new Vector2Int(trimmedBoundary.width/2,trimmedBoundary.height/2));
//        System.out.println(trimmedBoundary.width+"x"+trimmedBoundary.height+"rect at "+trimmedBoundary.bottomLeft.toString());
//        putCircleBoundary(trimmedBoundary, Tileset.WALL);//previous implementation,can be used for debugging

        //upper half
        Vector2Int middleLeft=Vector2Int.plus
                (trimmedBoundary.bottomLeft,new Vector2Int(1,trimmedBoundary.height/2));
        putHalfCircleFloor(middleLeft,Vector2Int.UP,trimmedBoundary.width,this.floor);

        //lower half
        putHalfCircleFloor(new Vector2Int(middleLeft.x,middleLeft.y-1),
                Vector2Int.DOWN,trimmedBoundary.width,this.floor);

        return true;
    }

    @Override
    public Vector2Int minRequiredSize() {
        return MIN_ROOM_SIZE;
    }

    @Override
    public void trim(TETile wall) {
        if (this.trimmedBoundary==null){
            return;
        }
        trimArcs(wall);
    }

    @Override
    public Vector2Int centerPosition() {
        if (trimmedBoundary==null){
            return Vector2Int.ZERO;
        }
        return Vector2Int.plus(trimmedBoundary.bottomLeft,
                new Vector2Int((trimmedBoundary.width/2),(trimmedBoundary.height/2)) );
    }

    @Override
    public Room getEmptyRoom() {
        return new CircleRoom();
    }

    @Override
    public Vector2Int randomValidPosition() {
        //simply choose a position around the center position
        Vector2Int centerPos=this.centerPosition();
        if (Vector2Int.equal(centerPos,Vector2Int.ZERO)){
            return null;
        }
        int x=RandomUtils.uniform(random,-1,2);
        int y=RandomUtils.uniform(random,-1,2);
        Vector2Int offset=new Vector2Int(x,y);
        Vector2Int position=Vector2Int.plus(centerPos,offset);
        if (world.getTile(position).character()==floor.character()){
            return position;
        }

        //simply check 8 directions...
        TETile tile=null;
        centerPos=position;

        position=Vector2Int.plus(centerPos,Vector2Int.LEFT);
        tile=world.getTile(position);
        if (tile!=null&&tile.character()==floor.character()){
            return position;
        }
        position=Vector2Int.plus(centerPos,Vector2Int.RIGHT);
        tile=world.getTile(position);
        if (tile!=null&&tile.character()==floor.character()){
            return position;
        }
        position=Vector2Int.plus(centerPos,Vector2Int.UP);
        tile=world.getTile(position);
        if (tile!=null&&tile.character()==floor.character()){
            return position;
        }
        position=Vector2Int.plus(centerPos,Vector2Int.DOWN);
        tile=world.getTile(position);
        if (tile!=null&&tile.character()==floor.character()){
            return position;
        }
        position=Vector2Int.plus(centerPos,Vector2Int.UP_LEFT);
        tile=world.getTile(position);
        if (tile!=null&&tile.character()==floor.character()){
            return position;
        }
        position=Vector2Int.plus(centerPos,Vector2Int.UP_RIGHT);
        tile=world.getTile(position);
        if (tile!=null&&tile.character()==floor.character()){
            return position;
        }
        position=Vector2Int.plus(centerPos,Vector2Int.DOWN_LEFT);
        tile=world.getTile(position);
        if (tile!=null&&tile.character()==floor.character()){
            return position;
        }
        position=Vector2Int.plus(centerPos,Vector2Int.DOWN_RIGHT);
        tile=world.getTile(position);
        if (tile!=null&&tile.character()==floor.character()){
            return position;
        }
        return this.centerPosition();
    }

    /**
     * trim boundary so that the size is always n*n where "n" is even and >=MIN_ROOM_SIZE.x;
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
    private void putCircleBoundary(RoomBoundary boundary, TETile wall){
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

    /**
     * @param direction either up(0,1) or down(0,-1)
     */
    private void putHalfCircleFloor(Vector2Int startPos,Vector2Int direction,int diameter,TETile floor){
        Vector2Int moveOffset=Vector2Int.plus(direction,Vector2Int.RIGHT);
        Vector2Int currentPos=startPos;
        for (int putCount = diameter-2; putCount >0; putCount-=2) {
            world.putRow(currentPos,1,putCount,floor);

            currentPos=Vector2Int.plus(currentPos,moveOffset);
        }

    }

//    private void trim44(){ //deprecated
//        Vector2Int bottomLeft=trimmedBoundary.bottomLeft;
//        Vector2Int upLeft=new Vector2Int(bottomLeft.x,bottomLeft.y+trimmedBoundary.height-1);
//        Vector2Int upRight=new Vector2Int(bottomLeft.x+trimmedBoundary.width-1,bottomLeft.y+trimmedBoundary.height-1);
//        Vector2Int bottomRight=new Vector2Int(bottomLeft.x+trimmedBoundary.width-1,bottomLeft.y);
//
//        //four corners
//        if (!world.existsTile(Vector2Int.plus(Vector2Int.DOWN_LEFT,bottomLeft))){
//            world.clearAt(upLeft);
//        }
//        if (!world.existsTile(Vector2Int.plus(Vector2Int.UP_LEFT,upLeft))){
//            world.clearAt(upLeft);
//        }
//        if (!world.existsTile(Vector2Int.plus(Vector2Int.UP_RIGHT,upRight))){
//            world.clearAt(upRight);
//        }
//        if (!world.existsTile(Vector2Int.plus(Vector2Int.DOWN_RIGHT,bottomRight))){
//            world.clearAt(bottomRight);
//        }
//
//    }

    private void trimArcs(TETile wall){
        int arcCount=(trimmedBoundary.height/2)-1;

        Vector2Int middleLeft=Vector2Int.plus
                (trimmedBoundary.bottomLeft,new Vector2Int(0,trimmedBoundary.height/2));
        Vector2Int middleRight=new Vector2Int(middleLeft.x+trimmedBoundary.width-1,middleLeft.y);
        //repeat a lot of codes I DON'T CARE !! (I'm wailing~ :<)
        //bottomLeft part
        Vector2Int currentPos=new Vector2Int(middleLeft.x,middleLeft.y-1);
//        currentPos.y--;
        Vector2Int moveDirection=Vector2Int.DOWN_RIGHT;
        Vector2Int checkDirection=Vector2Int.DOWN_LEFT;
        TETile nextTile=null;
        for (int i = 0; i < arcCount; i++) {
            if (!world.existsTile(Vector2Int.plus(currentPos,checkDirection)) ){
                nextTile=world.getTile(Vector2Int.plus(currentPos,moveDirection));
                if (nextTile!=null&&nextTile.character()==wall.character()){
                    world.clearAt(Vector2Int.plus(currentPos,Vector2Int.DOWN));
                }
            }

            currentPos=Vector2Int.plus(currentPos,moveDirection);
        }
        //bottomRight
        currentPos=new Vector2Int(middleRight.x,middleRight.y-1);
//        currentPos.y--;
        moveDirection=Vector2Int.DOWN_LEFT;
        checkDirection=Vector2Int.DOWN_RIGHT;
        for (int i = 0; i < arcCount; i++) {
            if (!world.existsTile(Vector2Int.plus(currentPos,checkDirection))){
                nextTile=world.getTile(Vector2Int.plus(currentPos,moveDirection));
                if (nextTile!=null&&nextTile.character()==wall.character()){
                    world.clearAt(Vector2Int.plus(currentPos,Vector2Int.DOWN));
                }
            }

            currentPos=Vector2Int.plus(currentPos,moveDirection);
        }
        //upLeft
        currentPos=new Vector2Int(middleLeft.x,middleLeft.y);
        moveDirection=Vector2Int.UP_RIGHT;
        checkDirection=Vector2Int.UP_LEFT;
        for (int i = 0; i < arcCount; i++) {
            if (!world.existsTile(Vector2Int.plus(currentPos,checkDirection)) ){
                nextTile=world.getTile(Vector2Int.plus(currentPos,moveDirection));
                if (nextTile!=null&&nextTile.character()==wall.character()){
                    world.clearAt(Vector2Int.plus(currentPos,Vector2Int.UP));
                }
            }

            currentPos=Vector2Int.plus(currentPos,moveDirection);
        }
        //upRight
        currentPos=new Vector2Int(middleRight.x,middleRight.y);
        moveDirection=Vector2Int.UP_LEFT;
        checkDirection=Vector2Int.UP_RIGHT;
        for (int i = 0; i < arcCount; i++) {
            if (!world.existsTile(Vector2Int.plus(currentPos,checkDirection))){
                nextTile=world.getTile(Vector2Int.plus(currentPos,moveDirection));
                if (nextTile!=null&&nextTile.character()==wall.character()){
                    world.clearAt(Vector2Int.plus(currentPos,Vector2Int.UP));
                }
            }

            currentPos=Vector2Int.plus(currentPos,moveDirection);
        }
    }
}
