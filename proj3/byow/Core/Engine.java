package byow.Core;

import byow.Input.InputReader;
import byow.Input.KeyboardInputReader;
import byow.Input.Processor.MainMenuInputProcessor;
import byow.Input.Processor.SeedProcessor;
import byow.TileEngine.*;
import byow.UI.UIDrawer;
import byow.World.*;
import byow.World.Generation.Room.Builder.*;
import byow.World.Generation.Room.*;
import byow.World.Generation.*;

import java.awt.*;
import java.rmi.server.UID;

//to be frank,this class should be named as "GameplayManager or GameFlowController",
//rather than the vague name Engine
//this class is responsible for initializing each module and construct the game loop
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

    private long inputSeed=0;

    //seeds for testing
    private static final long[] TEST_SEEDS=new long[]
            {101750485,724895794,77392579,13487979,
             35793599,27932983,43892472,23374293,
             538429348,45799375,553792934,432943299,
             839242039,324392434,647329429};

    //in the future interact interface,all behaviors should be defined in the InputSource interface...
    //simple test
    public static void main(String[] args){//USED FOR TESTING
        /*
        Vector2Int worldSize=WorldRenderer.getWorldSize(CANVAS_SIZE,TILE_SIZE_IN_PIXEL,
                PADDING_LEFT,PADDING_RIGHT,PADDING_DOWN,PADDING_UP);
        Engine engine=new Engine();
        WorldGenerator generator=engine.getWorldGenerator();
        World world=generator.generateWorld(worldSize,TEST_SEEDS[13], Tileset.NOTHING);
        WorldRenderer renderer=engine.initializeWorldRenderer(world,CANVAS_SIZE,
                TILE_SIZE_IN_PIXEL,PADDING_LEFT,PADDING_DOWN);
        renderer.renderWorld();
        */

        /*
        Vector2Int worldSize=WorldRenderer.getWorldSize(CANVAS_SIZE,TILE_SIZE_IN_PIXEL,
                PADDING_LEFT,PADDING_RIGHT,PADDING_DOWN,PADDING_UP);
        Engine engine=new Engine();
        UIDrawer uiDrawer=new UIDrawer(CANVAS_SIZE,TILE_SIZE_IN_PIXEL);
        WorldRenderer renderer=engine.initializeWorldRenderer
                (CANVAS_SIZE,TILE_SIZE_IN_PIXEL,PADDING_LEFT,PADDING_DOWN);
        uiDrawer.drawMainMenu();
        */

        Engine engine=new Engine();
        engine.interactWithKeyboard();
    }

    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */
    public void interactWithKeyboard() {
        //only use constant in this method...

        Vector2Int worldSize=WorldRenderer.getWorldSize(CANVAS_SIZE,TILE_SIZE_IN_PIXEL,
                PADDING_LEFT,PADDING_RIGHT,PADDING_DOWN,PADDING_UP);
        WorldGenerator generator=this.getWorldGenerator();
        WorldRenderer renderer=this.initializeWorldRenderer(CANVAS_SIZE,
                TILE_SIZE_IN_PIXEL,PADDING_LEFT,PADDING_DOWN);
        UIDrawer uiDrawer=new UIDrawer(CANVAS_SIZE,TILE_SIZE_IN_PIXEL);
        InputReader inputReader=new KeyboardInputReader();
        boolean startGame=mainMenuLoop(uiDrawer,inputReader,new MainMenuInputProcessor());
        if (!startGame){
            return;
        }
        World world=generator.generateWorld(worldSize,inputSeed,Tileset.NOTHING);
        renderer.renderWorld(world);
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

        //only use constant in this method...
        //below is just some copied stuff from interactWithKeyboard() just removing some rendering callings
        //I don't care much LOL

        TETile[][] finalWorldFrame = null;
        return finalWorldFrame;
    }

    //return whether start the game
    private boolean mainMenuLoop(UIDrawer uiDrawer, InputReader reader, MainMenuInputProcessor inputProcessor){
        uiDrawer.drawMainMenu();
        Character in=null;
        while (true){
            in=reader.readInput();
            if (inputProcessor.requestNewWorld(in)){
                readSeed(uiDrawer,reader,inputProcessor);
                return true;
            }
            if (inputProcessor.requestLoadWorld(in)){
                uiDrawer.drawText(new Vector2Int(CANVAS_SIZE.x/2,3),UIDrawer.FONT_HUD,Color.WHITE,"Work in Progress");
                return false;
            }
            if (inputProcessor.requestQuit(in)){
                uiDrawer.drawText(new Vector2Int(CANVAS_SIZE.x/2,0),UIDrawer.FONT_HUD,Color.WHITE,"Work in Progress");
                return false;
            }
        }
    }
    private void readSeed(UIDrawer uiDrawer,InputReader reader,MainMenuInputProcessor inputProcessor){
        SeedProcessor seedProcessor=new SeedProcessor();
        Character in;
        in=reader.readInput();

        //some hardcode prompt I don't care
        String prompt="Entering a seed:";
        String wrongPrompt="Invalid seed,enter another one:";
        String currentPrompt=prompt;

        Vector2Int promptPosition=new Vector2Int(CANVAS_SIZE.x/2,15+CANVAS_SIZE.y/2);
        Vector2Int seedPosition=new Vector2Int(CANVAS_SIZE.x/2,-15+CANVAS_SIZE.y/2);

        uiDrawer.clearCanvas();
        uiDrawer.drawText(promptPosition,UIDrawer.FONT_OPTION, Color.WHITE,currentPrompt);
        uiDrawer.drawText(seedPosition,UIDrawer.FONT_OPTION, Color.WHITE,"test");

        this.inputSeed=-1;
        for(in=reader.readInput();this.inputSeed<=0;in=reader.readInput()){
            if (in==null){
                continue;
            }
            if(inputProcessor.doneEnteringSeed(in)){
                this.inputSeed=seedProcessor.getSeed();
                //invalid seed ,show wrong prompt
                if(inputSeed<=0){
                    currentPrompt=wrongPrompt;
                    uiDrawer.clearCanvas();
                    uiDrawer.drawText(promptPosition,UIDrawer.FONT_OPTION, Color.WHITE,currentPrompt);
                }
                continue;
            }
            seedProcessor.feed(in);
            uiDrawer.clearCanvas();
            uiDrawer.drawText(promptPosition,UIDrawer.FONT_OPTION, Color.WHITE,currentPrompt);
            uiDrawer.drawText(seedPosition,UIDrawer.FONT_OPTION,
                    Color.WHITE,seedProcessor.getSeedString());
        }

    }

    private WorldGenerator getWorldGenerator(){
        //hard coded tiles I don't care :(
        SingleRoomBuilder rectRoomBuilder=
                new SingleRoomBuilder(new RectangleRoom(),new TETile[]{Tileset.FLOOR});
        SingleRoomBuilder circleRoomBuilder=
                new SingleRoomBuilder(new CircleRoom(), new TETile[]{Tileset.FLOOR});
        RandomRoomBuilder randomRoomBuilder=new RandomRoomBuilder
                (new RoomBuilder[]{rectRoomBuilder,circleRoomBuilder});

        RoomConnector connector=new RoomConnector(Tileset.FLOOR);

        WorldGenerator generator=new WorldGenerator(randomRoomBuilder,connector,new RoomWallBuilder(Tileset.WALL));
        return generator;
    }
    private WorldRenderer initializeWorldRenderer(
            Vector2Int canvasSizeInPixel,int tileSizeInPixel,
            int paddingLeft,int paddingDown){

        WorldRenderer renderer=new WorldRenderer
                (canvasSizeInPixel,tileSizeInPixel,paddingLeft,paddingDown);
        renderer.initializeCanvas();
        return renderer;
    }
}
