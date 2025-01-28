
/**
 * Create Custom HashTable to store String keys and values.
 * Implements dynamic resizing and ensures O(1) average runtime for lookups.
 */
public class CustomHashTable {

    // Entry for storing key-value pairs
    private static class Entry {
        String key; // The key for the hash table entry
        String value; // The associated value for the key
        Entry next; // Reference to the next entry in case of a collision (chaining)

        // Constructor to initialize an Entry with key and value
        public Entry(String key, String value) {
            this.key = key;
            this.value = value;
            this.next = null;
        }
    }

    private Entry[] table; // Array of linked lists to store key-value pairs
    private int capacity; // Current capacity of the hash table (size of the array)
    private int size;     // Number of key-value pairs currently stored in the table

    private static final double LOAD_FACTOR = 0.75; // Threshold to trigger resizing

    /**
     * Constructor to initialize the hash table with a specified initial capacity.
     * @param initialCapacity The initial capacity of the hash table.
     */
    public CustomHashTable(int initialCapacity) {
        this.capacity = initialCapacity;
        this.table = new Entry[capacity]; // Initialize the table with null entries
        this.size = 0; // Initialize size to 0
    }

    /**
     * Default constructor with an initial capacity of 16.
     */
    public CustomHashTable() {
        this(16); // Call the parameterized constructor with default capacity
    }

    /**
     * Hash function to calculate the index for a given key.
     * Ensures uniform distribution to minimize collisions.
     * @param key The key to hash.
     * @return The index in the table where the key-value pair should be stored.
     */
    private int hash(String key) {
        int hashCode = key.hashCode(); // Generate a hash code for the key
        return Math.abs(hashCode % capacity); // Map the hash code to a valid index
    }

    /**
     * Puts a key-value pair into the hash table.
     * If the key already exists, its value is updated.
     * @param key The key to insert or update.
     * @param value The value associated with the key.
     */
    public void put(String key, String value) {
        int index = hash(key); // Calculate the index using the hash function
        Entry head = table[index]; // Get the head of the linked list at the index

        // Check if the key already exists in the chain
        while (head != null) {
            if (head.key.equals(key)) { // Key found, update the value
                head.value = value;
                return;
            }
            head = head.next; // Move to the next entry in the chain
        }

        // Insert the new key-value pair at the beginning of the chain
        Entry newEntry = new Entry(key, value);
        newEntry.next = table[index]; // Point the new entry to the current head
        table[index] = newEntry; // Update the head to the new entry
        size++; // Increment the size of the hash table

        // Resize the table if the load factor is exceeded
        if ((double) size / capacity > LOAD_FACTOR) {
            resize();
        }
    }

    /**
     * Retrieves the value associated with the given key.
     * Returns null if the key is not found.
     * @param key The key to search for.
     * @return The value associated with the key, or null if not found.
     */
    public String get(String key) {
        int index = hash(key); // Calculate the index using the hash function
        Entry head = table[index]; // Get the head of the linked list at the index

        // Search the chain for the key
        while (head != null) {
            if (head.key.equals(key)) { // Key found, return the value
                return head.value;
            }
            head = head.next; // Move to the next entry in the chain
        }

        return null; // Key not found
    }

    /**
     * Removes a key-value pair from the hash table.
     * Returns the value associated with the key, or null if the key is not found.
     * @param key The key to remove.
     * @return The value associated with the removed key, or null if not found.
     */
    public String remove(String key) {
        int index = hash(key); // Calculate the index using the hash function
        Entry head = table[index]; // Get the head of the linked list at the index
        Entry prev = null; // Track the previous entry for removal

        // Search for the key in the chain
        while (head != null) {
            if (head.key.equals(key)) { // Key found
                if (prev == null) {
                    table[index] = head.next; // Remove head by updating the index pointer
                } else {
                    prev.next = head.next; // Bypass the current entry
                }
                size--; // Decrement the size of the hash table
                return head.value; // Return the removed value
            }
            prev = head; // Move to the next entry in the chain
            head = head.next;
        }

        return null; // Key not found
    }

    /**
     * Resizes the hash table when the load factor threshold is exceeded.
     * Rehashes all existing entries into a new table with double the capacity.
     */
    private void resize() {
        int newCapacity = capacity * 2; // Double the capacity
        Entry[] newTable = new Entry[newCapacity]; // Create a new table with increased capacity

        // Rehash all entries from the old table to the new table
        for (Entry head : table) {
            while (head != null) {
                int newIndex = Math.abs(head.key.hashCode() % newCapacity); // Calculate the new index
                Entry next = head.next; // Save the next entry in the chain

                // Insert into the new table
                head.next = newTable[newIndex]; // Point the entry to the new chain
                newTable[newIndex] = head; // Update the new index pointer

                head = next; // Move to the next entry in the old chain
            }
        }

        table = newTable; // Replace the old table with the new table
        capacity = newCapacity; // Update the capacity
    }

    /**
     * Returns the number of key-value pairs in the hash table.
     * @return The current size of the hash table.
     */
    public int size() {
        return size;
    }

    /**
     * Demonstrates the functionality of the hash table.
     */
    public static void main(String[] args) {
        CustomHashTable hashTable = new CustomHashTable();

        // Insert key-value pairs
        hashTable.put("name", "Alice");
        hashTable.put("age", "25");
        hashTable.put("city", "New York");

        // Retrieve values
        System.out.println("Name: " + hashTable.get("name")); // Output: Alice
        System.out.println("Age: " + hashTable.get("age"));   // Output: 25

        // Update a value
        hashTable.put("age", "26");
        System.out.println("Updated Age: " + hashTable.get("age")); // Output: 26

        // Remove a key-value pair
        System.out.println("Removed City: " + hashTable.remove("city")); // Output: New York
        System.out.println("City: " + hashTable.get("city")); // Output: null

        // Display size
        System.out.println("Size: " + hashTable.size()); // Output: 2
    }
}
