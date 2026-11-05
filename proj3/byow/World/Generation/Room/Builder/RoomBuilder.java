package byow.World.Generation.Room.Builder;

import byow.World.Generation.*;
import byow.World.*;
import byow.World.Generation.Room.Room;

import java.util.Random;

/**
 * Simply choose which room to build
 */
public abstract class RoomBuilder {
    /**
     * Build the room in the give boundary,return the room if built,otherwise return null
     */
    public abstract Room build(World world, RoomBoundary boundary, Random random);

}
