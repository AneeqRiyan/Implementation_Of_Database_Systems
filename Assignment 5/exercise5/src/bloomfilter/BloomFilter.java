package bloomfilter;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;
import java.util.function.Function;

public class BloomFilter<E> {
    protected static final int H_MUL = 31;

    private final byte[] bits;
    private final Function<Integer, Integer>[] hf;

    public BloomFilter(DataInput input) throws IOException {
        int numBytes = input.readInt();
        int vecSize = numBytes * Byte.SIZE;
        this.bits = new byte[numBytes];

        input.readFully(this.bits);

        this.hf = new Function[(int) -(Math.log(0.01) / Math.log(2))];

        for (int i = 0; i < this.hf.length; i++) {
            final int j = i;
            this.hf[i] = k -> (((j * H_MUL * k) % vecSize) + vecSize) % vecSize;
        }
    }

    public BloomFilter(final int numBytes) {
        final int vecSize = numBytes * Byte.SIZE;
        this.bits = new byte[numBytes * Byte.SIZE];
        // False positive rate of 1%:
        this.hf = new Function[(int) -(Math.log(0.01) / Math.log(2))];
        for (int i = 0; i < this.hf.length; i++) {
            final int j = i;
            this.hf[i] = k -> (((j * H_MUL * k) % vecSize) + vecSize) % vecSize;
        }
    }

    /**
     * Serialize this filter into the data output, packing 8 bytes from the bits vector into one output byte.
     */
    public void close(DataOutput output) throws IOException {
        output.writeInt(bits.length);
        output.write(bits);
    }

    /**
     * Add an element to this filter.
     */
    public void add(E element) {
        int hash = element.hashCode();
        for (Function<Integer, Integer> h : hf) {
            int idx = h.apply(hash);
            bits[idx / Byte.SIZE] |= (1 << (idx % Byte.SIZE));
        }
    }

    /**
     * Returns false, if the element was definitely not added to the filter, true otherwise.
     */
    public boolean containsMaybe(E element) {
        int hash = element.hashCode();
        for (Function<Integer, Integer> h : hf) {
            int idx = h.apply(hash);
            if ((bits[idx / Byte.SIZE] & (1 << (idx % Byte.SIZE))) == 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * Reset the state of the bloom filter.
     */
    public void reset() {
        for (int i = 0; i < bits.length; i++) {
            bits[i] = 0;
        }
    }
}
