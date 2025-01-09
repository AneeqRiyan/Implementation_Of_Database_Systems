package buffer;

public abstract class PageFaultRateBuffer extends Buffer {
    private int fsCount = 0;
    private int sCount = 0;

    public PageFaultRateBuffer(int capacity) {
        super(capacity);
    }

    public double getPageFaultRate() {
        return sCount > 0 ? (double) fsCount / sCount : 0.0;
    }

    protected Slot fix(char c) throws IllegalStateException {
        sCount++;  // Increment total page access count
        Slot slot = lookUp(c);
        if (slot == null) {
            fsCount++;  // Increment page fault count if page is not in buffer
        }
        return super.fix(c);
    }
}
