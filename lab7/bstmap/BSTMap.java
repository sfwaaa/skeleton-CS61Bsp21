package bstmap;

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
    private Entry<K,V> deletedEntry;//Due to java f**king type system,declare a variable here
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
        throw new UnsupportedOperationException();
        //Set<K> keySet=Set.of();
        //getPreorderKeySet(root,keySet);
        //return keySet;
    }

    /** Removes the mapping for the specified key from this map if present. */
    @Override
    public V remove(K key) {
        this.deletedEntry=null;
        root=delete(root, key);
        if (deletedEntry==null){
            return null;
        }
        size--;
        return deletedEntry.value;
    }
    /** Removes the entry for the specified key
     * only if it is currently mapped to the specified value.*/
    @Override
    public V remove(K key, V value) {
        this.deletedEntry=null;
        root=delete(root, key,value);
        if (deletedEntry==null){
            return null;
        }
        size--;
        return deletedEntry.value;
    }

    @Override
    public Iterator<K> iterator() {
        throw new UnsupportedOperationException();
        //return new BSTMapIterator<>(this.root,this.size);
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
        int comparison= key.compareTo(current.key);
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

        int comparison= key.compareTo(current.key);
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
    private Entry<K,V> delete(Entry<K,V> current,K key){
        if (current==null){
            this.deletedEntry=null;
            return null;
        }
        int comparison= key.compareTo(current.key);
        //compare and move to the correct subtree if entry not found
        if (comparison<0){
            current.left = delete(current.left,key);
            return  current;
        }
        else if (comparison>0) {
            current.right= delete(current.right,key);
            return current;
        }

        //if found, delete current Item and check if it has subtrees
        if (current.left==null){//leaf or single right subtree
            this.deletedEntry=current;
            return current.right;
        }
        if (current.right==null){//single left subtree
            this.deletedEntry=current;
            return current.left;
        }
        //two subtrees, replace current with its predecessor(chosen here) or successor
        current.left = deleteMax(current.left);
        Entry<K,V> predecessor=deletedEntry;


        predecessor.left=current.left;
        predecessor.right=current.right;

        this.deletedEntry=current;
        return  predecessor;

    }

    /**
     * Delete the entry with the given key and given value
     * @param current root of the current bst
     * @param key the key of the entry to be deleted
     * @return the deleted entry(in [0],null if entry not found)
     * and the proper "current" after deletion(in [1])
     */
    private Entry<K,V> delete(Entry<K,V> current,K key,V value){
        if (current==null){
            this.deletedEntry=null;
            return null;
        }
        int comparison= key.compareTo(current.key);
        //compare and move to the correct subtree if entry not found
        if (comparison<0){
            current.left = delete(current.left,key,value);
            return  current;
        }
        else if (comparison>0) {
            current.right= delete(current.right,key,value);
            return current;
        }
        if (current.value!=value){
            this.deletedEntry=null;
            return null;
        }

        //if found, delete current Item and check if it has subtrees
        if (current.left==null){//leaf or single right subtree
            this.deletedEntry=current;
            return current.right;
        }
        if (current.right==null){//single left subtree
            this.deletedEntry=current;
            return current.left;
        }
        //two subtrees, replace current with its predecessor(chosen here) or successor
        current.left = deleteMax(current.left);
        Entry<K,V> predecessor=deletedEntry;


        predecessor.left=current.left;

        this.deletedEntry=current;
        return  predecessor;

    }


    /**
     * Delete the maximum entry in the bst.
     * @param current root of the current bst
     * @return the proper "current" after deletion(in [1])
     */
    private Entry<K,V> deleteMax(Entry<K,V> current){
        if (current==null){
            return null;
        }
        current.right=deleteMax(current.right);
        if (current.right==null){
            this.deletedEntry=current;
            return current.left;
        }
        else {
           return current;
        }
    }

    /**
     * set the key set in preorder traversal in the parameter set.
     */
    private void getPreorderKeySet(Entry<K,V> root,Set<K> keySet){
        if (root==null){
            return ;
        }
        keySet.add(root.key);
        getPreorderKeySet(root.left,keySet);
        getPreorderKeySet(root.right,keySet);
    }
}
