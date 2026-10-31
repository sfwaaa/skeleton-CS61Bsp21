package byow.World;

/**
 * Rendering the world,avoid using TERenderer directly
 * This class directly renders the whole world,
 * so override it to implement fancy rendering effect
 */
public class WorldRenderer {

    public WorldRenderer
            (Vector2Int canvasSize,int tileSizeInPixel,
             int paddingLeft,int paddingDown){

    }

    public void initialize(World world){

    }
    /**
     * Calculate the size of the world that a renderer can (at least) render
     * @param canvasSize format:(width,height),e.g. (1280,720)
     */
    public static Vector2Int getWorldSize
    (Vector2Int canvasSize,int tileSizeInPixel,
     int paddingLeft,int paddingRight,int paddingDown,int paddingUp){
        return Vector2Int.ZERO;
    }
}
