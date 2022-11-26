package Allocator;

import java.util.concurrent.ConcurrentHashMap;

public class MultiThreadedAllocator implements Allocator {
    private final ConcurrentHashMap<Long, Allocator> allocators = new ConcurrentHashMap<>();

    @Override
    public Long allocate(int size) {
        return getAllocator().allocate(size);
    }

    @Override
    public void free(Long address) {
        // Veeeel te traag, maar het werkt :)
        for (Allocator allocator : allocators.values()) {
            try {
                allocator.free(address);
                return;
            } catch (AllocatorException ignored) {
            }
        }
    }

    @Override
    public Long reAllocate(Long oldAddress, int newSize) {
        free(oldAddress);
        return allocate(newSize);
    }

    @Override
    public boolean isAccessible(Long address) {
        return getAllocator().isAccessible(address);
    }

    @Override
    public boolean isAccessible(Long address, int size) {
        return getAllocator().isAccessible(address, size);
    }

    private Allocator getAllocator(){
        Long id = Thread.currentThread().getId();
        if (!allocators.containsKey(id))
            allocators.put(id, new SingleThreadedAllocator());

        return allocators.get(id);
    }
}
