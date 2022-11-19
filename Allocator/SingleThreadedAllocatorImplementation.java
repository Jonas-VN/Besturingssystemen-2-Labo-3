package Allocator;

import java.util.*;

public class SingleThreadedAllocatorImplementation implements Allocator {
    private HashMap<Integer, Arena> pageSizes;

    // Find the best fitted size (e.g. 3015 -> 4096, 17 -> 32, ...)
    public static int roundUp(int size){
        return (int) Math.pow(2, Math.ceil(Math.log(size) / Math.log(2)));
    }

    public SingleThreadedAllocatorImplementation() {
        this.pageSizes = new HashMap<>();

        for (int i = 0; i < 13; i++) {
            int pageSize = (int) Math.pow(2, i);
            pageSizes.put(pageSize, new Arena(Block.UNIT_BLOCK_SIZE, pageSize));
        }
    }

    public Long allocate(int size) {
        int roundedSize = roundUp(size);
        if (!pageSizes.containsKey(roundedSize)) {
            pageSizes.put(roundedSize, new Arena(roundedSize));
        }
        return pageSizes.get(roundedSize).getPage();
    }

    private Arena getLocation(Long address) {
        for (Arena arena : pageSizes.values()) {
            if (arena.isAccessible(address))
                return arena;
        }
        return null;
    }

    public void free(Long address) {
        Arena arena = getLocation(address);
        if (arena != null)
            arena.freePage(address);
    }

    public Long reAllocate(Long oldAddress, int newSize) {
        free(oldAddress);
        return allocate(newSize);
    }

    public boolean isAccessible(Long address) {
        for (Integer entry : pageSizes.keySet()) {
            if (pageSizes.get(entry).isAccessible(address)) {
                return true;
            }
        }
        return false;
    }

    public boolean isAccessible(Long address, int size) {
        Arena arena = pageSizes.get(roundUp(size));
        return arena.isAccessible(address);
    }
}