package byow.Core;

import byow.Entity.MovableObject;
import byow.Input.Processor.*;
import byow.Input.*;
import byow.Persistent.GamePrefs;
import byow.TileEngine.*;
import byow.UI.UIDrawer;
import byow.World.*;
import byow.World.Generation.Room.Builder.*;
import byow.World.Generation.Room.*;
import byow.World.Generation.*;

import java.awt.*;
import java.util.Random;

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
    private static final int PADDING_UP=4;
    private static final int PADDING_DOWN=2;

    private static final int HUD_HEIGHT=(CANVAS_SIZE.y)-(PADDING_UP/2)*TILE_SIZE_IN_PIXEL;

    private long inputSeed=0;
    private World world=null;
    private WorldRenderer worldRenderer=null;
    private UIDrawer uiDrawer=null;
    private Vector2Int initialPlayerPosition =null;
    private GamePrefs prefs;


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
        //N999SDDDWWWDDD
        TETile[][] wo= engine.interactWithInputString("LWWWDDD");
        System.out.println(TETile.toString(wo));

        //Engine engine=new Engine();
        //engine.interactWithKeyboard();
    }



    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     * In fact,it's indeed a trash...
     */
    public void interactWithKeyboard() {
        //only use constants in this method...
        Vector2Int worldSize=WorldRenderer.getWorldSize(CANVAS_SIZE,TILE_SIZE_IN_PIXEL,
                PADDING_LEFT,PADDING_RIGHT,PADDING_DOWN,PADDING_UP);
        WorldGenerator generator=this.getWorldGenerator();
        worldRenderer=this.initializeWorldRenderer(CANVAS_SIZE,
                TILE_SIZE_IN_PIXEL,PADDING_LEFT,PADDING_DOWN);
        uiDrawer=new UIDrawer(CANVAS_SIZE,TILE_SIZE_IN_PIXEL);
        InputReader inputReader=new KeyboardInputReader();
        this.prefs=new GamePrefs();
        boolean startGame=mainMenuLoop(uiDrawer,inputReader,new MainMenuInputProcessor());
        if (!startGame){
            return;
        }
        world=generator.generateWorld(worldSize,inputSeed,Tileset.NOTHING);

        MovableObject player=initializePlayer(generator);
        worldRenderer.renderWorld(world);
        exploringLoop(inputReader,new PlayerInputProcessor(),player);
        prefs.save(inputSeed,player.getCurrentPosition());
        System.out.println("exit!");
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
        Vector2Int worldSize=WorldRenderer.getWorldSize(CANVAS_SIZE,TILE_SIZE_IN_PIXEL,
                PADDING_LEFT,PADDING_RIGHT,PADDING_DOWN,PADDING_UP);
        WorldGenerator generator=this.getWorldGenerator();
        MainMenuInputProcessor menuProcessor=new MainMenuInputProcessor();
        InputReader reader=new StringInputReader(input);
        this.prefs=new GamePrefs();

        Character in=null;
        boolean generateWorld=false;
        while (true){
            in=reader.readInput();
            if (menuProcessor.requestNewWorld(in)){
                readSeedWithoutRendering(reader,menuProcessor);
                generateWorld=true;
                break;
            }
            if (menuProcessor.requestLoadWorld(in)){
                if (!this.prefs.hasSavedData()){
                    return null;
                }
                this.inputSeed=prefs.loadSeed();
                this.initialPlayerPosition =prefs.loadPlayerPosition();
                generateWorld=true;
                break;
            }
            if (menuProcessor.requestQuit(in)){
                return null;
            }
        }
        if (!generateWorld){
            return null;
        }
        world=generator.generateWorld(worldSize,this.inputSeed,Tileset.NOTHING);
        MovableObject player=initializePlayer(generator);

        PlayerInputProcessor playerInputProcessor=new PlayerInputProcessor();
        while ((in=reader.readInput())!=null){
            if (playerInputProcessor.getQuit(in)){
                break;
            }
            player.move(playerInputProcessor.getMove(in));
        }
        prefs.save(inputSeed,player.getCurrentPosition());
        return world.getFrame();
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
                if (!this.prefs.hasSavedData()){
                    System.out.println("no saved data");
                    return false;
                }
                this.inputSeed=prefs.loadSeed();
                this.initialPlayerPosition =prefs.loadPlayerPosition();
                return true;
            }
            if (inputProcessor.requestQuit(in)){
                //uiDrawer.drawText(new Vector2Int(CANVAS_SIZE.x/2,0),UIDrawer.FONT_HUD,Color.WHITE,"Work in Progress");
                return false;
            }
        }
    }
    //may require passing so many params, so make some of them member variables
    private void exploringLoop(InputReader inputReader,PlayerInputProcessor inputProcessor,MovableObject player){
        Vector2Int tilePosition;
        TETile hudTile=null;
        String previousHud="";
        boolean drawHud=false;
        boolean renderingWorld=false;
        Character in;
        while (true){
            //check hud
            tilePosition=calculateTileBelowMouse(inputProcessor.getMousePosition());
            if (world.validPosition(tilePosition)){
                hudTile=world.getTile(tilePosition);
                drawHud=!hudTile.description().equals(previousHud);
                renderingWorld=renderingWorld||drawHud;
                previousHud=hudTile.description();
            }

            in=inputReader.readInput();
            renderingWorld=renderingWorld||
                   player.move(inputProcessor.getMove(in));

            if (inputProcessor.getQuit(in)){
                break;
            }

            //process rendering
            if (renderingWorld){
                this.worldRenderer.renderWorld(world);
                renderingWorld=false;
            }
            if (drawHud){
                this.drawTileDescription(hudTile.description(),this.uiDrawer);
                drawHud=false;
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
        //uiDrawer.drawText(seedPosition,UIDrawer.FONT_OPTION, Color.WHITE,"test");

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

    private void readSeedWithoutRendering(InputReader reader,MainMenuInputProcessor inputProcessor){
        SeedProcessor seedProcessor=new SeedProcessor();
        Character in;
        this.inputSeed=-1;
        for(in=reader.readInput();this.inputSeed<=0;in=reader.readInput()){
            if (in==null){
                continue;
            }
            if(inputProcessor.doneEnteringSeed(in)){
                this.inputSeed=seedProcessor.getSeed();
            }
            seedProcessor.feed(in);
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

    private void drawTileDescription(String content,UIDrawer drawer){
        Vector2Int hudPosition=new Vector2Int(content.length()*TILE_SIZE_IN_PIXEL/2,HUD_HEIGHT);
        drawer.drawText(hudPosition,UIDrawer.FONT_HUD,Color.WHITE,content);
    }

    /**
     * Transform the mousePosition into a tile position
     */
    private Vector2Int calculateTileBelowMouse(Vector2Int mousePosition){
        //System.out.println("mouse at : "+mousePosition);
        return new Vector2Int(mousePosition.x-PADDING_LEFT,mousePosition.y-PADDING_DOWN);
    }

    /**
     * Initialize objects,set them into the world randomly
     * @param avatars avatars whit each representing a movable objects to be initialized
     * @return initialized objects,the same length as avatars
     */
    private MovableObject[] initializeMoveableObjects(World world,WorldGenerator generator,TETile[] obstacles,TETile[] avatars){
        if (avatars.length==0){
            return null;
        }
        Random random=new Random(this.inputSeed);
        MovableObject[] objects=new MovableObject[avatars.length];
        MovableObject newObject;
        Vector2Int randomPosition=null;
        Room[] rooms=generator.getBuiltRooms();
        for (int i = 0; i < avatars.length; i++) {
           newObject=new MovableObject(world,obstacles,avatars[i]);
           randomPosition=rooms[RandomUtils.uniform(random,rooms.length)].
                   randomValidPosition();
           newObject.setInitialPosition(randomPosition);
           objects[i]=newObject;
        }
        return objects;
    }
    private MovableObject initializePlayer(WorldGenerator generator){
       if (this.initialPlayerPosition ==null){
           MovableObject[] entities=this.initializeMoveableObjects(this.world,generator,
                   new TETile[]{Tileset.WALL},new TETile[]{Tileset.AVATAR});
           return entities[0];
       }
       MovableObject player=new MovableObject(this.world,
               new TETile[]{Tileset.WALL},Tileset.AVATAR);
       player.setInitialPosition(initialPlayerPosition);
       return player;
    }
}
