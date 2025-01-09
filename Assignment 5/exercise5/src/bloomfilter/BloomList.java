package bloomfilter;

import java.util.Collection;
import java.util.LinkedList;

public class BloomList<E> extends LinkedList<E> {
    private final BloomFilter<E> bf;

    /**
     * Create a BloomList from a BloomFilter instance.
     */
    public BloomList(BloomFilter<E> bf) {
        this.bf = bf;
    }

    /**
     * Reset the bloom filter to bring it to the optimal state
     * (i.e. the state reached when inserting all current element into the filter).
     * Can be called after (many) deletions to reduce the number of false positives.
     */
    public void resetBloomFilter() {
        // TODO
    }

    @Override
    public boolean add(E e) {
        // TODO
        return true;
    }

    @Override
    public void add(int index, E element) {
        // TODO
    }

    @Override
    public E set(int i, E e) {
        // TODO
        return null;
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        // TODO
        return true;
    }

    @Override
    public boolean contains(Object e) {
        // TODO
        return false;
    }

    @Override
    public void clear() {
        // TODO
    }
}
