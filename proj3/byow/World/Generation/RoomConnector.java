package byow.World.Generation;

import byow.TileEngine.TETile;
import byow.World.Generation.Room.*;
import byow.World.Vector2Int;
import byow.World.World;

import java.util.Random;

/**
 * Responsible for connecting the rooms
 */
public class RoomConnector {

    private TETile hallwayFloor;
    public RoomConnector(TETile hallwayFloor){
        this.hallwayFloor=hallwayFloor;
    }

    /**
     * put a hallway between two rooms
     */
    public void connect(World world, Random random, Room src, Room dst, int hallwayWidth){
        if (hallwayWidth==1){
            putSingleHallway(world,random,src.centerPosition(),dst.centerPosition());
        }
        else{
            putWiderHallway(world,random,src.centerPosition(),dst.centerPosition(),hallwayWidth);
        }
    }

    /**
     * particular for hallways with width one
     */
    private void putSingleHallway(World world, Random random, Vector2Int src, Vector2Int dst){
        Vector2Int[] directions=getMoveDirection(src,dst);
        Vector2Int horizontalMoveDirection=directions[0];
        Vector2Int verticalMoveDirection=directions[1];
        boolean needPutX=!Vector2Int.equal(horizontalMoveDirection,Vector2Int.ZERO);
        boolean needPutY=!Vector2Int.equal(verticalMoveDirection,Vector2Int.ZERO);

        if (needPutX&&needPutY){//L shape hallway
            //add some random effect if you want
            putRow(world,src,dst,horizontalMoveDirection);
            putColumn(world,new Vector2Int(dst.x,src.y),dst,verticalMoveDirection);
        }
        else if (needPutX){//only a line hallway
            putRow(world,src,dst,horizontalMoveDirection);
        }
        else if(needPutY){//only a line hallway
            putColumn(world,src,dst,horizontalMoveDirection);
        }


    }
    /**
     * particular for hallways with width >= one
     */
    private void putWiderHallway(World world, Random random, Vector2Int src, Vector2Int dst,int width){
//        System.out.println("from "+src.toString()+" to "+dst.toString());
        Vector2Int[] directions=getMoveDirection(src,dst);
        Vector2Int horizontalMoveDirection=directions[0];
        Vector2Int verticalMoveDirection=directions[1];
        boolean needPutX=!Vector2Int.equal(horizontalMoveDirection,Vector2Int.ZERO);
        boolean needPutY=!Vector2Int.equal(verticalMoveDirection,Vector2Int.ZERO);

        //may repeat some code, I DON'T CARE :(
        Vector2Int from=new Vector2Int(src.x,src.y);
        Vector2Int to=new Vector2Int(dst.x,dst.y);
        if (needPutX&&needPutY){//L shape hallway
            //add some random effect if you want
            for (int i = 0; i < width; i++) {
                putRow(world,from,to,horizontalMoveDirection);
                from=Vector2Int.plus(from,Vector2Int.UP);
                to=Vector2Int.plus(to,Vector2Int.UP);
            }
            from=new Vector2Int(dst.x,src.y);
            to=new Vector2Int(dst.x,dst.y);
            for (int i = 0; i < width; i++) {
                putColumn(world,from,to,verticalMoveDirection);
                from=Vector2Int.plus(from,Vector2Int.LEFT);
                to=Vector2Int.plus(to,Vector2Int.LEFT);
            }
        }
        else if (needPutX){//only straight hallway
            for (int i = 0; i < width; i++) {
                putRow(world,from,to,horizontalMoveDirection);
                from=Vector2Int.plus(from,Vector2Int.UP);
                to=Vector2Int.plus(to,Vector2Int.UP);
            }
        }
        else if(needPutY){//only straight hallway
            for (int i = 0; i < width; i++) {
                putColumn(world,from,to,verticalMoveDirection);
                from=Vector2Int.plus(from,Vector2Int.LEFT);
                to=Vector2Int.plus(to,Vector2Int.LEFT);
            }

        }

    }

    /**
     * @return new Vector2Int[]{horizontalDir,verticalDir},(0,0)if x/y of the src is the same as dst
     */
    private Vector2Int[] getMoveDirection(Vector2Int src, Vector2Int dst){
        Vector2Int horizontalMoveDirection=new Vector2Int(0,0);
        if (src.x<dst.x){
            horizontalMoveDirection.x=1;
        }
        else if(src.x>dst.x){
            horizontalMoveDirection.x=-1;
        }

        Vector2Int verticalMoveDirection=new Vector2Int(0,0);
        if (src.y<dst.y){
            verticalMoveDirection.y=1;
        }
        else if(src.y>dst.y){
            verticalMoveDirection.y=-1;
        }
        return new Vector2Int[]{horizontalMoveDirection,verticalMoveDirection};
    }

    /**
     * @param direction either (1,0) or (-1,0)
     */
    private void putRow(World world,Vector2Int src,Vector2Int dst,Vector2Int direction){
        Vector2Int current=new Vector2Int(src.x,src.y);
        while (Math.abs(current.x-dst.x)!=0){
            if (!world.existsTile(current)){
                world.put(current,hallwayFloor);
            }
            current=Vector2Int.plus(current,direction);
        }
    }
    /**
     * @param direction either (0,1) or (0,1)
     */
    private void putColumn(World world,Vector2Int src,Vector2Int dst,Vector2Int direction){
        Vector2Int current=new Vector2Int(src.x,src.y);
        while (Math.abs(current.y-dst.y)!=0){
            if (!world.existsTile(current)){
                world.put(current,hallwayFloor);
            }
            current=Vector2Int.plus(current,direction);
        }
    }
}
