package byow.World.Generation.Room.Builder;

import byow.Core.RandomUtils;
import byow.World.Generation.Room.Room;
import byow.World.Generation.RoomBoundary;
import byow.World.Vector2Int;
import byow.World.World;

import java.util.Random;

public class RandomRoomBuilder extends RoomBuilder{
    private RoomBuilder[] builders;

    /**
     *
     * @param builders should at least 1 element
     */
    public RandomRoomBuilder(RoomBuilder[] builders){
        this.builders=sortBuilder(builders);
    }
    @Override
    public Room build(World world, RoomBoundary boundary, Random random) {
        Vector2Int minRequiredSize=this.minRequiredSize();
        if (boundary.width<minRequiredSize.x||boundary.height<minRequiredSize.y){
            return null;
        }
        int builderIndex= RandomUtils.uniform(random,builders.length);
        if (builderIndex==0){
            return builders[builderIndex].build(world,boundary,random);
        }
        Room room=null;
        while (builderIndex>0){
            room=builders[builderIndex].build(world,boundary,random);
            if (room!=null){
                return room;
            }
            builderIndex= RandomUtils.uniform(random,builderIndex);
        }
        if (room!=null){
            return room;
        }
        else {
            return builders[0].build(world,boundary,random);
        }
    }

    @Override
    public Vector2Int minRequiredSize() {
        return builders[0].minRequiredSize();
    }

    //sort builder by their minRequireSize(),
    //return a shallow copy of the builders[]
    private RoomBuilder[] sortBuilder(RoomBuilder[] builders){
        RoomBuilder[] sortedBuilders=new RoomBuilder[builders.length];

        RoomBuilder swap=null;
        //shallow copy and insertion sort by minRequiredSize()
        for (int currentBuilderIndex = 0; currentBuilderIndex < builders.length; currentBuilderIndex++) {
            sortedBuilders[currentBuilderIndex]=builders[currentBuilderIndex];
            for (int backIndex = currentBuilderIndex; backIndex>0; backIndex--) {
                if (sortedBuilders[backIndex].minRequiredSize().sqrMagnitude()<
                        sortedBuilders[backIndex-1].minRequiredSize().sqrMagnitude())
                {
                    swap=sortedBuilders[backIndex];
                    sortedBuilders[backIndex]=sortedBuilders[backIndex-1];
                    sortedBuilders[backIndex-1]=swap;
                }
                else {
                    break;
                }
            }
        }
        return sortedBuilders;
    }

}
