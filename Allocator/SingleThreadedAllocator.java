package Allocator;

import java.lang.Math;
import java.util.concurrent.ConcurrentHashMap;

public class SingleThreadedAllocator implements Allocator {
    private final ConcurrentHashMap<Integer, Arena> arenas = new ConcurrentHashMap<>();
    private static final double LOG_2 = Math.log(2); // niet nodig om telkens opnieuw te berekenen

    // bit shifts zouden sneller moeten zijn ipv Math.pow(2, exp)
    private static int pow2(int exp) {
        return 1 << exp; // -> 2^exp
    }

    // De best passende grootte zoeken (bv. 3015 -> 4096, 17 -> 32, 8522 -> 16384...)
    public static int bestFitted(int size){
        return pow2((int) Math.ceil(Math.log(size) / LOG_2));
    }

    public Long allocate(int size) {
        int bestFittedSize = bestFitted(size);

        Arena arena;
        if (arenas.containsKey(bestFittedSize)) arena = arenas.get(bestFittedSize);
        else {
            if (size > 4096) arena = new Arena(bestFittedSize);
            else arena = new Arena(4096, bestFittedSize);
            arenas.put(bestFittedSize, arena);
        }
        return arena.allocate();
    }

    public void free(Long address) {
        for (Arena arena : arenas.values()) {
            if (arena.isAccessible(address)) {
                arena.free(address);
                return;
            }
        }
    }

    public Long reAllocate(Long oldAddress, int newSize) {
        for (Arena arena : arenas.values()) {
            if (arena.isAccessible(oldAddress)) {
                int oldSize = arena.getPageSize();
                if (oldSize >= newSize)
                    return oldAddress;
                free(oldAddress);
                return allocate(newSize);
            }
        }
        return -1L;
    }

    public boolean isAccessible(Long address) {
        return isAccessible(address, 1);
    }

    public boolean isAccessible(Long address, int size) {
        for (Arena arena : arenas.values()) {
            if (arena.isAccessible(address, size)) {
                return true;
            }
        }
        return false;
    }
}