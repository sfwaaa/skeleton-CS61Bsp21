package deque;

import java.util.Iterator;

/**
 * Deque based on resizable array
 * @param <T> the type of item you want to store in deque
 */
public class ArrayDeque<T> implements Iterable<T>,Deque<T> {

    /**
     * Default size when constructed
     */
    private static final int DEFAULT_SIZE=8;
    /**
     * The ratio of the minimum number of items to the max size
     */
    private static final float MIN_USAGE_FACTOR=0.25f;
    /**
     * This array is considered to be "small" when its
     * length is lower than this value.
     * Arbitrarily low usage factor is allowed in a small array
     */
    private static final int SMALL_ARRAY_LENGTH=15;
    /**
     * When actual usage factor is lower than MIN_USAGE_FACTOR,
     * array's length should be divided by this factor
     */
    private static final int RESIZING_FACTOR =2;

    private class ArrayDequeIterator implements Iterator<T>{
        private T[] items;
        /**
         * The index of the first valid element in items
         */
        private int first;
        /**
         * The index of the NEXT of
         * the last valid element in items
         */
        private int last;
        private boolean done;
        /**
         * construct an Iterator for ArrayDeque with the given parameter
         * @param items the items of the ArrayDeque
         * @param first the first of the ArrayDeque
         * @param last the last of the ArrayDeque
         */
        public ArrayDequeIterator(T[] items,int first,int last){
            this.items=items;
            this.first=first;
            this.last=last;
            this.done= first==last;
        }

        @Override
        public boolean hasNext() {
            return !done;
        }

        @Override
        public T next() {
            T returnedItem=items[first];
            first=(first+1)%items.length;
            if (first==last){
                done=true;
            }
            return returnedItem;
        }

    }

    private T[] items;
    /**
     * The index of the first valid element in items
     */
    private int first;

    /**
     * The index of the NEXT of
     * the last valid element in items
     */
    private int last;

    /**
     * Construct an empty deque
     */
    public ArrayDeque(){
        this.items=createItems(ArrayDeque.DEFAULT_SIZE);
        this.first=0;
        this.last=this.first;
    }

    @Override
    public Iterator<T> iterator() {
        return new ArrayDequeIterator(this.items,first,last);
    }


    /**
     * Returns the number of the data in the deque.
     * @return the number of the data in the deque
     */
    @Override
    public int size(){
        //To make this implementation more interesting,
        //we don't keep track of the actual size in a variable

        //this is equivalent to
        //"(last-first) mod items.length"
        if(last>=first){
            return last-first;
        }
        else{
            return last-first+items.length;
        }
    }

    /**
     * Returns true if deque is empty, false otherwise.
     * @return true if empty,false otherwise
     */
    @Override
    public boolean isEmpty(){
        return first==last;
    }

    /**
     * Gets the item at the given index,
     * where 0 is the front, 1 is the next item, and so forth.
     * If no such item exists, returns null.
     * @param index the index at which the item is
     * @return the item at the given index,null if no such item exists
     */
    @Override
    public T get(int index){
        if (index>=this.size()){
            return null;
        }
        int actualIndex=(first+index)%items.length;
        return this.items[actualIndex];
    }

    /**
     * Add an item of type T to the front of the deque.
     * Assume item should not be null.
     * @param item item to add
     */
    @Override
    public void addFirst(T item){
        if (isFull()){
            resize((this.items.length-1)*ArrayDeque.RESIZING_FACTOR);
        }

        this.first--;
        if (this.first<0){//equivalent to first-1 mod items.length
            this.first+=this.items.length;
        }
        this.items[first]=item;

    }

    /**
     * Adds an item of type T to the back of the deque.
     * @param item item to add
     */
    @Override
    public void addLast(T item){
        if (isFull()){
            resize((this.items.length-1)*ArrayDeque.RESIZING_FACTOR);
        }

        this.items[last]=item;
        this.last=(this.last+1) % this.items.length;
    }

