package bstmap;

import java.lang.reflect.Array;
import java.util.Iterator;
import java.util.Set;

/**
 * Map(Dictionary) based on bst
 * @param <K>
 * @param <V>
 */
public class BSTMap<K extends Comparable<K>,V> implements Map61B<K,V>{
    /**
     * Naked bst root which stores the key and corresponding value.
     * BASE PRINCIPLE : DON'T CHECK entry.left/right==null
     * WHILE IMPLEMENTING METHOD EXCEPT DELETION()
     * @param <K> Key type
     * @param <V> Value type
     */
    private class Entry<K extends Comparable<K>,V>{
        /**
         * the left entry with less key
         */
        public Entry<K,V> left;
        /**
         * the right entry with larger key
         */
        public Entry<K,V> right;
        //to save place,parent pointer is not set.

        public K key;
        public V value;

        public Entry(K key,V value){
            this.key=key;
            this.value=value;
            this.left=null;
            this.right=null;
        }

    }

    private class BSTMapIterator<K extends Comparable<K>> implements Iterator<K> {
        private Entry<K,V>[] entry;//inside maintains a queue for all entry
        private int first;
        private int last;

        /**
         *
         * @param root the root of the bst
         * @param sizeOfTree the size of the bst
         */
        public BSTMapIterator(Entry<K,V> root,int sizeOfTree){
            entry=(Entry<K, V>[]) new Object[(sizeOfTree/2)+1+1];
            entry[0]=root;
            first=0;
            last=1;
            if (root==null){
                last=0;
            }
        }
        @Override
        public boolean hasNext() {
            return first!=last;
        }

        @Override
        public K next() {
            Entry<K,V> result=entry[first];
            first=(first+1)%entry.length;
            if (result.left!=null){
                entry[last]=result.left;
                last=(last+1)%entry.length;
            }
            if (result.right!=null){
                entry[last]=result.right;
                last=(last+1)%entry.length;
            }
            return result.key;
        }
    }


    /**
     * The number of the entry in bst
     */
    private int size;

    public BSTMap(){
        size=0;
        root=null;
    }
    /**
     * The root of the bst
     */
    private Entry<K,V> root;
    /** Removes all of the mappings from this map. */
    @Override
    public void clear() {
        this.size=0;
        this.root=null;
    }

    /** Returns true if this map contains a mapping for the specified key. */
    @Override
    public boolean containsKey(K key) {
        return find(root,key)!=null;
    }

    /** Associates the specified value with the specified key in this map. */
    @Override
    public V get(K key) {
        Entry<K,V> result=find(root,key);
        if (result==null){
            return null;
        }
        else {
            return result.value;
        }
    }
    /**Returns the number of key-value mappings in this map. */
    @Override
    public int size() {
        return this.size;
    }
    /**Associates the specified value with the specified key in this map. */
    @Override
    public void put(K key, V value) {
        this.root= setEntry(root,key,value);
    }

    /* Returns a Set view of the keys contained in this map.*/
    @Override
    public Set<K> keySet() {
        return getPreorderKeySet(root);
    }

    /** Removes the mapping for the specified key from this map if present. */
    @Override
    public V remove(K key) {
        Entry<K,V>[] result=delete(root, key);
        if (result[0]==null){
            return null;
        }
        root=result[1];
        size--;
        return result[0].value;
    }
    /** Removes the entry for the specified key
     * only if it is currently mapped to the specified value.*/
    @Override
    public V remove(K key, V value) {
        Entry<K,V>[] result=delete(root, key,value);
        if (result[0]==null){
            return null;
        }
        root=result[1];
        size--;
        return result[0].value;
    }

    @Override
    public Iterator<K> iterator() {
        return new BSTMapIterator<>(this.root,this.size);
    }

    /**
     * Internal recursively put
     * @param current the entry to visit
     * @param key key to construct a new entry
     * @param value value to construct a new entry
     * @return returns parameter "current" if current isn't null,
     * otherwise returns a new entry with the given key and value
     */
    private Entry<K,V> setEntry(Entry<K,V> current, K key, V value){
        //if (reach null),this is where we need to put the new entry
        if (current==null){
            this.size++;
            return new Entry<>(key,value);
        }
        //every entry repeat its left/right field
        //and then move into the next child
        int comparison= current.key.compareTo(key);
        if (comparison==0){
            current.value=value;
            return current;
        }
        else if (comparison<0){
            current.left= setEntry(current.left,key,value);
        }
        else {
            current.right= setEntry(current.right,key,value);
        }
        return current;
    }

