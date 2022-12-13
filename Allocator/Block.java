package Allocator;

import java.util.concurrent.atomic.AtomicIntegerArray;


public class Block {
    private final AtomicIntegerArray allocatedPages;
    private final Long startAddress;
    private final int pageSize;
    private final int blockSize;

    public Block(Long startAddress, int pageSize, int blockSize) {
        this.allocatedPages = new AtomicIntegerArray(blockSize / pageSize);
        this.startAddress = startAddress;
        this.pageSize = pageSize;
        this.blockSize = blockSize;
    }

    public Long allocate() {
        for (int pageIndex = 0; pageIndex < blockSize / pageSize; pageIndex++) {
            if (allocatedPages.get(pageIndex) == 0) {
                allocatedPages.set(pageIndex, 1);
                return startAddress + (long) pageIndex * pageSize;
            }
        }
        return -1L;
    }

    public void free(Long address) {
        address -= startAddress;
        int pageIndex = (int) Math.floor((double) address / pageSize);
        allocatedPages.set(pageIndex, 0);
    }

    public boolean hasFreePages(){
        for (int pageIndex = 0; pageIndex < blockSize / pageSize; pageIndex++) {
            if (allocatedPages.get(pageIndex) == 0) {
                return true;
            }
        }
        return false;
    }

    public boolean isAccessible(Long address) {
        return isAccessible(address, 1);
    }

    public boolean isAccessible(Long address, int size) {
        address -= startAddress;

        if (address < 0 || (address + size) > blockSize)
            return false;

        int pageIndexStart = (int) Math.floor((double) address / pageSize);
        int pageIndexEnd = (int) Math.floor((address + size - 1.0) / pageSize);

        if (pageIndexStart != pageIndexEnd)
            return false;
        return allocatedPages.get(pageIndexStart) == 1;
    }
}