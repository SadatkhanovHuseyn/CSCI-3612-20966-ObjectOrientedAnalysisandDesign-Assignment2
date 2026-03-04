package ringbuffer;

import java.util.ArrayList;
import java.util.List;

public class RingBuffer<T> {

    private final Object[] buffer;
    private final int capacity;

    private long writeSequence = 0;

    private final List<RingBufferReader<T>> readers = new ArrayList<>();

    public RingBuffer(int capacity) {
        this.capacity = capacity;
        this.buffer = new Object[capacity];
    }

    public synchronized void write(T item) {
        long index = writeSequence % capacity;
        buffer[(int) index] = item;
        writeSequence++;
    }

    public synchronized RingBufferReader<T> createReader() {
        RingBufferReader<T> reader = new RingBufferReader<>(this);
        readers.add(reader);
        return reader;
    }

    protected synchronized T read(long sequence) {
        if (sequence < writeSequence - capacity) {
            return null; // Data overwritten
        }

        if (sequence >= writeSequence) {
            return null; // Nothing new
        }

        long index = sequence % capacity;
        return (T) buffer[(int) index];
    }

    protected synchronized long getWriteSequence() {
        return writeSequence;
    }

    protected int getCapacity() {
        return capacity;
    }
}
