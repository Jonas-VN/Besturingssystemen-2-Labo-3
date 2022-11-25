package Allocator;

import java.util.ArrayList;
import java.util.List;

public class Arena {
    private final List<Block> blocks = new ArrayList<>();
    private final int blockSize;
    private final int pageSize;

    public Arena(int blockSize){
        this.blockSize = blockSize;
        this.pageSize = blockSize;
    }

    public Arena(int blockSize, int pageSize){
        this.blockSize = blockSize;
        this.pageSize = pageSize;
    }

    public Long getPage() {
        for (Block block : blocks)
            if (block.hasFreePages())
                return block.getPage();

        // Geen plaats meer -> nieuwe toevoegen
        Long ret = BackingStore.getInstance().mmap(blockSize);
        Block block = new Block(ret, pageSize, blockSize);
        blocks.add(block);
        return block.getPage();
    }

    public void freePage(Long address) throws AllocatorException {
        for (Block block : blocks) {
            if (block.isAccessible(address)) {
                // block.freePage(address) returns true als het block leeg is -> block verwijderen
                if (block.freePage(address)) {
                    blocks.remove(block);
                    BackingStore.getInstance().munmap(block.getStartAddress(), block.getBlockSize());
                }
                return;
            }
        }
        throw new AllocatorException("Page not present in arena");
    }

    public boolean isAccessible(Long address) {
        for (Block block : blocks)
            if (block.isAccessible(address))
                return true;
        return false;
    }
}

