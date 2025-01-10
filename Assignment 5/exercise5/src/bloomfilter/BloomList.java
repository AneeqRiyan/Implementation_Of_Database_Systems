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
        bf.reset();
        for (E element : this) {
            bf.add(element);
        }
    }

    @Override
    public boolean add(E e) {
        bf.add(e);
        return super.add(e);
    }

    @Override
    public void add(int index, E element) {
        bf.add(element);
        super.add(index, element);
    }

    @Override
    public E set(int index, E element) {
        E oldElement = super.set(index, element);
        resetBloomFilter();
        return oldElement;
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        boolean added = super.addAll(index, c);
        if (added) {
            for (E element : c) {
                bf.add(element);
            }
        }
        return added;
    }

    @Override
    public boolean contains(Object e) {
        if (!bf.containsMaybe((E) e)) {
            return false;
        }
        return super.contains(e);
    }

    @Override
    public void clear() {
        super.clear();
        bf.reset();
    }
}
