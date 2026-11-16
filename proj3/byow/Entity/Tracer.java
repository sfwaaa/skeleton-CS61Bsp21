package byow.Entity;

import byow.Entity.PathFinding.PathFinder;
import byow.World.Vector2Int;

/**
 * auto tracing other object
 */
public class Tracer {

    private final MovableObject tracer;
    private final MovableObject target;
    private final PathFinder pathFinder;
    private long moveIntervalInMs;
    private long previousTime;

    private int pathIndex;
    private Vector2Int[] path;
    private Vector2Int previousTargetPosition;

    public Tracer(MovableObject tracer, MovableObject target, PathFinder finder){
        this.tracer=tracer;
        this.target=target;
        this.pathFinder=finder;
        moveIntervalInMs=1000;//1sec
        previousTime=System.currentTimeMillis();
        previousTargetPosition=Vector2Int.DOWN_LEFT;
    }

    public void setMoveInterval(float intervalInSecond){
        if (intervalInSecond<=0){
            return;
        }
        this.moveIntervalInMs = Math.round(intervalInSecond*1000);
    }
    public boolean canTrace(){
        return deltaTimeSinceLastMove()>=this.moveIntervalInMs;
    }
    public void move(){
        if (Vector2Int.equal(previousTargetPosition,target.getCurrentPosition())){
           moveNext(path[pathIndex]);
           pathIndex++;
           return;
        }

        findPath();
        moveNext(path[pathIndex]);
        pathIndex++;
    }
    public Vector2Int[] getPath(){
        return this.path;
    }
    private void moveNext(Vector2Int targetPosition){
        tracer.move(Vector2Int.minus(targetPosition,tracer.getCurrentPosition()));
        this.previousTargetPosition=targetPosition;
        this.previousTime=System.currentTimeMillis();
    }
    private void findPath(){
        path=pathFinder.getPath(target.getWorld(),
                tracer.getCurrentPosition(),target.getCurrentPosition());
        pathIndex=1;
    }
    private long deltaTimeSinceLastMove(){
        return System.currentTimeMillis()-previousTime;
    }
}
