package byow.lab12;

import byow.Core.RandomUtils;
import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import byow.World.Vector2Int;

import java.util.Random;

/**
 * Draws a world consisting of hexagonal regions.
 * just to get familiar with drawing tiles.All is done within this class
 */
public class HexWorld {

    //Resolution(Canvas Size) : 1280x720 -> 80x45 tiles, leaves some space for UI and empty space
    private static final int MAX_WIDTH=1280;
    private static final int MAX_HEIGHT=720;

    private static final int TILE_SIZE_IN_PIXEL =16;

    //leaves some space for UI and empty space,here all assigned arbitrarily
    private static final int DRAWING_TILE_OFFSET_LEFT=10;//
    private static final int DRAWING_TILE_OFFSET_RIGHT=10;
    private static final int DRAWING_TILE_OFFSET_UP=2;
    private static final int DRAWING_TILE_OFFSET_DOWN=2;

    //max tile count in width;
    //Excluding the offset so that we don't need to care about them
    private static final int MAX_TILE_WIDTH =
            (MAX_WIDTH/ TILE_SIZE_IN_PIXEL)-DRAWING_TILE_OFFSET_LEFT-DRAWING_TILE_OFFSET_RIGHT;
    //max tile count in height
    private static final int MAX_TILE_HEIGHT =
            (MAX_HEIGHT/ TILE_SIZE_IN_PIXEL)-DRAWING_TILE_OFFSET_UP-DRAWING_TILE_OFFSET_DOWN;

    public static void main(String[] args) {
        TERenderer ter = new TERenderer();
        //add offset to leave some space for UI and empty space,
        ter.initialize(MAX_TILE_WIDTH+DRAWING_TILE_OFFSET_LEFT+DRAWING_TILE_OFFSET_RIGHT,
                MAX_TILE_HEIGHT+DRAWING_TILE_OFFSET_UP+DRAWING_TILE_OFFSET_DOWN,
                DRAWING_TILE_OFFSET_LEFT,DRAWING_TILE_OFFSET_DOWN);


        TETile[][] world =emptyWorld(MAX_TILE_WIDTH, MAX_TILE_HEIGHT);
        //a 60x35 world with [0][0] at the origin,
        //[0][MAX_TILE_HEIGHT] on the uppermost on y-axis ,[MAX_TILE_WIDTH][0] on the rightmost on x-axis
        //which is consistent with the position represented by a Vector2Int object

        //put some tiles
        //putColumn(world,new Vector2Int(0,30),1,10,Tileset.TREE);
        //putHexagon(world,new Vector2Int(0,0),5,Tileset.TREE);
        TETile[] tiles=new TETile[]{Tileset.FLOWER,Tileset.TREE,Tileset.SAND};
        fillHexagon(world,4,new Vector2Int(10,0),3,5,tiles);
        ter.renderFrame(world);
    }