    /**
     * find the entry with given key and return it.
     * @param current the root of the current bst
     * @param key the key of the entry
     * @return the entry with given key,null if not found
     */
    private Entry<K,V> find(Entry<K,V> current,K key){
        if (current==null){
            return null;
        }

        int comparison= current.key.compareTo(key);
        if (comparison==0){
            return current;
        }
        else if (comparison<0){
            return find(current.left,key);
        }
        else {
            return find(current.right,key);
        }

    }

    /**
     * Delete the entry with the given key
     * @param current root of the current bst
     * @param key the key of the entry to be deleted
     * @return the deleted entry(in [0],null if entry not found)
     * and the proper "current" after deletion(in [1])
     */
    private Entry<K,V>[] delete(Entry<K,V> current,K key){
        if (current==null){
            return (Entry<K, V>[]) new Object[]{null,null};
        }
        Entry<K, V>[] result=null;
        int comparison= current.key.compareTo(key);
        //compare and move to the correct subtree if entry not found
        if (comparison<0){
            result= delete(current.left,key);
            current.left=result[1];
            return  result;
        }
        else if (comparison>0) {
            result= delete(current.right,key);
            current.right=result[1];
            return result;
        }

        //if found, delete current Item and check if it has subtrees
        if (current.left==null){//leaf or single right subtree
            result = (Entry<K, V>[]) new Object[2];
            result[0]=current;
            result[1]=current.right;
            return result;
        }
        if (current.right==null){//single left subtree
            result = (Entry<K, V>[]) new Object[2];
            result[0]=current;
            result[1]=current.left;
            return result;
        }
        //two subtrees, replace current with its predecessor(chosen here) or successor
        result = deleteMax(current.left);
        Entry<K,V> predecessor=result[0];
        predecessor.left=result[1];

        result[0]=current;
        result[1]=predecessor;
        return result;
    }

    /**
     * Delete the entry with the given key and given value
     *(I don't want duplicate code ,
     * but since it's just a lab with on successive maintaining , I do it for laziness.)
     * @param current root of the current bst
     * @param key the key of the entry to be deleted
     * @param value the value of the entry to be deleted
     * @return the deleted entry(in [0],null if entry not found)
     * and the proper "current" after deletion(in [1])
     */
    private Entry<K,V>[] delete(Entry<K,V> current,K key,V value){
        if (current==null){
            return (Entry<K, V>[]) new Object[2];
        }
        Entry<K, V>[] result=null;
        int comparison= current.key.compareTo(key);
        //compare and move to the correct subtree if entry not found
        if (comparison<0){
            result= delete(current.left,key);
            current.left=result[1];
            return  result;
        }
        else if (comparison>0) {
            result= delete(current.right,key);
            current.right=result[1];
            return result;
        }
        if (current.value!=value)
        {
            return (Entry<K, V>[]) new Object[]{null,current};
        }
        //if found, delete current Item and check if it has subtrees
        if (current.left==null){//leaf or single right subtree
            result = (Entry<K, V>[]) new Object[]{current,current.right};
            return result;
        }
        if (current.right==null){//single left subtree
            result = (Entry<K, V>[]) new Object[]{current,current.left};
            return result;
        }
        //two subtrees, replace current with its predecessor(chosen here) or successor
        result = deleteMax(current.left);
        Entry<K,V> predecessor=result[0];
        predecessor.left=result[1];

        result[0]=current;
        result[1]=predecessor;
        return result;
    }


    /**
     * Delete the maximum entry in the bst.
     * @param current root of the current bst
     * @return the deleted entry(in [0],null if entry not found)
     * and the proper "current" after deletion(in [1])
     */
    private Entry<K,V>[] deleteMax(Entry<K,V> current){
        if (current==null){
            return (Entry<K, V>[]) new Object[]{null,null};
        }
        Entry<K, V>[] result=deleteMax(current.right);
        current.right=result[1];
        if (result[0]==null){
            result[0]=current;
            result[1]=current.left;
        }
        else {
            result[1]=current;
        }
        return result;
    }

    /**
     * Returns the key set in preorder traversal.
     * @return key set in preorder traversal
     */
    private Set<K> getPreorderKeySet(Entry<K,V> root){
        if (root==null){
            return Set.of();
        }
        Set<K> keySet=getPreorderKeySet(root.left);
        keySet.add(root.key);
        keySet.addAll(getPreorderKeySet(root.right));
        return keySet;
    }
}
