package Allocator;

import java.util.*;

public class SingleThreadedAllocator implements Allocator {
    private final HashMap<Integer, Arena> pageSizes;
    private static final double LOG_2 = Math.log(2); // niet nodig om telkens opnieuw te berekenen

    // bit shifts zouden sneller moeten zijn ipv Math.pow(2, exp)
    private static int pow2(int exp) {
        return 1 << exp; // -> 2^exp
    }

    // Find the best fitted size (e.g. 3015 -> 4096, 17 -> 32, ...)
    public static int bestFitted(int size){
        return pow2((int) Math.ceil(Math.log(size) / LOG_2));
    }

    public SingleThreadedAllocator() {
        this.pageSizes = new HashMap<>();

        for (int i = 3; i < 13; i++) {
            int pageSize = pow2(i);
            pageSizes.put(pageSize, new Arena(4096, pageSize));
        }
    }

    public Long allocate(int size) {
        int bestSize = bestFitted(size);
        synchronized (pageSizes) {
            if (!pageSizes.containsKey(bestSize))
                pageSizes.put(bestSize, new Arena(bestSize));
            return pageSizes.get(bestSize).getPage();
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
        throw new AllocatorException("Address not available in thread " + Thread.currentThread().getId());
    }

    public Long reAllocate(Long oldAddress, int newSize) {
        free(oldAddress);
        return allocate(newSize);
    }

    public boolean isAccessible(Long address) {
        synchronized (pageSizes) {
            for (Arena arena : pageSizes.values())
                if (arena.isAccessible(address))
                    return true;
        }
        return false;
    }

    public boolean isAccessible(Long address, int size) {
        synchronized (pageSizes) {
            Arena arena = pageSizes.get(bestFitted(size));
            return arena.isAccessible(address);
        }
    }
}