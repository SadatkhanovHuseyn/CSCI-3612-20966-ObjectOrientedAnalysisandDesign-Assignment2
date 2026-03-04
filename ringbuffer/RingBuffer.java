package ringbuffer;

import java.util.ArrayList;
import java.util.List;

public class RingBuffer<T> {
    private final Object[] buffer;
    public final int capacity;
    public final Object lock = new Object(); // Added missing lock
    private long writeSequence = 0;
    private final List<Reader<T>> readers = new ArrayList<>(); // Changed name

    public RingBuffer(int capacity) {
        this.capacity = capacity;
        this.buffer = new Object[capacity];
    }

    public synchronized void write(T item) {
        long index = writeSequence % capacity;
        buffer[(int) index] = item;
        writeSequence++;
    }

    public synchronized Reader<T> createReader() {
        // Updated to match Reader.java constructor: (buffer, startSeq, name, delay)
        Reader<T> reader = new Reader<>(this, 0, "Reader-" + readers.size(), 100);
        readers.add(reader);
        return reader;
    }

    // Missing methods required by Reader.java
    public long oldestAvailableSeqUnsafe() {
        return Math.max(0, writeSequence - capacity);
    }

    public long writeSeqUnsafe() {
        return writeSequence;
    }

    public T getAtSeqUnsafe(long sequence) {
        return (T) buffer[(int) (sequence % capacity)];
    }

    public String debugRing() {
        return "Buffer Status: " + writeSequence + " items written";
    }
}
