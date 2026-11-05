package byow.World.Generation;

import byow.Core.RandomUtils;
import byow.TileEngine.*;
import byow.World.*;
import byow.World.Generation.Room.Builder.RoomBuilder;

import java.util.ArrayList;
import java.util.Random;

/**
 * Generating the world via three steps:
 * splitting the world,building the rooms and connecting the rooms
 * This is based on BSP algorithm
 */
public class WorldGenerator {
    //hardcoded room size,I don't know where to put them properly
    /**
     * Only the boundaries whose area is between the area of ROOM_SIZE_DIVISIBLE_MIN and of ROOM_SIZE_MAX
     * can be split.
     * The x/y component of the ROOM_SIZE_DIVISIBLE_MIN should be > 2*ROOM_SIZE_MIN.x/.y
     */
    private static final Vector2Int ROOM_SIZE_DIVISIBLE_MIN =new Vector2Int(9,9);
    private static final Vector2Int ROOM_SIZE_MIN=new Vector2Int(4,4);
    /**
     * max room size ,force split when the boundary's width/height exceed ROOM_SIZE_MAX.x/.y
     */
    private static final Vector2Int ROOM_SIZE_MAX=new Vector2Int(12,12);


    /**
     * The split bias ±b in splitting the room
     */
    private static final int SPLIT_BIAS=5;

    private RoomBuilder roomBuilder;
    private RoomConnector roomConnector;

    public WorldGenerator(RoomBuilder builder,RoomConnector connector){

        this.roomBuilder=builder;
        this.roomConnector=connector;
    }

    public World generateWorld(Vector2Int worldSize,long seed){
        World world=new World(worldSize, Tileset.NOTHING);
        Random random=new Random(seed);
        //splitting
        ArrayList<RoomBoundary> boundaries=new ArrayList<>();
        RoomBoundary worldBoundary=new RoomBoundary
                (Vector2Int.ZERO,world.getWidth(),world.getHeight());
        this.split(random,SPLIT_BIAS,worldBoundary,boundaries);
        //building
        for(RoomBoundary boundary : boundaries){
            System.out.println(boundary.width+"x"+boundary.height+"rect at "+boundary.bottomLeft.toString());
            this.roomBuilder.build(world,boundary,random);
        }
        //connecting
        return world;
    }
    /**
     * Split the world into different rectangles(RoomBoundary,always be a rectangle)
     * @param bias the perturbation in splitting rooms,must be POSITIVE
     * @param boundary the rectangle to split
     * @param split the output Result of the splitting,
     *                   instantiate it before calling the method
     */
    private void split(Random random,int bias, RoomBoundary boundary, ArrayList<RoomBoundary> split){
        boolean exceedingMaxHeight=ROOM_SIZE_MAX.y<boundary.height;
        boolean exceedingMaxWidth= ROOM_SIZE_MAX.x<boundary.width;

        boolean canSplit= //both width&height exceed divisible room size  OR width/height exceed max room size
                ((ROOM_SIZE_DIVISIBLE_MIN.x)<=boundary.width && (ROOM_SIZE_DIVISIBLE_MIN.y)<=boundary.height)||
                (exceedingMaxHeight||exceedingMaxWidth);
        if (!canSplit){
            split.add(boundary);
            return;
        }
        //exceeding the max size
        /* previous
        boolean forceSplit= (ROOM_SIZE_MAX.x)<boundary.width ||
                            (ROOM_SIZE_MAX.y)<boundary.height;
        if (forceSplit){
            RoomBoundary[] boundaries=splitBoundary(boundary,random,bias);
            split(random,bias,boundaries[0], split);
            split(random,bias,boundaries[1], split);
            return;
        }
        */

        if (exceedingMaxHeight&&exceedingMaxWidth){//force random split
            RoomBoundary[] boundaries=splitBoundary(boundary,random,bias);
            split(random,bias,boundaries[0], split);
            split(random,bias,boundaries[1], split);
            return;
        }
        else if(exceedingMaxHeight){//only exceedingMaxHeight
            RoomBoundary[] boundaries=splitBoundaryHorizontal(boundary,random,bias);
            split(random,bias,boundaries[0], split);
            split(random,bias,boundaries[1], split);
            return;
        }
        else if(exceedingMaxWidth){//only exceedingMaxWidth
            RoomBoundary[] boundaries=splitBoundaryVertical(boundary,random,bias);
            split(random,bias,boundaries[0], split);
            split(random,bias,boundaries[1], split);
            return;
        }

        //in a moderate size,the larger the boundary is,more possible to be split
        int minDivisibleRoomArea=ROOM_SIZE_DIVISIBLE_MIN.x*ROOM_SIZE_DIVISIBLE_MIN.y;
        int maxRoomArea=ROOM_SIZE_MAX.x*ROOM_SIZE_MAX.y;
        boolean maySplit=RandomUtils.
                uniform(random,minDivisibleRoomArea,maxRoomArea)<=boundary.area();

        if (maySplit){
            RoomBoundary[] boundaries=splitBoundary(boundary,random,bias);
            split(random,bias,boundaries[0], split);
            split(random,bias,boundaries[1], split);
        }
        else {
            split.add(boundary);
        }


    }

