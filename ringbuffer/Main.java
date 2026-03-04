package ringbuffer;

public class Main {
    public static void main(String[] args) {
        RingBuffer<String> buffer = new RingBuffer<>(5);

        Reader<String> reader1 = buffer.createReader();
        Reader<String> reader2 = buffer.createReader();

        buffer.write("A");
        buffer.write("B");
        buffer.write("C");

        System.out.println("Reader1: " + reader1.read().orElse("Empty"));
        System.out.println("Reader2: " + reader2.read().orElse("Empty"));

        buffer.write("D");
        buffer.write("E");
        buffer.write("F"); 

        System.out.println("Reader1: " + reader1.read().orElse("Empty"));
        System.out.println("Reader2: " + reader2.read().orElse("Empty"));
    }
}
