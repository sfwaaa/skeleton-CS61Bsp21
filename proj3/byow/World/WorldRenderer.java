package byow.World;

import byow.TileEngine.TERenderer;

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
    private World world;

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
     }

    /**
     * Initialize the renderer with the given world and constructor params
     */
     public void initialize(World world){
         this.world=world;
         this.renderer.initialize(canvasSize.x/tileSizeInPixel,canvasSize.y/tileSizeInPixel,
                 paddingLeft,paddingDown);
     }
     public void renderWorld(){
         this.renderer.renderFrame(world.getFrame());
     }

}
