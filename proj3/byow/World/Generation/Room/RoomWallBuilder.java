package byow.World.Generation.Room;

import byow.TileEngine.TETile;
import byow.World.Vector2Int;
import byow.World.World;

/**
 * Building the room wall after connecting.
 * When encounter a nonempty tile,
 * check where the wall can be put around the tile
 * Although java isn't flexible to customize the building behaviors,
 * this project is simple enough to make sure almost all building behavior
 */
public class RoomWallBuilder {
    private final TETile wall;
    public RoomWallBuilder(TETile wall){
        this.wall=wall;
    }

    public void buildWall(World world,Room[] rooms){
        int worldWidth=world.getWidth();
        int worldHeight=world.getHeight();

        for (int x = 0; x < worldWidth; x++) {
            for (int y = 0; y <worldHeight; y++) {
                if (!world.existsTile(x,y) && existSurroundingFloorTile(world,x,y)){
                    world.put(new Vector2Int(x,y),wall);
                }
            }
        }
        for (Room room : rooms) {
            room.trim(this.wall);
        }
    }
    private boolean existSurroundingFloorTile(World world,int x,int y){
        //left
        if (existFloorTile(world,-1,y)){
            return true;
        }
        //right
        if (existFloorTile(world,x+1,y)){
            return true;
        }
        //up
        if (existFloorTile(world,x,y+1)){
            return true;
        }
        //down
        if (existFloorTile(world,x,y-1)){
            return true;
        }
        //up_left
        if (existFloorTile(world,x-1,y+1)){
            return true;
        }
        //up_right
        if (existFloorTile(world,x+1,y+1)){
            return true;
        }
        //down_left
        if (existFloorTile(world,x-1,y-1)){
            return true;
        }
        //down_right
        if (existFloorTile(world,x+1,y-1)){
            return true;
        }
        return false;
    }
    private boolean existFloorTile(World world, int x, int y){
        return world.existsTile(x,y) &&
                world.getTile(x,y).character()!=this.wall.character();
    }
}
