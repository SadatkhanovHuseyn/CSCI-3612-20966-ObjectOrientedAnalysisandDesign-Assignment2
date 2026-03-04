package ringbuffer;

import java.util.Optional;

public final class Reader<T> implements Runnable {

    private final RingBuffer<T> buffer;
    private final String name;
    private final long delayMs;
    private long nextSeqToRead;

    public Reader(RingBuffer<T> buffer, long startSeq, String name, long delayMs) {
        this.buffer = buffer;
        this.nextSeqToRead = startSeq;
        this.name = name;
        this.delayMs = delayMs;
    }

    public Optional<T> read() {
        synchronized (buffer.lock) {
            long oldestAvailable = buffer.oldestAvailableSeqUnsafe();
            long writeSeq = buffer.writeSeqUnsafe();

            if (nextSeqToRead < oldestAvailable) {
                nextSeqToRead = oldestAvailable;
            }

            if (nextSeqToRead >= writeSeq) {
                return Optional.empty();
            }

            T item = buffer.getAtSeqUnsafe(nextSeqToRead);
            nextSeqToRead++;
            return Optional.ofNullable(item);
        }
    }

    public long position() {
        synchronized (buffer.lock) {
            return nextSeqToRead;
        }
    }

    // FIXED: Accessing the variable 'capacity' instead of calling a method 'capacity()'
    public int ringIndex() {
        synchronized (buffer.lock) {
            return (int) (nextSeqToRead % buffer.capacity);
        }
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                Optional<T> item = read();
                if (item.isPresent()) {
                    T val = item.get();
                    System.out.println("[" + name + "] read=" + val + " pos=" + ringIndex() + " | " + buffer.debugRing());
                }
                Thread.sleep(delayMs);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
