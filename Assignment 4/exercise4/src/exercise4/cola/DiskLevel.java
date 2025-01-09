package exercise4.cola;

import java.util.Iterator;

import xxl.core.collections.containers.Container;
import xxl.core.util.Pair;

/**
 * Abstracts a cola-level persisted on disk.
 */
public class DiskLevel<K extends Comparable<K>, V> implements COLALevel<K, V> {

    /** Offset of the first block in the container */
    private final Long offset;

    /** The underlying container */
    private final Container container;

    /** Number of elements stored in each block */
    private final int elemsPerBlock;

    /** Number of elements stored in this level */
    private final int numElems;

    /**
     * @param offset        the offset in the container of the first block of this level
     * @param level         the actual level
     * @param elemsPerBlock the number of entries per block
     * @param container     the underlying container
     */
    public DiskLevel(Long offset, int level, int elemsPerBlock, Container container) {
        this.offset = offset;
        this.elemsPerBlock = elemsPerBlock;
        this.container = container;
        this.numElems = 1 << level;
    }

    @Override
    public Iterator<Pair<K, V>> iterator() {
        return new Iterator<>() {

            int read = 0;

            COLABlock<K, V> block = null;

            @Override
            public boolean hasNext() {
                return read < numElems;
            }

            @Override
            public Pair<K, V> next() {
                if (read % elemsPerBlock == 0) {
                    block = (COLABlock<K, V>) container.get(offset + (read / elemsPerBlock * BasicCOLA.DISK_BLOCK_SIZE));
                }
                return block.get(read++ % elemsPerBlock);
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("Remove not supported");
            }
        };
    }

    @Override
    public V search(K key)  {
        // TODO:
        try {
            int totalBlocks = (int) Math.ceil((double) numElems / elemsPerBlock);
            for (int i = 0; i < totalBlocks; i++) {
                COLABlock<K, V> block = (COLABlock<K, V>) container.get(offset + i * BasicCOLA.DISK_BLOCK_SIZE);
                for (int j = 0; j < block.getSize(); j++) {
                    Pair<K, V> pair = block.get(j);
                    int cmp = pair.getFirst().compareTo(key);
                    if (cmp == 0) {
                        return pair.getSecond(); // Key found
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null; // Key not found
    }


    @Override
    public void set(Iterator<Pair<K, V>> elms) {
        long id = offset;
        int i = 0;
        COLABlock<K, V> block = null;

        while (elms.hasNext()) {
            int mod = i++ % elemsPerBlock;
            if (mod == 0) {
                if (block != null) {
                    // Check block size consistency
                    if (block.getSize() > elemsPerBlock) {
                        throw new IllegalArgumentException("Block size exceeds allowed limit: " + elemsPerBlock);
                    }

                    container.update(id, block);
                    System.out.println("Writing block to offset: " + id + ", size: " + block.getSize());
                    id += BasicCOLA.DISK_BLOCK_SIZE;
                }
                block = new COLABlock<>(elemsPerBlock);
            }
            block.set(mod, elms.next());
        }

        // Save the last block
        if (block != null) {
            container.update(id, block);
            System.out.println("Writing final block to offset: " + id + ", size: " + block.getSize());
        }
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        int i = 0;
        for (Pair<K, V> p : this) {
            if (i > 0) sb.append(", ");
            sb.append(p);
            i++;
        }
        sb.append(']');
        return sb.toString();
    }
}