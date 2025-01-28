import java.util.LinkedList;

/**
 * A shared queue implementation for multiple readers and a single writer.
 * This queue handles synchronization to ensure thread safety and efficient message consumption.
 */
class SharedQueue {
    private final LinkedList<String> queue = new LinkedList<>();
    private final int capacity;

    public SharedQueue(int capacity) {
        this.capacity = capacity;
    }

    /**
     * Adds a message to the queue. Blocks if the queue is full.
     *
     * @param message The message to be added.
     */
    public synchronized void add(String message) {
        while (queue.size() == capacity) {
            try {
                wait(); // Wait until space is available in the queue.
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        queue.add(message);
        notifyAll(); // Notify consumers that a new message is available.
    }

    /**
     * Removes and returns a message from the queue. Blocks if the queue is empty.
     *
     * @return The message removed from the queue.
     */
    public synchronized String remove() {
        while (queue.isEmpty()) {
            try {
                wait(); // Wait until a message is available.
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        String message = queue.removeFirst();
        notifyAll(); // Notify the writer that space is available.
        return message;
    }
}

/**
 * A test program showcasing the shared queue with multiple readers and a single writer.
 */
public class SharedQueueDemo {

    public static void main(String[] args) {
        SharedQueue sharedQueue = new SharedQueue(10); // Shared queue with a capacity of 10.

        // Create and start 5 consumer threads.
        for (int i = 1; i <= 5; i++) {
            final int consumerId = i;
            new Thread(() -> {
                while (true) {
                    String message = sharedQueue.remove(); // Consume a message.
                    System.out.println("Consumer " + consumerId + " consumed: " + message);
                }
            }).start();
        }

        // Create and start a writer thread.
        new Thread(() -> {
            int messageCount = 0;
            while (true) {
                try {
                    Thread.sleep(200); // Add 5 messages per second.
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                sharedQueue.add("Message " + (++messageCount)); // Produce a new message.
                System.out.println("Writer added: Message " + messageCount);
            }
        }).start();
    }
}

/*
Explanation of synchronization and design considerations:

1. Synchronization:
   - The `add` and `remove` methods are synchronized to ensure thread safety when accessing the shared queue.
   - `wait` and `notifyAll` are used to coordinate between the writer and readers.
   - The writer thread waits if the queue is full, and readers wait if the queue is empty.

2. Multiple Readers:
   - Readers use the `wait` method, which puts threads in a waiting state without consuming CPU resources.
   - Multiple readers can wait for messages without blocking each other since the monitor's intrinsic queue handles them.

3. Minimizing Lock Time:
   - The synchronized block only locks the critical sections: adding or removing elements from the queue.
   - The time spent in the locked condition is minimized by keeping the critical section concise.

4. Fair Distribution:
   - Since `notifyAll` wakes up all waiting threads, messages are distributed fairly among the readers as they compete for the lock.

5. Avoiding Thread.sleep in Consumers:
   - The consumers rely on `wait` for synchronization instead of polling or sleeping, ensuring efficient CPU utilization.
*/
