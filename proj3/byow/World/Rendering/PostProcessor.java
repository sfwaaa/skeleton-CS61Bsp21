package byow.World.Rendering;

import byow.TileEngine.TETile;

/**
 * Processing the world frame before rendering it to screen
 */
public abstract class PostProcessor {
    /**
     * Is the postProcessor enabled?
     * Only call the process() when it is enabled!
     */
    public boolean enabled=true;

    /**
     * process the frame to get fancy effect
     * @param frame frame to be drawn to the screen
     */
    public abstract void process(TETile[][] frame);
}
