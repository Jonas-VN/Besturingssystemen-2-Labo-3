package Allocator;

import java.util.concurrent.ConcurrentLinkedQueue;

public class Arena {
    private final ConcurrentLinkedQueue<Block> blocks;
    private final int blockSize;
    private final int pageSize;

    public Arena(int blockSize){
        this.blockSize = blockSize;
        this.pageSize = blockSize;
        this.blocks = new ConcurrentLinkedQueue<>();
    }

    public Arena(int blockSize, int pageSize){
        this.blockSize = blockSize;
        this.pageSize = pageSize;
        this.blocks = new ConcurrentLinkedQueue<>();
    }

    public int getPageSize() {
        return pageSize;
    }

    public Long allocate() {
        for (Block block : blocks) {
            if (block.hasFreePages()) {
                return block.allocate();
            }
        }
        // Alle blocken zijn vol -> nieuwe maken
        Long ret = OperatingSystem.getInstance().mmap(blockSize);
        Block block = new Block(ret, pageSize, blockSize);
        blocks.add(block);
        return block.allocate();
    }

    public void free(Long address) {
        for (Block block : blocks) {
            if (block.isAccessible(address)) {
                block.free(address);
                return;
            }
        }
    }

    public boolean isAccessible(Long address) {
        return isAccessible(address, 1);
    }

    public boolean isAccessible(Long address, int size) {
        for (Block block : blocks) {
            if (block.isAccessible(address, size))
                return true;
        }
        return false;
    }
}