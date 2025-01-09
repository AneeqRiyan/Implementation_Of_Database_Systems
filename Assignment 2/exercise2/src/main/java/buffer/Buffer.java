package buffer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Buffer {
    class Slot {
        int index;
        char c;
        boolean isFixed;

        Slot(int index) {
            this.index = index;
            this.isFixed = false;
        }

        void fix() {
            if (!isFixed)
                fixedSlots++;
            isFixed = true;
        }

        void unfix() {
            if (isFixed)
                fixedSlots--;
            isFixed = false;
        }

        void insert(char c) {
            slot_map.put(c, this);
            this.c = c;
            size++;
        }

        void remove() {
            if (index < size) {
                Slot slot = slots.get(--size);

                slot_map.remove(c);
                slots.set(slot.index = index, slot);
                slots.set(index = size, this);
                if (isFixed)
                    fixedSlots--;

                isFixed = false;
            }
        }
    }

    int fixedSlots = 0;

    int size = 0;

    List<Slot> slots;

    Map<Character, Slot> slot_map;

    Buffer(int capacity) {
        this.slots = new ArrayList<Slot>(capacity);
        for (int i = 0; i < capacity; i++)
            slots.add(newSlot(i));
        slot_map = new HashMap<Character, Slot>();
    }

    abstract Slot victim();

    Slot newSlot(int index) {
        return new Slot(index);
    }

    public int size() {
        return size;
    }

    public int capacity() {
        return slots.size();
    }

    Slot lookUp(char c) {
        return slot_map.get(c);
    }


    Slot fix(char c) throws IllegalStateException {
        Slot slot = lookUp(c);

        if (slot == null) {
            if (fixedSlots == slots.size()) {
                throw new IllegalStateException("Buffer overflow. Too many slots fixed.");
            }

            // Evict a victim slot if the buffer is full
            if (size() == slots.size()) {
                Slot vic = victim();
                if (vic != null) {
                    vic.remove();
                } else {
//                    System.err.println("Victim returned null. No slot available for eviction.");
                    return null;  // Safeguard against further null operations
                }
            }

            // Insert a new slot if space is available
            if (size() < slots.size()) {
                slot = slots.get(size());
                slot.insert(c);
                slot.fix();
            } else {
                throw new IllegalStateException("Failed to insert: Buffer size exceeds capacity.");
            }
        } else {
            slot.fix();
        }

        return slot;
    }

    public void unfix(char c) {
        Slot slot = lookUp(c);

        if (slot != null)
            slot.unfix();
    }

    public boolean contains(char c) {
        return lookUp(c) != null;
    }

    public boolean isFixed(char c) {
        Slot slot = lookUp(c);

        return slot != null && slot.isFixed;
    }

    public void get(char c, boolean unfix) {
        Slot slot = fix(c);

        if (slot == null) {
//            System.err.println("Failed to retrieve page " + c + ": No available slot.");
            return;  // Exit early if no slot was allocated
        }

        if (unfix) {
            slot.unfix();
        }
    }


    public void remove(char c) {
        Slot slot = lookUp(c);

        if (slot != null) {
            slot.remove();
        }
    }

    public int fixedSlots() {
        return fixedSlots;
    }
}