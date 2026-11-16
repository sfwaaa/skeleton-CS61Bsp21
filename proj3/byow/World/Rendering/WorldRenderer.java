package byow.World.Rendering;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.World.Vector2Int;
import byow.World.World;

/**
 * Rendering the world,avoid using TERenderer directly
 * This class directly renders the whole world, add some processor to
 * implement fancy rendering effects (rather than override this class)
 */
public class WorldRenderer {

    private TERenderer renderer;
    private final Vector2Int canvasSize;
    private final int tileSizeInPixel;
    private final int paddingLeft;
    private final int paddingDown;
    private PostProcessor[] postProcessors;

    /**
     * Calculate the size of the world that a renderer can (at least) render
     * @param canvasSize format:(width,height),e.g. (1280,720)
     */
    public static Vector2Int getWorldSize
    (Vector2Int canvasSize,int tileSizeInPixel,
     int paddingLeft,int paddingRight,int paddingDown,int paddingUp){
        Vector2Int worldSize=new Vector2Int();
        worldSize.x=(canvasSize.x/tileSizeInPixel)-paddingLeft-paddingRight;
        worldSize.y=(canvasSize.y/tileSizeInPixel)-paddingDown-paddingUp;
        return worldSize;
    }

    public WorldRenderer
            (Vector2Int canvasSize,int tileSizeInPixel,
             int paddingLeft,int paddingDown){
        this.renderer=new TERenderer();
        this.canvasSize=canvasSize;
        this.tileSizeInPixel=tileSizeInPixel;
        this.paddingDown=paddingDown;
        this.paddingLeft=paddingLeft;
        this.postProcessors=new PostProcessor[0];
     }

    /**
     * Initialize the renderer with the given world and constructor params,
     * due to the tile engine,I must use the way to initialize my canvas
     */
     public void initializeCanvas(){
         this.renderer.initialize(canvasSize.x/tileSizeInPixel,canvasSize.y/tileSizeInPixel,
                 paddingLeft,paddingDown);
     }

    /**
     * assign the processor to post process the frame.
     * The renderer will process the frame from postProcessors[0] to the last one
     */
     public void setPostProcessors(PostProcessor[] postProcessors){
         if (postProcessors==null){
             return;
         }
         this.postProcessors=postProcessors;
     }
     public void renderWorld(World world){
         //note: a TETile is immutable
         this.renderer.renderFrame(processFrame(world.getFrame()));
     }

    /**
     * process the frame,return a copy of it
     * @return copy of the processed frame
     */
     private TETile[][] processFrame(TETile[][] frame){
         int width=frame.length;
         int height=frame[0].length;
         TETile[][] pendingFrame=new TETile[width][height];
         for (int x = 0; x < width; x++) {
             System.arraycopy(frame[x], 0, pendingFrame[x], 0, height);
         }
         for (PostProcessor postProcessor : postProcessors) {
             if (postProcessor.enabled) {
                 postProcessor.process(pendingFrame);
             }
         }
         return pendingFrame;
     }
}
