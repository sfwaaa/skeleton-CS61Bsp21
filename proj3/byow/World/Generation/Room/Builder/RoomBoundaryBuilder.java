package byow.World.Generation.Room.Builder;

import byow.Core.RandomUtils;
import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import byow.World.Generation.*;
import byow.World.*;
import byow.World.Generation.Room.Room;

import java.util.Random;

//simply mark the boundary,ONLY USED FOR DEBUGGING!!
public class RoomBoundaryBuilder extends RoomBuilder {
    private static TETile[] TILES=new TETile[]
            {Tileset.TREE,Tileset.WALL,Tileset.WATER};
    private static final Random random=new Random(114514);
    private static final TERenderer renderer=new TERenderer();
    public RoomBoundaryBuilder(){
        RoomBoundaryBuilder.renderer.initialize(1280/16,720/16,10,2);
    }
    @Override
    public Room build(World world, RoomBoundary boundary,Random random) {
        TETile tile=TILES[RandomUtils.uniform(random,TILES.length)];

        world.putRow(boundary.bottomLeft,1,
                boundary.width,tile);
        Vector2Int topLeft=new Vector2Int
                (boundary.bottomLeft.x,boundary.bottomLeft.y+boundary.height-1);
        world.putRow(topLeft,1,boundary.width,tile);

        world.putColumn
                (boundary.bottomLeft,1,boundary.height,tile);
        Vector2Int bottomRight=new Vector2Int
                (boundary.bottomLeft.x+boundary.width-1,boundary.bottomLeft.y);
        world.putColumn
                (bottomRight,1,boundary.height,tile);
        renderer.renderFrame(world.getFrame());
        return null;
    }

    @Override
    public Vector2Int minRequiredSize() {
        return null;
    }
}
