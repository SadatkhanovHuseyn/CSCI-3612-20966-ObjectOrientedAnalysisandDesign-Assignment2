# Ring Buffer

## Project Overview
This project is a Java implementation of a **fixed-capacity circular (ring) buffer** designed for a **single writer** and **multiple independent readers**.

- The buffer holds up to **N** items.
- A **single writer** appends items using `write()`.
- Multiple readers can be created via `createReader()`.
- **Each reader has its own read position** (independent consumption).
- Reads are **non-destructive**: one reader reading an item does not remove it for others.
- When the buffer is full, the writer **overwrites the oldest data** (no blocking).
- If a reader is too slow and its target data was overwritten, it **skips forward** to the **oldest available** item (lapping/overwrite handling).

---

## How It Works (High-Level)
The ring buffer stores items in an array and tracks a monotonically increasing `writeSequence`:

- The array index for a written item is:  
  `index = writeSequence % capacity`
- The **oldest available** sequence is:  
  `oldestAvailable = max(0, writeSequence - capacity)`

Each reader tracks `nextSeqToRead`. On `read()`:

1. Compute `oldestAvailable` and current `writeSequence`.
2. If `nextSeqToRead < oldestAvailable`, the reader **missed data** (it was overwritten), so it jumps to `oldestAvailable`.
3. If `nextSeqToRead >= writeSequence`, there is nothing new to read → returns `Optional.empty()`.
4. Otherwise, read the element at `nextSeqToRead`, increment it, return `Optional.of(item)`.

---

## Design (OO Responsibilities)

### `RingBuffer<T>`
**Responsibility:** Owns the storage and write-side sequencing.
- Stores the underlying `Object[] buffer`
- Holds `capacity` and a `writeSequence`
- Provides:
  - `write(T item)` → write/overwrite into ring
  - `createReader()` → factory for `Reader<T>`
  - helpers used by readers:
    - `oldestAvailableSeqUnsafe()`
    - `writeSeqUnsafe()`
    - `getAtSeqUnsafe(long seq)`
  - `debugRing()` → quick buffer state string

### `Reader<T>` (implements `Runnable`)
**Responsibility:** Represents an independent consumer cursor over the buffer.
- Holds `nextSeqToRead` (per-reader position)
- `read()` returns `Optional<T>`
- Implements lapping logic (skip to oldest available if overwritten)
- `run()` shows an example continuous reader loop (poll + sleep + log)

### `Writer` (implements `Runnable`)
**Responsibility:** Example producer that continuously writes incrementing values.
- Demonstrates continuous publishing into the ring buffer

### `Main`
**Responsibility:** Simple deterministic demo (write a few items, read with two readers).

### `ReaderStart` (enum)
**Responsibility:** Declares possible reader start policies (e.g., “from now” vs “from oldest available”).  
*(Included for extension/clarity; can be wired into `createReader()` to choose start behavior.)*

---

## UML Class Diagram (Mermaid)
```mermaid
classDiagram
  direction LR

  class RingBuffer~T~ {
    - Object[] buffer
    + int capacity
    + Object lock
    - long writeSequence
    - List~Reader~T~~ readers
    + RingBuffer(int capacity)
    + void write(T item)
    + Reader~T~ createReader()
    + long oldestAvailableSeqUnsafe()
    + long writeSeqUnsafe()
    + T getAtSeqUnsafe(long sequence)
    + String debugRing()
  }

  class Reader~T~ {
    - RingBuffer~T~ buffer
    - String name
    - long delayMs
    - long nextSeqToRead
    + Reader(RingBuffer~T~ buffer, long startSeq, String name, long delayMs)
    + Optional~T~ read()
    + long position()
    + int ringIndex()
    + void run()
  }

  class Writer {
    - RingBuffer~Integer~ buffer
    - long delayMs
    - long counter
    + Writer(RingBuffer~Integer~ buffer, long delayMs)
    + void run()
  }

  class Main {
    + static void main(String[] args)
  }

  class ReaderStart {
    <<enumeration>>
    FROM_NOW
    FROM_OLDEST_AVAILABLE
  }

  RingBuffer~T~ "1" --> "*" Reader~T~ : creates/holds
  Writer --> RingBuffer~Integer~ : writes to
  Main --> RingBuffer~String~ : demo
  Main --> Reader~String~ : demo reads

