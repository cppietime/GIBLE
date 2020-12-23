package com.funguscow.gible;

import java.util.*;

/**
 * A store that associates each entry with a numeric ID and a name
 * @param <T> Type of stored items
 */
public class IndexNameStore<T> implements Collection<IndexNameStore.IndexNameStoreEntry<T>> {

    public static class InvalidStoreRequestException extends RuntimeException{
        public InvalidStoreRequestException(String message){
            super(message);
        }
    }

    public static class StoreDuplicateException extends RuntimeException{
        public StoreDuplicateException(String message){
            super(message);
        }
    }

    public static class IndexNameStoreEntry<T>{
        public int id;
        public String name;
        public T entry;
        public IndexNameStoreEntry(int id, String name, T entry){
            this.id = id;
            this.name = name;
            this.entry = entry;
        }
    }

    public class IndexNameStoreIterator implements Iterator<IndexNameStoreEntry<T>> {

        private final IndexNameStore<T> store;
        private int index;

        private IndexNameStoreIterator(IndexNameStore<T> store){
            this.store = store;
            index = 0;
        }

        @Override
        public boolean hasNext() {
            return index < store.size();
        }

        @Override
        public IndexNameStoreEntry<T> next() {
            T item = store.get(index);
            return new IndexNameStoreEntry<>(index++, store.getName(item), item);
        }
    }

    protected final List<T> list;
    protected final Map<String, T> nameMap;
    protected final Map<T, Integer> ids;
    protected final Map<T, String> inverseNames;

    public IndexNameStore(){
        list = new ArrayList<>();
        nameMap = new HashMap<>();
        ids = new HashMap<>();
        inverseNames = new HashMap<>();
    }

    public int size(){
        return list.size();
    }

    @Override
    public boolean isEmpty() {
        return list.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return ids.containsKey(o);
    }

    @Override
    public Iterator<IndexNameStoreEntry<T>> iterator() {
        return new IndexNameStoreIterator(this);
    }

    @Override
    public Object[] toArray() {
        return list.toArray();
    }

    @Override
    public <T1> T1[] toArray(T1[] a) {
        return list.toArray(a);
    }

    public boolean add(IndexNameStoreEntry t) {
        throw new UnsupportedOperationException("IndexNameStore members must be added via `addWithName`");
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException("IndexNameStore cannot remove");
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return c.stream().allMatch(this::contains);
    }

    @Override
    public boolean addAll(Collection<? extends IndexNameStoreEntry<T>> c) {
        throw new UnsupportedOperationException("IndexNameStore members must be added via `addWithName`");
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException("IndexNameStore cannot remove");
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException("IndexNameStore cannot remove");
    }

    @Override
    public void clear() {
        ids.clear();
        list.clear();
        nameMap.clear();
    }

    /**
     *
     * @param item Item to register
     * @param name Key of item
     * @return ID of newly added item
     */
    public int addWithName(T item, String name){
        if(nameMap.containsKey(name))
            throw new StoreDuplicateException(String.format("Duplicate key %s", name));
        if(inverseNames.containsKey(item))
            throw new StoreDuplicateException(String.format("Duplicate entry %s", item.toString()));
        int id = list.size();
        list.add(item);
        nameMap.put(name, item);
        ids.put(item, id);
        inverseNames.put(item, name);
        return id;
    }

    /**
     * Adds an item with its string value as a name
     * @param item Item to add
     * @return ID of newly added item
     */
    public int addAutoName(T item){
        return addWithName(item, item.toString());
    }

    /**
     * Get the numeric ID of an item
     * @param item Item to search for
     * @return item's ID
     */
    public int getId(T item){
        Integer id = ids.get(item);
        if(id == null)
            throw new InvalidStoreRequestException(String.format("No registered item matching %s", item.toString()));
        return id;
    }

    /**
     * Get the name of an item
     * @param item Item to search
     * @return item's name
     */
    public String getName(T item){
        String name = inverseNames.get(item);
        if(name == null)
            throw new InvalidStoreRequestException(String.format("No registered item matching %s", item.toString()));
        return name;
    }

    /**
     * Get an item by id
     * @param i ID
     * @return Item matching ID
     */
    public T get(int i){
        if(i >= list.size())
            throw new InvalidStoreRequestException(String.format("Store index %d is out of range %d", i, list.size()));
        return list.get(i);
    }

    /**
     * Get an item by name
     * @param name Name of item
     * @return Item matching name
     */
    public T get(String name){
        T item = nameMap.get(name);
        if(item == null)
            throw new InvalidStoreRequestException(String.format("Key %s not in store map", name));
        return item;
    }

}
