package buffer;

import java.util.ArrayDeque;

public class SimpleTwoQueueBuffer extends PageFaultRateBuffer {

    private final ArrayDeque<Slot> a1 = new ArrayDeque<>();
    private final ArrayDeque<Slot> am = new ArrayDeque<>();
    private int kin;

    public SimpleTwoQueueBuffer(int capacity) {
        super(capacity);
        this.kin = capacity / 4;  // Set kin to 25% of total capacity
    }

    @Override
    protected Buffer.Slot fix(char c) throws IllegalStateException {
        Slot slot = lookUp(c);

        if (slot == null) {
            // If slot is not in buffer, handle as a page fault
            System.out.println("Page fault for: " + c);
            slot = super.fix(c);
            if (a1.size() < kin) {
                a1.addFirst(slot);  // Add to a1 if there's space
            } else {
                System.out.println("Evicting from a1 due to capacity");
                Slot evicted = a1.pollLast();
                a1.addFirst(slot);
                if (evicted != null) {
                    System.out.println("Evicted page: " + evicted.c);
                }
            }
        }
        return slot;
    }

    protected Slot victim() {
        if (!a1.isEmpty()) {
            return a1.pollLast();
        } else if (!am.isEmpty()) {
            return am.pollLast();
        } else {
            throw new IllegalStateException("No slots available for eviction.");
        }
    }
}
