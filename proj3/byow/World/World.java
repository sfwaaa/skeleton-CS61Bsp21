package byow.World;

import byow.TileEngine.*;

/**
 * This class represents the 2D tile world with two-dimensional plane coordinate system.
 * Every tile operation except rendering should be performed via this class.
 * Note : TETile should always stay immutable!! Or we may need an object pool to
 * manage the creation and destruction of tiles to avoid bugs!
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

    /**
     * @return whether the position is inside the world
     */
    public boolean validPosition(Vector2Int position){
        return  position!=null&&
                0<=position.x && position.x<widthMax &&
                0<=position.y && position.y<heightMax;
    }
    /**
     * @return whether the position is inside the world
     */
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
     * check whether there is a nonempty tile in the given position
     * the equal() is based on comparing the char of the TETile
     * @return true if there is a nonempty tile in the given position
     */
    public boolean existsTile(int x,int y){
        if(!validPosition(x,y)){
            return false;
        }
        return tilePlane[x][y].character()!=emptyTile.character();
    }
    /**
     * check whether there is a nonempty tile around the given position
     * the equal() is based on comparing the char of the TETile
     * @return true if there is a nonempty tile
     * in the left/right/up/down/up_left/up_right/down_left/down_right of the given position
     */
    public boolean existsSurroundingTiles(Vector2Int position){
       return existsSurroundingTiles(position.x,position.y);
    }
    //note : this method hasn't been tested yet.
    /**
     * check whether there is a nonempty tile around the given position
     * the equal() is based on comparing the char of the TETile
     * @return true if there is a nonempty tile
     * in the left/right/up/down/up_left/up_right/down_left/down_right of the given position
     */
    public boolean existsSurroundingTiles(int x,int y){
        if (!validPosition(x,y)){
            return false;
        }
        //left
        if (existsTile(x-1,y)){
            return true;
        }
        //right
        if (existsTile(x+1,y)){
            return true;
        }
        //up
        if (existsTile(x,y+1)){
            return true;
        }
        //down
        if (existsTile(x,y-1)){
            return true;
        }
        //up_left
        if (existsTile(x-1,y+1)){
            return true;
        }
        //up_right
        if (existsTile(x+1,y+1)){
            return true;
        }
        //down_left
        if (existsTile(x-1,y-1)){
            return true;
        }
        //down_right
        if (existsTile(x+1,y-1)){
            return true;
        }
        return false;
    }

    /**
     * get the tile in the given position
     * @return null if invalid position
     */
    public TETile getTile(Vector2Int position){
        return this.getTile(position.x,position.y);
    }

    /**
     * get the tile in the given position
     * @return null if invalid position
     */
    public TETile getTile(int x,int y){
        if (!validPosition(x,y)){
            return null;
        }
        return tilePlane[x][y];
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
     * Set the tile of the position to empty ,return the previous one,null if invalid position
     */
    public TETile clearAt(Vector2Int position){
        return put(position,this.emptyTile);
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

    /**
     * Swap two tile in the world,not checking the swapped tile
     */
    public void swap(Vector2Int p1,Vector2Int p2){
        if (!validPosition(p1)||!validPosition(p2)){
            return;
        }
        TETile t1=this.tilePlane[p1.x][p1.y];
        tilePlane[p1.x][p1.y]=tilePlane[p2.x][p2.y];
        tilePlane[p2.x][p2.y]=t1;
    }

    private void fill(TETile tile){
        for (int x = 0; x < widthMax; x++) {
            for (int y = 0; y < heightMax; y++) {
                tilePlane[x][y]=tile;
            }
        }
    }
}
