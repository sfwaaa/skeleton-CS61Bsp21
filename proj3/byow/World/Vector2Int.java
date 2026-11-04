package byow.World;

/**
 * The simple representation of the 2-dimensional vector with integer component
 */
public class Vector2Int {
    //unit direction vectors
    public static final Vector2Int UP =new Vector2Int(0,1);
    public static final Vector2Int DOWN =new Vector2Int(0,-1);
    public static final Vector2Int LEFT =new Vector2Int(-1,0);
    public static final Vector2Int RIGHT =new Vector2Int(1,0);

    public static final Vector2Int ZERO =new Vector2Int(0,0);

    public static Vector2Int plus(Vector2Int a,Vector2Int b){
        return new Vector2Int(a.x+b.x,a.y+b.y);
    }
    public static Vector2Int minus(Vector2Int a,Vector2Int b){
        return new Vector2Int(a.x-b.x,a.y-b.y);
    }
    public static Vector2Int minus(Vector2Int a){
        return new Vector2Int(-a.x, -a.y);
    }
    //scale the vector
    public static Vector2Int times(int d,Vector2Int a){
        return new Vector2Int(a.x*d, a.y*d);
    }

    //maybe times(a,b)...

    public int x;
    public int y;

    /**
     * Create a zero vector
     */
    public Vector2Int(){
        x=0;
        y=0;
    }
    public Vector2Int(int x,int y){
        this.x=x;
        this.y=y;
    }

    @Override
    public String toString() {
        return "(" + x + "," + y + ")";
    }
}
