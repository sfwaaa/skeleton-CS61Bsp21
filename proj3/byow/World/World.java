package byow.World;

import byow.TileEngine.*;

/**
 * This class represents the 2D tile world,
 * every tile operation except rendering
 * should be performed via this class
 */
public class World {
    /**
     * The 2D-plane with [0][0] at the origin,
     * [0][heightMax-1] on the uppermost on y-axis,
     * [widthMax-1][0] on the rightmost on x-axis,
     * which is consistent with the position represented by a Vector2Int object,
     * but different from the memory layout
     */
    private TETile[][] tilePlane;

    private final int widthMax;
    private final int heightMax;
    private final TETile emptyTile;//tile representing "empty"

    /**
     * Create an empty world
     * @param width the max count of tiles on x-axis
     * @param height the max count of tiles on y-axis
     * @param emptyTile tile for "empty"
     */
    public World(int width,int height,TETile emptyTile){
        this.widthMax=width;
        this.heightMax=height;
        this.emptyTile=emptyTile;
        this.tilePlane=new TETile[width][height];
        fill(emptyTile);
    }
    /**
     * Create an empty world
     * @param size  the size of the world with x for
     *              the max count of tiles on x-axis,
     *              y for the max count of tiles on y-axis
     * @param emptyTile tile for "empty"
     */
    public World(Vector2Int size,TETile emptyTile){
        this(size.x,size.y,emptyTile);
    }

    public int getWidth(){
        return this.widthMax;
    }
    public int getHeight(){
        return this.heightMax;
    }
    public boolean validPosition(Vector2Int position){
        return  position!=null&&
                0<=position.x && position.x<widthMax &&
                0<=position.y && position.y<heightMax;
    }
    public boolean validPosition(int x,int y){
        return  0<=x && x<widthMax &&
                0<=y && y<heightMax;
    }

    /**
     * return the frame to render , DON'T modify it outside!!
     */
    public TETile[][] getFrame(){
        return this.tilePlane;
    }

    /**
     * check whether there is a nonempty tile in the given position
     * the equal() is based on comparing the char of the TETile
     * @return true if there is a nonempty tile in the given position
     */
    public boolean existsTile(Vector2Int position){
        if(!validPosition(position)){
            return false;
        }
        return tilePlane[position.x][position.y].character()!=emptyTile.character();
    }

    /**
     * Set the tile in the position,return the previous one,null if invalid position
     */
    public TETile put(Vector2Int position,TETile tile){
        if(!validPosition(position)){
            return null;
        }
        TETile previous= this.tilePlane[position.x][position.y];
        this.tilePlane[position.x][position.y]=tile;
        return previous;
    }
    /**
     *
     * @param stride stride maybe positive(->) or negative(<-)
     */
    public void putRow(Vector2Int startPos,int stride,int rowCount,TETile tile){
        if(!validPosition(startPos)){
            return;
        }
        int y=startPos.y;
        int putCount=0;
        for(int x =startPos.x; validPosition(x,y)&&putCount<rowCount;x+=stride){
            tilePlane[x][y]=tile;
            putCount++;
        }
    }
    public void putColumn(Vector2Int startPos,int stride,int columnCount,TETile tile){
        if(!validPosition(startPos)){
            return;
        }
        int x=startPos.x;
        int putCount=0;
        for (int y = startPos.y; validPosition(x,y)&& putCount<columnCount; y+=stride) {
            tilePlane[x][y]=tile;
            putCount++;
        }
    }

    private void fill(TETile tile){
        for (int x = 0; x < widthMax; x++) {
            for (int y = 0; y < heightMax; y++) {
                tilePlane[x][y]=tile;
            }
        }
    }
}
