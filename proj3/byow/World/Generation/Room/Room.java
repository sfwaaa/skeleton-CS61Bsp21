package byow.World.Generation.Room;

import byow.TileEngine.TETile;
import byow.World.Generation.RoomBoundary;
import byow.World.Vector2Int;
import byow.World.World;

import java.util.Random;

/**
 * Representing a room object in a world
 * Override it to support different kind of room
 */
public abstract class Room {
    protected RoomBoundary boundary;
    protected World world;
    protected Random random;
    protected TETile[] floorMaterials;//used for building floor

    /**
     * Set the world and the boundary that the room will be built in
     * MUST call it before calling the buildRoomFloor() method
     * @param boundary room boundary(inclusive)
     * @param floors floor tiles used to build the room.
     *               Should obey the derived classes' rules of passing the tiles
     */
    public void initialize(World world, RoomBoundary boundary, Random random,TETile[] floors){
        this.boundary=boundary;
        this.world=world;
        this.random=random;
        this.floorMaterials=floors;
    }

    //only putting the floor makes life easier :)
    /**
     * put the floor tiles into the room
     * @return true if built successfully
     */
    public abstract boolean buildRoomFloor();
    /**
     *
     * @param direction The direction that come from room to the outside
     * @return the position of the entrance
     */
    public abstract Vector2Int buildEntrance(Vector2Int direction);

    /**
     * return the min size required to build the room
     */
    public abstract Vector2Int minRequiredSize();
    //f**k java I have to do this to creat instance in the builder class
    public abstract Room getEmptyRoom();
    //another possible method :
    //boolean bounded(Vector2 pos) is the pos bounded in this room?
}
