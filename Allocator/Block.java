package Allocator;

import java.util.BitSet;

public class Block {
    public static final int UNIT_BLOCK_SIZE = 4096;

    private final Long startAddress;
    private final int pageSize;
    private final int blockSize;
    private final BitSet allocatedPages = new BitSet();

    public Block(Long startAddress, int pageSize, int blockSize) {
        this.startAddress = startAddress;
        this.pageSize = pageSize;
        this.blockSize = blockSize;
    }

    public Long getStartAddress() {
        return startAddress;
    }

    public int getBlockSize() {
        return blockSize;
    }

    public Long getPage() throws AllocatorException {
        for (int i = 0; i < blockSize; i += pageSize) {
            int pageIndex = i / pageSize;
            if (!allocatedPages.get(pageIndex)) {
                allocatedPages.set(pageIndex);
                return startAddress + i;
            }
        }
        throw new AllocatorException("No free pages in block");
    }

    public void freePage(Long address) throws AllocatorException, EmptyBlockException {
        // System.out.println(address + " is free!");
        Long relativeAddress = address - startAddress;

        if (relativeAddress < 0)
            throw new AllocatorException("Page not present in block");

        int pageIndex = (int) Math.floor(relativeAddress / pageSize);
        allocatedPages.set(pageIndex, false);

        if (allocatedPages.isEmpty())
            throw new EmptyBlockException("Block is empty");
    }

    public boolean hasFreePages(){
        for (int i = 0; i < blockSize / pageSize; i++)
            if (!allocatedPages.get(i))
                return true;
        return false;
    }

    public boolean isAccessible(Long address) {
        Long relativeAddress = address - startAddress;

        if (relativeAddress < 0)
            return false;

        int pageIndex = (int) Math.floor(relativeAddress / pageSize);
        return allocatedPages.get(pageIndex);
    }
}