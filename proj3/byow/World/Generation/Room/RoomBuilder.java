package byow.World.Generation.Room;

import byow.World.Generation.*;
import byow.World.*;

/**
 * Simply choose which room to build
 */
public abstract class RoomBuilder {

    public abstract void build(World world, RoomBoundary boundary);

}