    //split the world either horizontally or vertically,returns the split halves
    private RoomBoundary[] splitBoundary(RoomBoundary boundary,Random random,int bias){
        //tend to split the longer
        boolean splitHorizontal=RandomUtils.
                uniform(random,-boundary.width,boundary.height)>=0;
        if (splitHorizontal){
            return splitBoundaryHorizontal(boundary,random,bias);
        }
        else {
            return splitBoundaryVertical(boundary,random,bias);
        }
    }
    private RoomBoundary[] splitBoundaryVertical(RoomBoundary boundary,Random random,int bias){
//        int randomOffset=RandomUtils.uniform(random,-bias,bias+1);
//        int splitOffset= (boundary.width/2) +randomOffset;
//        splitOffset=Math.max(ROOM_SIZE_DIVISIBLE_MIN.x,splitOffset);
        //clamp
        int offsetMin=Math.max( ROOM_SIZE_MIN.x, (boundary.width/2)-bias );
        int offsetMax=Math.min( (boundary.width/2)+bias , boundary.width-ROOM_SIZE_MIN.x );

        int splitOffset=RandomUtils.uniform(random,offsetMin,offsetMax);

        RoomBoundary newBoundary=boundary.divideVertically(splitOffset);
        return new RoomBoundary[]{boundary,newBoundary};
    }
    private RoomBoundary[] splitBoundaryHorizontal(RoomBoundary boundary, Random random, int bias){
//        int randomOffset=RandomUtils.uniform(random,-bias,bias+1);
//        int splitOffset= (boundary.height/2) +randomOffset;
//        splitOffset=Math.max(ROOM_SIZE_DIVISIBLE_MIN.y,splitOffset);
        //clamp,leave space for min room size
        int offsetMin=Math.max( ROOM_SIZE_MIN.y, (boundary.height/2)-bias );
        int offsetMax=Math.min( (boundary.height/2)+bias , boundary.height-ROOM_SIZE_MIN.y );
        int splitOffset=RandomUtils.uniform(random,offsetMin,offsetMax);

        RoomBoundary newBoundary=boundary.divideHorizontally(splitOffset);
        return new RoomBoundary[]{boundary,newBoundary};
    }

    /*
    first implementation of split()
    //simple recursive implementation
        boolean canSplit=
                (ROOM_SIZE_MIN.x*2)<=boundary.width &&
                (ROOM_SIZE_MIN.y*2)<=boundary.height;
        if (!canSplit){
            split.add(boundary);
            return;
        }
        boolean canSplitHorizontal= RandomUtils.uniform(random,2)==1;
        boolean canSplitVertical=RandomUtils.uniform(random,2)==1;
        boolean forceSplit=
                (ROOM_SIZE_MAX.x)<boundary.width ||
                (ROOM_SIZE_MAX.y)<boundary.height;

        if (forceSplit||(canSplitHorizontal&&canSplitVertical)){
            //split the larger boundary
            if (boundary.height>=boundary.width){
                RoomBoundary[] boundaries=splitBoundaryHorizontal(boundary,random,bias);
                split(random,bias,boundaries[0], split);
                split(random,bias,boundaries[1], split);
            }
            else {
                RoomBoundary[] boundaries=splitBoundaryVertical(boundary,random,bias);
                split(random,bias,boundaries[0], split);
                split(random,bias,boundaries[1], split);
            }
            return;
        }
        if (canSplitHorizontal){
//            boolean canSplitHorizontal=boundary.height>=(ROOM_SIZE_MIN.y*2);
//            if (!canSplitHorizontal){
//                return;
//            }
//            int splitOffset= (boundary.height/2) +
//                    RandomUtils.uniform(random,-bias,bias+1);
//            splitOffset=Math.min(ROOM_SIZE_MIN.y,splitOffset);
//            RoomBoundary newBoundary=boundary.divideHorizontally(splitOffset);
            RoomBoundary[] boundaries=splitBoundaryHorizontal(boundary,random,bias);
            split(random,bias,boundaries[0], split);
            split(random,bias,boundaries[1], split);
            return;
        }

        if (canSplitVertical){
//            int splitOffset= (boundary.width/2) +
//                    RandomUtils.uniform(random,-bias,bias+1);
//            splitOffset=Math.min(ROOM_SIZE_MIN.x,splitOffset);
//            RoomBoundary newBoundary=boundary.divideVertically(splitOffset);
            RoomBoundary[] boundaries=splitBoundaryVertical(boundary,random,bias);
            split(random,bias,boundaries[0], split);
            split(random,bias,boundaries[1], split);
            return;
        }

    */
}
