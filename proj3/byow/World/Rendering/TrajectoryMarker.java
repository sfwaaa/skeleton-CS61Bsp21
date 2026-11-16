package byow.World.Rendering;

import byow.Entity.*;
import byow.TileEngine.*;
import byow.World.Vector2Int;

/**
 * Marking the tracer's trajectory
 */
public class TrajectoryMarker extends PostProcessor{

    private final Tracer tracer;
    private final TETile[] coverableTiles;
    private final TETile mark;
    public TrajectoryMarker(Tracer tracer, TETile[] coverableTiles, TETile mark){
       this.tracer=tracer;
       this.coverableTiles=coverableTiles;
       this.mark=mark;
    }

    @Override
    public void process(TETile[][] frame) {
        Vector2Int[] path=tracer.getPath();
        if (path==null){
            return;
        }
        Vector2Int pos;
        for (int i = 1; i < path.length; i++) {
            pos=path[i];
            if (coverableAt(frame, pos)) {
                frame[pos.x][pos.y]=mark;
            }
        }
    }

    private boolean coverableAt(TETile[][] frame,Vector2Int position){
        char ch=frame[position.x][position.y].character();
        for (int i = 0; i < coverableTiles.length; i++) {
            if (ch==coverableTiles[i].character()){
                return true;
            }
        }
        return false;
    }
}
