package Allocator;

import java.util.LinkedList;

public class Arena {
    private LinkedList<Block> memoryBlocks = new LinkedList<>();
    private BackingStore backingStore = BackingStore.getInstance();;

    // size of the blocks in de arena
    private int blockSize;

    // size of the pages in the blocks of the arena
    private int pageSize;

    public Arena(int blockSize){
        this.blockSize = blockSize;
        this.pageSize = blockSize;
    }

    public Arena(int blockSize, int pageSize){
        this.blockSize = blockSize;
        this.pageSize = pageSize;
    }

    public Long getPage() {
        for (Block block : memoryBlocks)
            if (block.hasFreePages())
                return block.getPage();

        Long ret = backingStore.mmap(blockSize);
        Block block = new Block(ret, pageSize, blockSize);
        memoryBlocks.add(block);
        return block.getPage();
    }

    public void freePage(Long address) throws AllocatorException {
        for (Block block : memoryBlocks) {
            if (block.isAccessible(address)) {
                try {
                    block.freePage(address);
                } catch (EmptyBlockException e) {
                    memoryBlocks.remove(block);
                    backingStore.munmap(block.getStartAddress(), block.getBlockSize());
                }
                return;
            }
        }
        throw new AllocatorException("Page not present in arena");
    }

    public boolean isAccessible(Long address) {
        for (Block block : memoryBlocks)
            if (block.isAccessible(address))
                return true;
        return false;
    }
}

