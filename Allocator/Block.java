package Allocator;

import java.util.BitSet;

public class Block {
    private final BitSet pages = new BitSet();

    private final Long startAddress;
    private final int pageSize;
    private final int blockSize;

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
        for (int offset = 0; offset < blockSize; offset += pageSize) {
            int pageIndex = offset / pageSize;
            if (!pages.get(pageIndex)) {
                pages.set(pageIndex);
                return startAddress + offset;
            }
        }
        throw new AllocatorException("No free pages in block");
    }

    public boolean freePage(Long address) throws AllocatorException {
        Long virtualAddress = address - startAddress;

        int pageIndex = (int) Math.floor(virtualAddress / pageSize);
        pages.set(pageIndex, false);

        return pages.isEmpty();
    }

    public boolean hasFreePages(){
        for (int index = 0; index < blockSize / pageSize; index++)
            if (!pages.get(index))
                return true;
        return false;
    }

    public boolean isAccessible(Long address) {
        Long virtualAddress = address - startAddress;

        if (virtualAddress < 0)
            return false;

        int pageIndex = (int) Math.floor(virtualAddress / pageSize);
        return pages.get(pageIndex);
    }
}