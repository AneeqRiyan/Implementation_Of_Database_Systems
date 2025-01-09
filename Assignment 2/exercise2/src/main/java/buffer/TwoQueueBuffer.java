package buffer;

import java.util.ArrayDeque;

public class TwoQueueBuffer extends PageFaultRateBuffer {

    private final ArrayDeque<Slot> a1in = new ArrayDeque<>();
    private final ArrayDeque<Character> a1out = new ArrayDeque<>();
    private final ArrayDeque<Slot> am = new ArrayDeque<>();
    private final int kin;
    private final int kout;

    public TwoQueueBuffer(int capacity) {
        super(capacity);
        this.kin = capacity / 4;  // Set kin to 25% of total capacity
        this.kout = kin;  // Set kout to the same size as kin (can be customized)
    }


    @Override
    protected Slot fix(char c) {
        Slot slot = lookUp(c);

        if (am.contains(slot)) {
            am.remove(slot);
            am.addFirst(slot);
//            System.out.println("Hit in am for: " + c);
        } else if (a1in.contains(slot)) {
            a1in.remove(slot);
            am.addFirst(slot);
//            System.out.println("Promoting from a1in to am for: " + c);
        } else if (a1out.contains(c)) {
            a1out.remove(c);
            slot = super.fix(c);
            am.addFirst(slot);
//            System.out.println("Promoting from a1out to am for: " + c);
        } else {
            if (a1in.size() >= kin) {
                Slot removed = a1in.pollLast();
                if (removed != null) {
                    a1out.addFirst(removed.c);
                    if (a1out.size() > kout) {
                        a1out.pollLast();
                    }
                }
            }
            slot = super.fix(c);

            // Check for null before adding to a1in
            if (slot != null) {
                a1in.addFirst(slot);
//                System.out.println("Page fault for: " + c);
            } else {
//                System.err.println("Failed to fix page " + c + ": No available slot for eviction.");
            }
        }

        return slot;
    }



    protected Slot victim() {
        if (!a1in.isEmpty()) {
            return a1in.pollLast();  // Evict from a1in
        } else if (!am.isEmpty()) {
            return am.pollLast();  // Evict from am
        } else {
//            System.err.println("No slots available in a1in or am for eviction.");
            return null;  // Gracefully handle empty state
        }
    }
}
