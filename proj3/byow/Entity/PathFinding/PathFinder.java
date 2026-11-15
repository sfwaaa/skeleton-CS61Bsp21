package byow.Entity.PathFinding;

import byow.World.Vector2Int;
import byow.World.World;

/**
 * representing a path finding strategy.
 */
public interface PathFinder {
    /**
     * return the shortest path from the src to dst
     * @param world the world which only allows moving four direction(left/up/down/right)
     * @param src start position
     * @param dst destination
     * @return the shortest path from src to dest
     * with path[0]==src,path[length-1]==dst;null if no path to dst
     */
    public Vector2Int[] getPath(World world, Vector2Int src, Vector2Int dst);
}
