# Ring Buffer

## Project Overview
This project implements a **Circular (Ring) Buffer** in Java designed for a **Single Writer and Multiple Readers**.

Key characteristics:

- The buffer has a **fixed capacity (N)**.
- Only **one writer** can write to the buffer.
- Multiple readers can read **independently**.
- Each reader maintains its **own reading position**.
- Reading **does not remove data** from the buffer.
- When the buffer becomes full, **new data overwrites the oldest data**.
- Slow readers may **miss overwritten data** and will automatically continue from the oldest available element.

---

## Design and Responsibilities

### `RingBuffer<T>`
Responsible for managing the buffer storage and write operations.

Responsibilities:
- Stores the circular array (`Object[] buffer`)
- Maintains `writeSequence`
- Handles writing new data
- Creates readers using `createReader()`
- Provides helper methods for readers to access data

Main methods:
- `write(T item)`
- `createReader()`
- `oldestAvailableSeqUnsafe()`
- `writeSeqUnsafe()`
- `getAtSeqUnsafe(long sequence)`

---

### `Reader<T>`
Represents an **independent consumer** of the ring buffer.

Responsibilities:
- Maintains its own `nextSeqToRead`
- Reads elements without affecting other readers
- Handles overwritten data if the reader becomes too slow
- Can run as a thread (`Runnable`)

Main methods:
- `read()`
- `position()`
- `run()`

---

### `Writer`
Represents the **single producer** of the system.

Responsibilities:
- Continuously writes values into the ring buffer
- Demonstrates producer behavior
- Runs as a thread (`Runnable`)

---

### `Main`
Used to **test and demonstrate** the ring buffer behavior.

It:
- Creates a ring buffer
- Creates multiple readers
- Writes several elements
- Shows that readers consume data independently.

---

## How to Run the Project

### 1. Compile the Java files

From the project root directory run:

```bash
javac ringbuffer/*.java
````

### 2. Run the Application

After successfully compiling the Java files, run the main program:

```bash
java ringbuffer.Main
```