    /**
     * Removes and returns the item at the front of the deque.
     * @return the removed item at the front of the deque,
     * If no such item exists, returns null
     */
    @Override
    public T removeFirst(){
        if (this.isEmpty()){
            return null;
        }
        T removedItem=this.items[first];
        this.items[first]=null;//avoid loitering

        this.first=(this.first+1)%this.items.length;

        if (isLowUsage()){
            resize((this.items.length-1)/ArrayDeque.RESIZING_FACTOR);
        }
        return removedItem;
    }

    /**
     * Removes and returns the item at the back of the deque.
     * @return the removed item at the back of the deque,
     * If no such item exists, returns null
     */
    @Override
    public T removeLast(){
        if (this.isEmpty()){
            return null;
        }
        int actualLast=last-1;
        if (actualLast<0){
            actualLast+=this.items.length;
        }
        T removedItem=this.items[actualLast];
        this.items[actualLast]=null;//avoid loitering

        this.last=actualLast;

        if (isLowUsage()){
            resize((this.items.length-1)/ArrayDeque.RESIZING_FACTOR);
        }
        return removedItem;
    }

    /**
     *  Prints the items in the deque from first to last,
     *  separated by a space.Once all the items have been printed,
     *  print out a new line.
     */
    @Override
    public void printDeque(){
        int i=first;
        if (i==last){
            System.out.println();
            return;
        }

        System.out.print(this.items[i]);

        i=(i+1)%this.items.length;
        while (i!=last){
            System.out.print(" "+this.items[i]);
            i=(i+1)%this.items.length;
        }

        System.out.println();
    }

    /**
     * Returns whether the parameter o is equal to the Deque
     * @param o  the reference object with which to compare.
     * @return true if o is equal to the Deque
     */
    @Override
    public boolean equals(Object o){
        if(o==null){
            return false;
        }
        if (o==this){
            return true;
        }
        if (o.getClass()!=this.getClass()){
            return false;
        }
        ArrayDeque<T> deque=(ArrayDeque<T>) o;
        if (this.size()!=deque.size()){
            return false;
        }
        int dequeSize=deque.size();

        for (int i=0;i<dequeSize;i++){
            if(!this.get(i).equals(deque.get(i))){
                return false;
            }
        }
        return true;
    }

    /**
     * Returns true if deque is full, false otherwise.
     * @return true if full,false otherwise
     */
    private boolean isFull(){
        return ((last+1)%items.length)==first;
    }

    /**
     * Allocate a new items with the given size and
     * copy elements from the old one to this.
     * And update first and last pointer.
     * Assume the new items with new size
     * can hold all items in the previous items
     * @param newSize the size of the new items
     */
    private void resize(int newSize){
        T[] newItems=createItems(newSize);
        int size=this.size();

        int previousItemsIndex=0;
        int previousLength=this.items.length;

        int i=0;
        for (;i<size;i++){
            previousItemsIndex =
                    (first+i)%previousLength;
            newItems[i]=this.items[previousItemsIndex];
        }

        this.first=0;
        this.last=i;
        this.items=newItems;
    }

    /**
     * tell whether the current usage factor is low
     * @return true if current usage factor is lower than MIN_USAGE_FACTOR,
     * false otherwise
     */
    private boolean isLowUsage(){
        //For smaller arrays,
        // the usage factor can be arbitrarily low
        if (this.items.length-1 <=ArrayDeque.SMALL_ARRAY_LENGTH){
            return false;
        }
        int actualItemsCount=this.size();
        int maxCount=this.items.length-1;
        float usageFactor=(float)actualItemsCount/(float)maxCount;
        return usageFactor<ArrayDeque.MIN_USAGE_FACTOR;
    }

    /**
     * Create a new empty items with size of "size + 1",
     * which leaves an empty element for last to point to
     * @param size the actual number of the items that items can store
     * @return a new empty array with proper size
     */
    private T[] createItems(int size){
        //to be frank,generic in java is weird...
        return (T[])new Object[size+1];
    }
}
