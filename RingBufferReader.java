package ringbuffer;

public class RingBufferReader<T> {

    private final RingBuffer<T> buffer;
    private long readSequence;

    protected RingBufferReader(RingBuffer<T> buffer) {
        this.buffer = buffer;
        this.readSequence = buffer.getWriteSequence();
    }

    public synchronized T read() {
        T item = buffer.read(readSequence);

        if (item == null) {
            if (readSequence < buffer.getWriteSequence() - buffer.getCapacity()) {
                // Reader is too slow — missed data
                readSequence = buffer.getWriteSequence() - buffer.getCapacity();
            }
            return null;
        }

        readSequence++;
        return item;
    }
}
