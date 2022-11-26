package Allocator;

import java.util.*;

public class SingleThreadedAllocatorImplementation implements Allocator {
    private final HashMap<Integer, Arena> pageSizes;
    private static final double LOG_2 = Math.log(2); // niet nodig om telkens opnieuw te berekenen

    // bit shifts zouden sneller moeten zijn ipv Math.pow(2, i)
    private static int pow2(int exp) {
        return 1 << exp; // -> 2^exp
    }

    // Find the best fitted size (e.g. 3015 -> 4096, 17 -> 32, ...)
    public static int roundUp(int size){
        return pow2((int) Math.ceil(Math.log(size) / LOG_2));
    }

    public SingleThreadedAllocatorImplementation() {
        this.pageSizes = new HashMap<>();

        for (int i = 3; i < 13; i++) {
            int pageSize = pow2(i);
            pageSizes.put(pageSize, new Arena(4096, pageSize));
        }
    }

    public Long allocate(int size) {
        int roundedSize = roundUp(size);
        synchronized (pageSizes) {
            if (!pageSizes.containsKey(roundedSize))
                pageSizes.put(roundedSize, new Arena(roundedSize));
            return pageSizes.get(roundedSize).getPage();
        }
    }

    public void free(Long address) throws AllocatorException {
        synchronized (pageSizes) {
            for (Arena arena : pageSizes.values()) {
                if (arena.isAccessible(address)) {
                    arena.freePage(address);
                    return;
                }
            }
        }
        throw new AllocatorException("Address not available in this thread " + Thread.currentThread().getId());
    }

    public Long reAllocate(Long oldAddress, int newSize) {
        free(oldAddress);
        return allocate(newSize);
    }

    public boolean isAccessible(Long address) {
        synchronized (pageSizes) {
            for (Integer entry : pageSizes.keySet())
                if (pageSizes.get(entry).isAccessible(address))
                    return true;
        }
        return false;
    }

    public boolean isAccessible(Long address, int size) {
        synchronized (pageSizes) {
            Arena arena = pageSizes.get(roundUp(size));
            return arena.isAccessible(address);
        }
    }
}