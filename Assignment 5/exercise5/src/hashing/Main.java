package hashing;

import xxl.core.collections.containers.io.BlockFileContainer;
import xxl.core.io.Buffer;
import xxl.core.io.LRUBuffer;
import xxl.core.io.converters.LongConverter;

public class Main {
    public static void main(String[] args) {
        int numInserts = 1000;

        BlockFileContainer primary = new BlockFileContainer("main", ExternalLinearHashMap.BLOCK_SIZE);
        BlockFileContainer secondary = new BlockFileContainer("overflow", ExternalLinearHashMap.BLOCK_SIZE);
        Buffer<Object, Integer, HashBlock<Long, Long>> buffer = new LRUBuffer<>(512);

        primary.clear();
        secondary.clear();

        ExternalLinearHashMap<Long, Long> map = new ExternalLinearHashMap<Long, Long>(LongConverter.DEFAULT_INSTANCE, LongConverter.DEFAULT_INSTANCE, primary, secondary, buffer);
        for (long i = 0; i < numInserts; i++) {
            map.insert(i, i);

            for (long j = 0; j <= i; j++) {
                if (!map.contains(j)) {
                    throw new RuntimeException("Element not found: " + j);
                }
            }

            if (map.getSize() != i + 1) {
                throw new RuntimeException("wrong size");
            }
        }

        for (long i = 0; i < numInserts; i++) {
            if (!map.contains(i)) {
                throw new RuntimeException("Element not found: " + i);
            }
        }

        map.close();

        // Map should be persisted and work after re-creating
        map = new ExternalLinearHashMap<Long, Long>(LongConverter.DEFAULT_INSTANCE, LongConverter.DEFAULT_INSTANCE, primary, secondary, buffer);

        for (long i = 0; i < numInserts; i++) {
            if (!map.contains(i)) {
                throw new RuntimeException("Element not found: " + i);
            }
        }
        System.out.println("All keys successfully inserted and validated.");
        System.out.println("Final size of hash map: " + map.getSize());
        System.out.println("Load factor: " + map.getLoadFactor());

        long start = System.currentTimeMillis();
        for (long i = 0; i < numInserts; i++) {
            map.insert(i, i);
        }
        long end = System.currentTimeMillis();
        System.out.println("Insertion time: " + (end - start) + " ms");
    }

//    for 1000 records insertion
//    All keys successfully inserted and validated.
//    Final size of hash map: 1000
//    Load factor: 0.7993605115907274
//    Insertion time: 0 ms


//    All keys successfully inserted and validated.
//    Final size of hash map: 10000
//    Load factor: 0.7999360051195904
//    Insertion time: 120 ms
}