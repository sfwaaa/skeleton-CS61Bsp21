package byow.Entity.PathFinding;

import byow.TileEngine.TETile;
import byow.World.Vector2Int;
import byow.World.World;

import java.util.ArrayList;
import java.util.Comparator;

//simple A* implementation
public class AStar implements PathFinder {

    /**
     * representing a single searching unit
     */
    private class Node{
        /**
         * node that can go directly to this
         */
        public Node parent;
        /**
         * current position of this node
         */
        public final Vector2Int position;
        /**
         * Shortest distance from src(value g(n))
         */
        public int distanceFromSrc;
        /**
         * the estimated distance from the node to src(value h(n))
         */
        public int heuristic;
        public Node(Node parent,Vector2Int position,int distanceFromSrc,Vector2Int dst){
            this.parent=parent;
            this.position=position;
            this.distanceFromSrc=distanceFromSrc;
            this.heuristic=heuristic(position,dst);
        }
        public Node(Vector2Int position){
            this.position=position;
        }

        /**
         * @return the cost to choose this node in open list (value f(n))
         */
        public int getTotalCost(){
            return distanceFromSrc+heuristic;
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
    }
    private class NodeComparator implements Comparator<Node> {
        @Override
        public int compare(Node o1, Node o2) {
            return Integer.compare
                    (o1.getTotalCost(),o2.getTotalCost());
        }
    }

    private final NodeComparator nodeCmp=new NodeComparator();

    /**
     * here moving between two tiles has equal cost.So just hardcode it.
     * Note : moving diagonally is NOT allowed,
     * so we don't care about the cost of it.
     * (if you allow moving diagonally,should maybe set another cost for this kind of moving,
     * e.g. MOVING_DIAGONAL_COST=14=10*\sqrt{2})
     */
    private static final int MOVING_COST=10;
    /**
     * heuristic function used by this A*,estimating the distance between two position.
     * here is Manhattan distance
     * @return the Manhattan distance between two position
     */
    private static int heuristic(Vector2Int from,Vector2Int to){
        int xTiles=Math.abs(from.x-to.x);
        int yTiles=Math.abs(from.y-to.y);
        //here the estimated cost==MOVING_COST
        return MOVING_COST*(xTiles+yTiles);
    }

    /**
     * this is a simple implementation
     * so the cost of going through these walkable tiles is the same
     */
    private final TETile[] walkableTiles;
    /**
     * the list of nodes waiting for being chosen.
     * here we simply maintained a sorted list
     * with the first having the smallest cost
     */
    private ArrayList<Node> openList;
    /**
     * the list of nodes that have been chosen
     */
    private ArrayList<Node> closedList;
    private Node dstNode;

    public AStar(TETile[] walkableTiles){
        this.walkableTiles=walkableTiles;
    }


    @Override
    public Vector2Int[] getPath(World world, Vector2Int src, Vector2Int dst) {
        openList=new ArrayList<>();
        closedList=new ArrayList<>();
        openList.add(new Node(null,src,0,dst));
        dstNode=null;
        if (!find(world,dst)){
            return null;
        }
        return calculatePath();
    }
    private boolean find(World world,Vector2Int dst){
        Node current=null;
        Vector2Int adjacentPosition;
        while (dstNode==null&&!openList.isEmpty()){
            current=openList.remove(0);
            closedList.add(current);
            //check 4 adjacent tiles
            adjacentPosition=Vector2Int.plus
                    (current.position,Vector2Int.LEFT);
            updatePosition(world,adjacentPosition,current,dst);
            adjacentPosition=Vector2Int.plus
                    (current.position,Vector2Int.UP);
            updatePosition(world,adjacentPosition,current,dst);
            adjacentPosition=Vector2Int.plus
                    (current.position,Vector2Int.DOWN);
            updatePosition(world,adjacentPosition,current,dst);
            adjacentPosition=Vector2Int.plus
                    (current.position,Vector2Int.RIGHT);
            updatePosition(world,adjacentPosition,current,dst);
        }

        return dstNode!=null;
    }

    /**
     * add the node into open list and sort it.
     * (had better use a binary heap to manage open list to get better performance)
     */
    private void addToOpenList(Node node){
        openList.add(node);
        openList.sort(nodeCmp);
    }
    private boolean inClosedList(Vector2Int position){
        return closedList.contains(new Node(position));
    }
    //find the node corresponding the given position
    private Node getNodeInOpenList(Vector2Int position){
       int i=openList.indexOf(new Node(position));
       if (i<0){
           return null;
       }
       else {
           return openList.get(i);
       }
    }
    private boolean walkableAt(World world,Vector2Int position){
        if (!world.existsTile(position)){
            return false;
        }
        TETile tile=world.getTile(position);
        for (int i = 0; i < walkableTiles.length; i++) {
            if (tile.character()==walkableTiles[i].character()){
                return true;
            }
        }
        return false;
    }
    //update the info of the given position
    private void updatePosition(World world,Vector2Int position,Node parent,Vector2Int dst){
        if (Vector2Int.equal(position,dst)){
            dstNode=new Node(parent,dst,
                    parent.distanceFromSrc+MOVING_COST,dst);
        }
        if (inClosedList(position)||!walkableAt(world,position)){
            return;
        }
        Node node=getNodeInOpenList(position);
        if (node==null){
            addToOpenList(new Node(parent,position,
                    parent.distanceFromSrc+MOVING_COST,dst));
            return;
        }
        if(parent.distanceFromSrc+MOVING_COST<node.distanceFromSrc){
            node.distanceFromSrc=parent.distanceFromSrc+MOVING_COST;
            node.parent=parent;
            openList.sort(nodeCmp);
        }
    }
    private Vector2Int[] calculatePath(){
        ArrayList<Vector2Int> path=new ArrayList<>();
        Node current=dstNode;
        while (current!=null){
            path.add(0,current.position);
            current=current.parent;
        }
        return path.toArray(new Vector2Int[0]);
    }
}
