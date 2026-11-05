package byow.World.Generation.Room.Builder;

import byow.World.Generation.Room.Room;
import byow.World.Generation.RoomBoundary;
import byow.World.World;

import java.util.Random;

public class RandomRoomBuilder extends RoomBuilder{
    private RoomBuilder[] builders;
    private int i=0;
    public RandomRoomBuilder(RoomBuilder[] builders){
        this.builders=builders;
        this.i=0;
    }
    @Override
    public Room build(World world, RoomBoundary boundary, Random random) {

        return null;
    }
}
