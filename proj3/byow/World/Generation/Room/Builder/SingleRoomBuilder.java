package byow.World.Generation.Room.Builder;

import byow.TileEngine.TETile;
import byow.World.Generation.Room.Room;
import byow.World.Generation.RoomBoundary;
import byow.World.Vector2Int;
import byow.World.World;

import java.util.Random;

public class SingleRoomBuilder extends RoomBuilder{
    private Room room;
    private TETile[] floors;
    /**
     * Create a builder
     * @param roomObject used to create object
     */
    public SingleRoomBuilder(Room roomObject, TETile[] floors){
        this.room=roomObject;
        this.floors = floors;
    }
    @Override
    public Room build(World world, RoomBoundary boundary, Random random){
        Room newRoom =room.getEmptyRoom();
        newRoom.initialize(world,boundary,random,floors);
        if(newRoom.buildRoomFloor()){
            return newRoom;
        }
        else {
            return null;
        }
    }

    @Override
    public Vector2Int minRequiredSize() {
        return room.minRequiredSize();
    }
}
