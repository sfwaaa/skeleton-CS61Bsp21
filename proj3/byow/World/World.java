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
}
