package deque;

import java.util.Iterator;

/**
 * Deque based on linked list.
 * @param <T> the type of item you want to store in deque
 */
public class LinkedListDeque<T> implements Iterable<T>,Deque<T> {


    /**
     * naked linked list.
     * @param <Q> data type
     */
    private class Node<Q>{
        public Q item;
        public Node<Q> next;
        public Node<Q> previous;

        /**
         * construct an empty Node.
         */
        public Node() {
            this.next=null;
            this.previous=null;
        }

        /**
         * construct a Node with the given data.
         * @param item  the data to be stored in the node
         */
        public Node(Q item){
            this.next=null;
            this.previous=null;
            this.item = item;
        }
    }

    private class DequeIterator implements Iterator<T>{
        private Node<T> current;
        private Node<T> sentinel;
        /**
         * determine whether iteration reached the last node
         */
        private boolean done;
        /**
         * Construct an iterator with a LinkedListDeque
         * @param sentinel sentinel of a LinkedListDeque,shouldn't be null
         */
        public DequeIterator(Node<T> sentinel){
            this.sentinel=sentinel;
            this.current=sentinel.next;
            this.done= current==sentinel;
        }
        @Override
        public boolean hasNext() {
            return !done;//only determine having next
        }

        @Override
        public T next() {
            //return value and move in this method
            T returnedItem=current.item;
            current=current.next;
            if (current==sentinel){
                this.done=true;
            }
            return returnedItem;
        }
    }

    /**
     * The "first" node that contains invalid data
     * to make implementation easier.
     * in an empty deque,sentinel.next or previous points to itself.
     */
    private Node<T> sentinel;
    /**
     * The number of nodes with valid data in this deque.
     */
    private int size;

    /**
     * Construct an empty deque.
     */
    public LinkedListDeque(){
        this.sentinel=new Node<T>();
        this.sentinel.next=this.sentinel;
        this.sentinel.previous=this.sentinel;

        this.size=0;
    }

    @Override
    public Iterator<T> iterator() {
        return new DequeIterator(this.sentinel);
    }

    /**
     * Returns the number of the data in the deque.
     * @return the number of the data in the deque
     */
    @Override
    public int size(){
        return this.size;
    }

    /**
     * Returns true if deque is empty, false otherwise.
     * @return true if empty,false otherwise
     */
    @Override
    public boolean isEmpty(){
        return this.size <= 0;
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
        Node<T> current=sentinel.next;
        int currentIndex=0;
        while (current!=sentinel){
            if (currentIndex==index){
                return current.item;
            }
            current=current.next;
            currentIndex++;
        }
        return null;
    }

    /**
     * Gets the item at the given index,
     * where 0 is the front, 1 is the next item, and so forth.
     * If no such item exists, returns null.
     * @param index the index at which the item is
     * @return the item at the given index,null if no such item exists
     */
    public T getRecursive(int index){
       return getRecursive(sentinel.next,0,index);
    }
    private T getRecursive(Node<T> first,int currentIndex,int targetIndex){
        if (first==this.sentinel){
            return null;
        }
        if (currentIndex==targetIndex){
            return first.item;
        }
        return getRecursive(first.next,currentIndex+1,targetIndex);
    }
    /**
     * Add an item of type T to the front of the deque.
     * Assume item should not be null.
     * @param item item to add
     */
    @Override
    public void addFirst(T item){
        Node<T> previousFirst=sentinel.next;
        Node<T> newNode=new Node<>(item);

        sentinel.next=newNode;

        newNode.next=previousFirst;
        newNode.previous=sentinel;

        previousFirst.previous=newNode;

        this.size++;
    }

    /**
     * Adds an item of type T to the back of the deque.
     * @param item item to add
     */
    @Override
    public void addLast(T item){
        Node<T> previousLast =sentinel.previous;
        Node<T> newNode=new Node<>(item);

        sentinel.previous=newNode;

        newNode.next= sentinel;
        newNode.previous=previousLast;

        previousLast.next=newNode;

        this.size++;
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
        this.size--;

        Node<T> removedNode=this.sentinel.next;
        Node<T> newFirstNode=removedNode.next;

        sentinel.next=newFirstNode;
        newFirstNode.previous=sentinel;

        return removedNode.item;
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
        this.size--;

        Node<T> removedNode=this.sentinel.previous;
        Node<T> newLastNode=removedNode.previous;

        sentinel.previous=newLastNode;
        newLastNode.next=sentinel;

        return removedNode.item;
    }

    /**
     *  Prints the items in the deque from first to last,
     *  separated by a space.Once all the items have been printed,
     *  print out a new line.
     */
    @Override
    public void printDeque(){
        Node<T> current=sentinel.next;
        if (current!=sentinel){
            System.out.print(current.item);
        }
        else {
            System.out.println();
            return;
        }

        current=current.next;
        while (current!=sentinel){
            System.out.print(" "+current.item);
            current=current.next;
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

        /*obsolete implementation
        * This if statement can't tell whether o is LinkedListDeque<T>
        * due to type erasure.
        * if (!(o instanceof LinkedListDeque)){
        *   return false;
        *}
        * */
        if(o==null){
            return false;
        }
        if (o==this){
            return true;
        }
        if (o.getClass()!=this.getClass()){
            return false;
        }
        LinkedListDeque<T> deque=(LinkedListDeque<T>) o;
        if (this.size!=deque.size()){
            return false;
        }
        int dequeSize=deque.size();
        Node<T> currentNode=this.sentinel.next;
        for (int i=0;i<dequeSize;i++){
            if(!currentNode.item.equals(deque.get(i))){
                return false;
            }
            currentNode=currentNode.next;
        }
        return true;
    }

}
