package byow.World;

/**
 * The simple representation of the 2-dimensional vector with integer component
 */
public class Vector2Int {
    //direction vectors
    public static final Vector2Int UP =new Vector2Int(0,1);
    public static final Vector2Int DOWN =new Vector2Int(0,-1);
    public static final Vector2Int LEFT =new Vector2Int(-1,0);
    public static final Vector2Int RIGHT =new Vector2Int(1,0);

    public static final Vector2Int UP_RIGHT =new Vector2Int(1,1);
    public static final Vector2Int DOWN_RIGHT =new Vector2Int(1,-1);
    public static final Vector2Int UP_LEFT =new Vector2Int(-1,1);
    public static final Vector2Int DOWN_LEFT =new Vector2Int(-1,-1);

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
    public static boolean equal(Vector2Int v1,Vector2Int v2){
        return v1.x==v2.x && v1.y==v2.y;
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

    /**
     *
     * @return square Magnitude,may have int overflow
     */
    public int sqrMagnitude(){
        return x*x+y*y;
    }

    @Override
    public String toString() {
        return "(" + x + "," + y + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj==null){
            return false;
        }
        if (obj.getClass()!= Vector2Int.class){
            return false;
        }
        Vector2Int v=(Vector2Int) obj;
        return v.x==this.x&&v.y==this.y;
    }
}
