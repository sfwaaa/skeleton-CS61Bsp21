package byow.World.Rendering;

import byow.Entity.MovableObject;
import byow.TileEngine.TETile;
import byow.World.Vector2Int;

import java.awt.*;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Queue;

/**
 * restrict player vision
 */
public class FogVision extends PostProcessor {

    private class Node{
        public final Vector2Int position;
        public int distanceFromCenter;
        public Node(Vector2Int position,int distanceFromCenter){
            this.position=position;
            this.distanceFromCenter=distanceFromCenter;
        }
        @Override
        public boolean equals(Object obj) {
            if (obj==this){
                return true;
            }
            if (obj==null||obj.getClass()!=Node.class){
                return false;
            }
            return Vector2Int.equal(this.position,((Node) obj).position);
        }
        @Override
        public int hashCode() {
            return this.position.hashCode();
        }
    }

    //distance between center and left/up/down/right
    private static final int INCREMENT_STRAIGHT =10;
    //distance between center and up_left/up_right/down_left/down_right
    private static final int INCREMENT_DIAGONAL=14;

    private final MovableObject center;
    private final TETile[] obstacles;
    private final TETile fog;
    private int radius;

    private HashMap<Vector2Int,Integer> visiblePositions;//mark searched position and their position

    /**
     * @param obstacles tiles around obstacles will not be searched
     */
    public FogVision(MovableObject center,TETile fog,TETile[] obstacles){
        this.center=center;
        this.obstacles=obstacles;
        this.fog=fog;
        radius=6* INCREMENT_STRAIGHT;
        visiblePositions=new HashMap<>();
    }

    public void setRadius(float radius){
        if (radius<=0){
            return;
        }
        this.radius=Math.round(radius* INCREMENT_STRAIGHT);
    }


    @Override
    public void process(TETile[][] frame) {
        Vector2Int centerPosition=center.getCurrentPosition();
        searchVisiblePosition(frame,centerPosition);
        for (int x = 0; x < frame.length; x++) {
            for (int y = 0; y < frame[0].length; y++) {
                if (!markedAt(new Vector2Int(x,y))){
                    frame[x][y]=fog;
                }
            }
        }
    }
    private void searchVisiblePosition(TETile[][] frame,Vector2Int startPosition){
        //bfs
        visiblePositions.clear();
        visiblePositions.put(startPosition,0);
        Queue<Vector2Int> vqueue=new ArrayDeque<>();
        vqueue.add(startPosition);

        Vector2Int currentPos=null;
        int distanceFromCenter;
        Vector2Int adjacentPos=null;
        boolean obstacleLeft;
        boolean obstacleRight;
        boolean obstacleUp;
        boolean obstacleDown;

        while (!vqueue.isEmpty()){
           currentPos=vqueue.remove();
           distanceFromCenter=visiblePositions.get(currentPos);

           adjacentPos=Vector2Int.plus(currentPos,Vector2Int.UP);
           updatePosition(frame,vqueue,
                   adjacentPos,distanceFromCenter,INCREMENT_STRAIGHT);
           obstacleUp=isObstacles(frame,adjacentPos);

           adjacentPos=Vector2Int.plus(currentPos,Vector2Int.DOWN);
           updatePosition(frame,vqueue,
                   adjacentPos,distanceFromCenter,INCREMENT_STRAIGHT);
           obstacleDown=isObstacles(frame,adjacentPos);

           adjacentPos=Vector2Int.plus(currentPos,Vector2Int.RIGHT);
           updatePosition(frame,vqueue,
                   adjacentPos,distanceFromCenter,INCREMENT_STRAIGHT);
           obstacleRight=isObstacles(frame,adjacentPos);

           adjacentPos=Vector2Int.plus(currentPos,Vector2Int.LEFT);
           updatePosition(frame,vqueue,
                   adjacentPos,distanceFromCenter,INCREMENT_STRAIGHT);
           obstacleLeft=isObstacles(frame,adjacentPos);

            if (!obstacleLeft||!obstacleUp){
                adjacentPos=Vector2Int.plus(currentPos,Vector2Int.UP_LEFT);
                updatePosition(frame,vqueue,
                        adjacentPos,distanceFromCenter,INCREMENT_DIAGONAL);
            }
            if (!obstacleRight||!obstacleUp){
                adjacentPos=Vector2Int.plus(currentPos,Vector2Int.UP_RIGHT);
                updatePosition(frame,vqueue,
                        adjacentPos,distanceFromCenter,INCREMENT_DIAGONAL);
            }
            if (!obstacleLeft||!obstacleDown){
                adjacentPos=Vector2Int.plus(currentPos,Vector2Int.DOWN_LEFT);
                updatePosition(frame,vqueue,
                        adjacentPos,distanceFromCenter,INCREMENT_DIAGONAL);
            }
           if (!obstacleRight||!obstacleDown){
               adjacentPos=Vector2Int.plus(currentPos,Vector2Int.DOWN_RIGHT);
               updatePosition(frame,vqueue,
                       adjacentPos,distanceFromCenter,INCREMENT_DIAGONAL);
           }
        }

    }

    /**
     * update the position info, mark it if in radius,
     * store it for next iteration if not obstacles
     */
    private void updatePosition(TETile[][] frame,Queue<Vector2Int> queue,
                               Vector2Int position,int fromDistance,int distanceIncrement){
        int newDistance= fromDistance+distanceIncrement;
        if (markedAt(position)){
            int previousDistance=visiblePositions.get(position);
            if (newDistance<previousDistance){
                visiblePositions.put(position,newDistance);
            }
            return;
        }
        if (newDistance>radius){
            return;
        }
        visiblePositions.put(position,newDistance);

        if (!isObstacles(frame,position)){
           queue.add(position);
        }

    }
    private boolean isObstacles(TETile[][] frame,Vector2Int position){
        TETile tile=frame[position.x][position.y];
        for(TETile t : obstacles) {
            if (t.character()==tile.character()){
                return true;
            }
        }
        return false;
    }
    private boolean markedAt(Vector2Int position){
       return visiblePositions.containsKey(position);
    }
}
