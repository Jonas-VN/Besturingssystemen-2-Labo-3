package Allocator;

import java.util.LinkedList;

public class Arena {
    // list of blocks
    private LinkedList<Block> memoryBlocks;

    private BackingStore backingStore;

    // size of the blocks in de arena
    private int blockSize;

    // size of the pages in the blocks of the arena
    private int pageSize;


    public Arena(int blockSize){
        this.blockSize = blockSize;
        this.pageSize = blockSize;

        memoryBlocks = new LinkedList<>();
        backingStore = BackingStore.getInstance();
    }

    public Arena(int blockSize, int pageSize){
        this.blockSize = blockSize;
        this.pageSize = pageSize;

        memoryBlocks = new LinkedList<>();
        backingStore = BackingStore.getInstance();
    }

    public Long getPage() {
        for (Block block : memoryBlocks) {
            if (block.hasFreePages()) {
                //System.out.println("Could use block with size " + block.getBlockSize());
                return block.getPage();
            }
        }

        Long ret = backingStore.mmap(blockSize);
        Block block = new Block(ret, pageSize, blockSize);
        memoryBlocks.add(block);
        //System.out.println("Requested new memeory from OS with blockSize " + blockSize + " and pageSize " + pageSize + " at address " + ret);
        return block.getPage();
    }

    public void freePage(Long address) throws AllocatorException {
        for (Block block : memoryBlocks){
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
        for (Block block : memoryBlocks){
            if (block.isAccessible(address))
                return true;
        }
        return false;
    }
}

