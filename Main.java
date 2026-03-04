package ringbuffer;

public class Main {

    public static void main(String[] args) {

        RingBuffer<String> buffer = new RingBuffer<>(5);

        RingBufferReader<String> reader1 = buffer.createReader();
        RingBufferReader<String> reader2 = buffer.createReader();

        buffer.write("A");
        buffer.write("B");
        buffer.write("C");

        System.out.println("Reader1: " + reader1.read());
        System.out.println("Reader2: " + reader2.read());

        buffer.write("D");
        buffer.write("E");
        buffer.write("F"); // Overwrites oldest

        System.out.println("Reader1: " + reader1.read());
        System.out.println("Reader2: " + reader2.read());
    }
}
