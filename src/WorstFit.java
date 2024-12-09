import java.util.ArrayList;

// Class representing a memory partition
class Partition {
    int start; // Start address of the block (logical, for tracking purposes)
    int size; // Size of the block in KB
    boolean isAllocated; // Allocation status

    //Partition constructor (Each partition should have a size and its status)
    Partition(int start, int size) {
        this.start = start;
        this.size = size;
        this.isAllocated = false; // By default, the partition is free
    }
}

//This is basically the dynamic memory allocator class
public class WorstFit {
    ArrayList<Partition> partitions = new ArrayList<Partition>(); // List of partitions

    public WorstFit(int totalMemorySize) {
        // Initialize with a single free partition representing the entire memory
        partitions.add(new Partition(0, totalMemorySize));
    }

    //Method to allocate the processes (includes two parameters as processName and processSize)
    public void processAllocation(String processName, int processSize) {
        int worstFitIndex = -1; // Index of the worst fit partition
        int maxBlockSize = -1; // Size of the largest eligible partition

        //Finding the largest partition that can fit the process (Iterates through partitions)
        for (int i = 0; i < partitions.size(); i++) {
            Partition p = partitions.get(i);
            if (!p.isAllocated && p.size >= processSize && p.size > maxBlockSize) {
                maxBlockSize = p.size; // Update the size of the largest block found
                worstFitIndex = i;     // Store the index of the worst fit block
            }
        }

        // If no suitable partition is found, allocation fails
        if (worstFitIndex == -1) {
            System.out.println("\nProcess " + processName + " of size " + processSize + " KB cannot be allocated ");
            return;
        }

        //Allocate the process to the selected partition
        Partition selectedPartition = partitions.get(worstFitIndex);
        System.out.println("\nAllocation of Process " + processName + " of size " + processSize + "KB to partition Starting at " + selectedPartition.start + " with size " + selectedPartition.size + " KB.");

        if (selectedPartition.size > processSize) {
            // Split the partition: allocate part of it and leave the remaining as a new free block
            Partition newPartition = new Partition(selectedPartition.start + processSize, selectedPartition.size - processSize);
            partitions.add(worstFitIndex + 1, newPartition);
        }

        // Update the allocated partitions size and status
        selectedPartition.size = processSize;
        selectedPartition.isAllocated = true;

    }

    //Frees the memory allocated to a process (processStart Starting address of the process to be freed)
    public void freeMemory(int processStart) {
        for (int i = 0; i < partitions.size(); i++) {
            Partition p = partitions.get(i);
            if (p.start == processStart && p.isAllocated) {
                System.out.println("\nFreeing partition starting at " + p.start + " with size " + p.size + " KB. ");
                p.isAllocated = false;

                // Merge adjacent free partitions
                mergeFreePartitions();
                return;
            }
        }
        System.out.println("No allocated block found at start address " + processStart + ".");
    }

    //Merges adjacent free partitions to reduce fragmentation.
    private void mergeFreePartitions() {
        for (int i = 0; i < partitions.size() - 1; i++) {
            Partition current = partitions.get(i);
            Partition next = partitions.get(i + 1);

            if (!current.isAllocated && !next.isAllocated) {
                // Merge the current and next partition
                current.size += next.size;
                partitions.remove(i + 1);
                i--; // Recheck from the merged partition
            }
        }
    }

    //Displays the current status of memory partitions
    public void displayPartitionStatus() {
        System.out.println("<<<Current Partition Status>>> ");
        for (Partition p : partitions) {
            System.out.println("Partiton  [Start: " + p.start + " , Size: " + p.size + " KB " + (p.isAllocated ? "(Allocated)" : "(Free)") + "]");
        }
    }


    public static void main(String[] args) {

        // Create an instance of the WorstFit allocator(1000 KB total memory)
        WorstFit allocate = new WorstFit(1000);

        //Initial memory Status
        allocate.displayPartitionStatus();

        // Allocate processes and display the state after each allocation
        allocate.processAllocation("A", 400); // Process A: 400 KB
        allocate.displayPartitionStatus();

        allocate.processAllocation("B", 250); // Process B: 250 KB
        allocate.displayPartitionStatus();

        allocate.processAllocation("C", 200); // Process C: 200 KB
        allocate.displayPartitionStatus();

        allocate.freeMemory(0); // Free Process A (starts at 0)
        allocate.displayPartitionStatus();

        allocate.processAllocation("D", 230); // Process D: 230 KB
        allocate.displayPartitionStatus();

        allocate.processAllocation("E", 300); // Process E: 300 KB
        allocate.displayPartitionStatus();

        allocate.freeMemory(400); // Free Process B (starts at 400)
        allocate.displayPartitionStatus();

        allocate.processAllocation("F", 220); // Process E: 300 KB
        allocate.displayPartitionStatus();

    }
}