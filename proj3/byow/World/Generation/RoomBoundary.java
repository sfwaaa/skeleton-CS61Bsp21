package byow.World.Generation;

import byow.World.*;

/**
 * The split result that represents the rect boundary(inclusive) of a room
 */
public class RoomBoundary {
    public Vector2Int bottomLeft;
    public int width;
    public int height;

    public RoomBoundary(){
        this.bottomLeft=Vector2Int.ZERO;
        this.width=0;
        this.height=0;
    }
    public RoomBoundary(Vector2Int bottomLeft,int width,int height){
        this.width=width;
        this.height=height;
        this.bottomLeft=new Vector2Int(bottomLeft.x,bottomLeft.y);
    }
    public int area(){
        return width*height;
    }
    /**
     * Divide the rect and return the divided other half
     * @param widthOffset the split index related to height
     */
    public RoomBoundary divideVertically(int widthOffset){
        RoomBoundary newBoundary=new RoomBoundary
                (this.bottomLeft,this.width,this.height);
        this.width=widthOffset;
        newBoundary.width-=widthOffset;
        newBoundary.bottomLeft.x+=widthOffset;
        return newBoundary;
    }
    /**
     * Divide the rect and return the divided other half
     * @param heightOffset the split index related to width,
     *                    e.g. split width 6 with param 3 leave 3 tiles in both new boundary
     */
    public RoomBoundary divideHorizontally(int heightOffset){
        RoomBoundary newBoundary=new RoomBoundary
                (this.bottomLeft,this.width,this.height);
        this.height=heightOffset;

        newBoundary.height-=heightOffset;
        newBoundary.bottomLeft.y+=heightOffset;
        return newBoundary;
    }

    /**
     * Return a copy of a shrunk boundary of this boundary
     * @param left left shrunk offset
     * @param right right shrunk offset
     * @param down down shrunk offset
     * @param up up shrunk offset
     * @return the shrunk boundary
     */
    public RoomBoundary shrink(int left,int right,int down,int up){
        RoomBoundary shrunk=new RoomBoundary();
        shrunk.bottomLeft=new Vector2Int
                (this.bottomLeft.x+left,this.bottomLeft.y+down);
        shrunk.height=this.height-down-up;
        shrunk.width=this.width-left-right;
        return shrunk;
    }
}
