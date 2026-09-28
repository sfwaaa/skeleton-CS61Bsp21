package hashmap;

import java.security.Key;
import java.util.*;

/**
 *  A hash table-backed Map implementation. Provides amortized constant time
 *  access to elements via get(), remove(), and put() in the best case.
 *
 *  Assumes null keys will never be inserted, and does not resize down upon remove().
 *  @author sfwaaa
 */
public class MyHashMap<K, V> implements Map61B<K, V> {
    /**
     * Protected helper class to store key/value pairs
     * The protected qualifier allows subclass access
     */
    protected class Node {
        K key;
        V value;

        Node(K k, V v) {
            key = k;
            value = v;
        }
    }

    private static final int DEFAULT_SIZE=16;
    private static final double DEFAULT_LOAD_FACTOR_MAX=0.75;
    private static final int RESIZE_FACTOR=2;


    private final double loadFactorMax;
    /**
     * THe number of the key-value pairs in this hash map
     */
    private int size;
    private HashSet<K> keySet;
    /* Instance Variables */
    private Collection<Node>[] buckets;


    /** Constructors */
    public MyHashMap() {
       this(DEFAULT_SIZE,DEFAULT_LOAD_FACTOR_MAX);
    }

    public MyHashMap(int initialSize) {
        this(initialSize,DEFAULT_LOAD_FACTOR_MAX);
    }

    /**
     * MyHashMap constructor that creates a backing array of initialSize.
     * The load factor (# items / # buckets) should always be <= loadFactor
     *
     * @param initialSize initial size of backing array
     * @param maxLoad maximum load factor
     */
    public MyHashMap(int initialSize, double maxLoad) {
        this.size=0;
        this.loadFactorMax=maxLoad;
        this.buckets=createTable(initialSize);
        this.keySet=new HashSet<>();
    }

    /**
     * Returns a new node to be placed in a hash table bucket
     */
    private Node createNode(K key, V value) {
        return new Node(key,value);
    }//use if you like

    /**
     * Returns a data structure to be a hash table bucket
     *
     * The only requirements of a hash table bucket are that we can:
     *  1. Insert items (`add` method)
     *  2. Remove items (`remove` method)
     *  3. Iterate through items (`iterator` method)
     *
     * Each of these methods is supported by java.util.Collection,
     * Most data structures in Java inherit from Collection, so we
     * can use almost any data structure as our buckets.
     *
     * Override this method to use different data structures as
     * the underlying bucket type
     *
     * BE SURE TO CALL THIS FACTORY METHOD INSTEAD OF CREATING YOUR
     * OWN BUCKET DATA STRUCTURES WITH THE NEW OPERATOR!
     */
    protected Collection<Node> createBucket() {
        return new LinkedList<Node>();
    }//To be frank,this should be an abstract method to postpone implementation to derived classes

    /**
     * Returns a table to back our hash table. As per the comment
     * above, this table can be an array of Collection objects
     *
     * BE SURE TO CALL THIS FACTORY METHOD WHEN CREATING A TABLE SO
     * THAT ALL BUCKET TYPES ARE OF JAVA.UTIL.COLLECTION
     *
     * @param tableSize the size of the table to create
     */
    private Collection<Node>[] createTable(int tableSize) {
        Collection<Node>[] buckets=new Collection[tableSize];
        for (int i = 0; i < buckets.length; i++) {
            buckets[i]=createBucket();
        }
        return buckets;
    }

    @Override
    public void clear() {
        this.size=0;
        //well...this is absolutely O(n)...
        for(Collection<Node> collection : this.buckets){
            collection.clear();
        }
        this.keySet=new HashSet<>();
    }

    @Override
    public boolean containsKey(K key) {
        int index=this.getValidIndex(key);
        Collection<Node> bucket=this.buckets[index];
        for(Node node : bucket){
            if (node.key.equals(key)){
                return true;
            }
        }
        return false;
    }
    /**
     * Returns the value to which the specified key is mapped, or null if this
     * map contains no mapping for the key.
     */
    @Override
    public V get(K key) {
        int index=this.getValidIndex(key);
        Collection<Node> bucket=this.buckets[index];
        for(Node node : bucket){
            if (node.key.equals(key)){
                return node.value;
            }
        }
        return null;
    }

    @Override
    public int size() {
        return this.size;
    }
    /**
     * Associates the specified value with the specified key in this map.
     * If the map previously contained a mapping for the key,
     * the old value is replaced.
     */
    @Override
    public void put(K key, V value) {
        int index=this.getValidIndex(key);
        Collection<Node> bucket=this.buckets[index];
        for(Node node : bucket){
            if (node.key.equals(key)){
                node.value=value;
                return;
            }
        }

        bucket.add(createNode(key,value));
        this.keySet.add(key);
        this.size++;
        if (this.getLoadFactor()>=this.loadFactorMax){
            resize();
        }
    }

    @Override
    public Set<K> keySet() {
        return this.keySet;
    }

    /**
     * Removes the mapping for the specified key from this map if present.
     */
    @Override
    public V remove(K key) {
        int index=this.getValidIndex(key);
        Collection<Node> bucket=this.buckets[index];
        V removedVal=null;
        for(Node node : bucket){
            if (node.key.equals(key)){
                removedVal=node.value;
                bucket.remove(node);
                return removedVal;
            }
        }
        return null;
    }

    /**
     * Removes the entry for the specified key only if it is currently mapped to
     * the specified value.
     */
    @Override
    public V remove(K key, V value) {
        int index=this.getValidIndex(key);
        Collection<Node> bucket=this.buckets[index];
        V removedVal=null;
        for(Node node : bucket){
            if (node.key.equals(key)&&node.value.equals(value)){
                removedVal=node.value;
                bucket.remove(node);
                return removedVal;
            }
        }
        return null;
    }

    @Override
    public Iterator<K> iterator() {
        return keySet.iterator();
    }

    /**
     * return x mod y which behaves mathematically
     */
    private int mod(int x,int y){
        return Math.floorMod(x,y);
    }
    private double getLoadFactor(){
        return (double) this.size /(double)this.buckets.length;
    }
    private int getValidIndex(K key){
        int hash=key.hashCode();
        return mod(hash,this.buckets.length);
    }
    private int getValidIndex(K key,int bucketsLength){
        int hash=key.hashCode();
        return mod(hash,bucketsLength);
    }
    private void resize(){
        Collection<Node>[] previousBuckets=this.buckets;
        Collection<Node>[] newBuckets=
                createTable(this.buckets.length*RESIZE_FACTOR);

        int validIndex=0;
        for(Collection<Node> buc : previousBuckets){
            for (Node node : buc){
                validIndex = getValidIndex(node.key,newBuckets.length);
                newBuckets[validIndex].add(node);
            }
        }
        this.buckets=newBuckets;
    }
}
