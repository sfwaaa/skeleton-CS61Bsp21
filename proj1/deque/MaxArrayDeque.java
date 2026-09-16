package deque;
import java.util.Comparator;
import java.util.Iterator;


public class MaxArrayDeque<T> extends ArrayDeque<T>{
    /**
     * the default comparator that max() use
     */
    private Comparator<T> defaultComparator;
    public MaxArrayDeque(Comparator<T> comparator){
        super();//call the constructor of the ArrayDeque
        this.defaultComparator=comparator;
    }

    /**
     * returns the maximum element in the deque as governed by
     * the previously given Comparator.
     * If the MaxArrayDeque is empty, return null.
     * @return the maximum element in the deque
     */
    public T max(){
        return this.max(defaultComparator);
    }

    /**
     * returns the maximum element in the deque as governed by
     * the parameter.
     * If the MaxArrayDeque is empty, return null.
     * @param comparator the comparison function
     * @return the maximum element in the deque
     */
    public T max(Comparator<T> comparator){
        if (comparator==null){
            return null;
        }
        T max=null;
        T current=null;
        Iterator<T> iterator=iterator();
        if (iterator.hasNext()){
            max=iterator.next();
        }
        else {
            return max;
        }
        while (iterator.hasNext()){
            current=iterator.next();
            if (comparator.compare(max,current)<0){
                max=current;
            }
        }
        return max;
    }


}