    private static TETile[][] emptyWorld(int width,int height){
        //note : here the layout of the array in the memory is different from
        //what we think logically...
        TETile[][] world=new TETile[width][height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                world[i][j]=Tileset.NOTHING;
            }
        }
        return world;
    }

    /**
     *
     * @param size hexagon size
     * @param bottomLeftPos the bottom left of the rectangle(inclusive) that bounds all the hexagons
     * @param startColumnCount hexagon count at the first column
     * @param maxColumnCount hexagon count at the middle column
     */
    private static void fillHexagon(TETile[][]world,int size,Vector2Int bottomLeftPos,int startColumnCount,int maxColumnCount,TETile[] tiles){
        if (size<=1||bottomLeftPos==null||tiles==null){
            return;
        }
        int hexagonHeight=2*size;
        int hexagonWidth=size+(size-1)*2;
        Vector2Int currentPointOffset=new Vector2Int(0,hexagonHeight);
        Vector2Int startPointOffset=new Vector2Int(2*size-1,-size);

        Random random=new Random(2873123);

        //put left half
        Vector2Int currentPoint=Vector2Int.
                plus(bottomLeftPos, Vector2Int.times(size*2,Vector2Int.UP) );
        Vector2Int startPoint=currentPoint;
        int currentCount=0;
        int targetCount=startColumnCount;
        while (targetCount<=maxColumnCount){
            //put one column
            while (currentCount<targetCount){
                putHexagon(world,currentPoint,size,tiles[random.nextInt(tiles.length)]);
                currentPoint=Vector2Int.plus(currentPoint,currentPointOffset);
                currentCount++;
            }
            //choose the next start point for putting next column
            targetCount++;
            currentCount=0;
            startPoint=Vector2Int.plus(startPoint,startPointOffset);
            currentPoint=startPoint;
        }
        //put the remaining columns,just repeat some code LOL
        startPoint=Vector2Int.minus(startPoint,startPointOffset);//retrieve back
        startPointOffset.y=-startPointOffset.y;
        startPoint=Vector2Int.plus(startPoint,startPointOffset);

        currentPoint=startPoint;
        currentCount=0;
        targetCount=maxColumnCount-1;
        while (startColumnCount<=targetCount){
            //put one column
            while (currentCount<targetCount){
                putHexagon(world,currentPoint,size,tiles[random.nextInt(tiles.length)]);
                currentPoint=Vector2Int.plus(currentPoint,currentPointOffset);
                currentCount++;
            }
            //choose the next start point for putting next column
            targetCount--;
            currentCount=0;
            startPoint=Vector2Int.plus(startPoint,startPointOffset);
            currentPoint=startPoint;
        }

    }



    /*
    put a hexagon that is bounded in a tall thin rectangle(inclusive),this may overlap other hexagon
    size should >=2
    the bottomLeftPos e.g. :
         aaa
        aaaaa
       aaaaaaa
       aaaaaaa
        aaaaa
       * aaa
       the positon of the "*" is the bottomLeftPos of this hexagon
     */
    private static void putHexagon(TETile[][] world,Vector2Int bottomLeftPos,int size,TETile tile){
        if (world==null||bottomLeftPos==null||size<=1||tile==null){
            return;
        }
        //put down half
        Vector2Int startPoint=Vector2Int.
                plus(bottomLeftPos,Vector2Int.times(size-1,Vector2Int.UP));
        Vector2Int startPointOffset=new Vector2Int(1,-1);
        int putCount=size+(size-1)*2;

        while (putCount>=size){
            putRow(world,startPoint,1,putCount,tile);
            startPoint=Vector2Int.plus(startPoint,startPointOffset);
            putCount-=2;
        }

        //put upper half,just repeat some code, LOL
        startPoint=Vector2Int.
                plus(bottomLeftPos,Vector2Int.times(size,Vector2Int.UP));
        startPointOffset=new Vector2Int(1,1);
        putCount=size+(size-1)*2;
        while (putCount>=size){
            putRow(world,startPoint,1,putCount,tile);
            startPoint=Vector2Int.plus(startPoint,startPointOffset);
            putCount-=2;
        }
    }
    //stride maybe positive(->) or negative(<-)
    //may stop when reach the boundary
    private static void putRow(TETile[][] world,Vector2Int startPos,int stride,int rowCount,TETile tile){
        int y=startPos.y;
        if (y<0||world[0].length<=y){
            return;
        }
        int putCount=0;
        for(int x =startPos.x; 0<=x&&x<=world.length&&putCount<rowCount;x+=stride){
            world[x][y]=tile;
            putCount++;
        }

    }
    private static void putColumn(TETile[][] world,Vector2Int startPos,int stride,int columnCount,TETile tile){
        int x=startPos.x;
        if (x<0||world.length<=x){
            return;
        }
        int putCount=0;
        for (int y = startPos.y; 0<=y&&y<world[0].length&& putCount<columnCount; y+=stride) {
            world[x][y]=tile;
            putCount++;
        }
    }

//    private static int getRowIndex(Vector2Int pos){
//        return pos.y;//note the order!
//    }
//    private static int getColumnIndex(Vector2Int pos){
//        return pos.x;//note the order!
//    }


}
