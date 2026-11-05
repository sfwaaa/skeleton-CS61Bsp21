package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import byow.World.*;
import byow.World.Generation.Room.Builder.RoomBoundaryBuilder;
import byow.World.Generation.Room.Builder.RoomBuilder;
import byow.World.Generation.Room.Builder.SingleRoomBuilder;
import byow.World.Generation.Room.CircleRoom;
import byow.World.Generation.Room.RectangleRoom;
import byow.World.Generation.RoomBoundary;
import byow.World.Generation.WorldGenerator;

import java.util.Random;

//to be frank,this class should be named as "WorldController or InputHandler ...",
//rather than the vague name Engine
//this class is responsible for initializing renderers, taking input and calling method.
public class Engine {
    /**
     * Resolution(Canvas Size) : 1280x720 -> 80x45 tiles,
     * x for width,y for height
     */
    private static final Vector2Int CANVAS_SIZE=new Vector2Int(1280,720);
    private static final int TILE_SIZE_IN_PIXEL =16;//keep this 16 since TERenderer hardcodes it
    //leave some space for UI and empty space
    private static final int PADDING_LEFT=10;//
    private static final int PADDING_RIGHT=10;
    private static final int PADDING_UP=2;
    private static final int PADDING_DOWN=2;

    //seeds for testing
    private static final long[] TEST_SEEDS=new long[]
            {101750485,724895794,77392579,13487979,35793599};

    //in the future interact interface,all behaviors should be defined in the InputSource interface...
    //simple test
    public static void main(String[] args){
        WorldRenderer renderer=new WorldRenderer
                (CANVAS_SIZE,TILE_SIZE_IN_PIXEL,PADDING_LEFT,PADDING_DOWN);
        Vector2Int worldSize=WorldRenderer.getWorldSize(CANVAS_SIZE,TILE_SIZE_IN_PIXEL,
                PADDING_LEFT,PADDING_RIGHT,PADDING_DOWN,PADDING_UP);

        SingleRoomBuilder rectRoomBuilder=
                new SingleRoomBuilder(new RectangleRoom(), Tileset.WALL);
        SingleRoomBuilder circleRoomBuilder=
                new SingleRoomBuilder(new CircleRoom(), Tileset.WALL);
        WorldGenerator generator=new WorldGenerator(circleRoomBuilder,null);

        World world =generator.generateWorld(worldSize,TEST_SEEDS[1]);

        renderer.initialize(world);
        renderer.renderWorld();

//        World textWorld=new World(10,10,Tileset.NOTHING );
//        RoomBoundary boundary=new RoomBoundary(Vector2Int.ZERO,8,8);
//        CircleRoom room=new CircleRoom();
//        room.initializeBoundary(textWorld,boundary,new Random());
//        room.buildRoomTiles(Tileset.WALL);
//        System.out.println(TETile.toString(textWorld.getFrame()));
    }

    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */
    public void interactWithKeyboard() {
    }

    /**
     * Method used for autograding and testing your code. The input string will be a series
     * of characters (for example, "n123sswwdasdassadwas", "n123sss:q", "lwww". The engine should
     * behave exactly as if the user typed these characters into the engine using
     * interactWithKeyboard.
     *
     * Recall that strings ending in ":q" should cause the game to quite save. For example,
     * if we do interactWithInputString("n123sss:q"), we expect the game to run the first
     * 7 commands (n123sss) and then quit and save. If we then do
     * interactWithInputString("l"), we should be back in the exact same state.
     *
     * In other words, both of these calls:
     *   - interactWithInputString("n123sss:q")
     *   - interactWithInputString("lww")
     *
     * should yield the exact same world state as:
     *   - interactWithInputString("n123sssww")
     *
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */
    public TETile[][] interactWithInputString(String input) {
        // TODO: Fill out this method so that it run the engine using the input
        // passed in as an argument, and return a 2D tile representation of the
        // world that would have been drawn if the same inputs had been given
        // to interactWithKeyboard().
        //
        // See proj3.byow.InputDemo for a demo of how you can make a nice clean interface
        // that works for many different input types.

        TETile[][] finalWorldFrame = null;
        return finalWorldFrame;
    }
}
