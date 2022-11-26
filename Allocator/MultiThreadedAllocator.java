package Allocator;

import java.util.concurrent.ConcurrentHashMap;

public class MultiThreadedAllocator implements Allocator {
    private ConcurrentHashMap<Long, SingleThreadedAllocatorImplementation> allocators = new ConcurrentHashMap<>();

    @Override
    public Long allocate(int size) {
        return getAllocator().allocate(size);
    }

    @Override
    public void free(Long address) {
        getAllocator().free(address);
    }

    @Override
    public Long reAllocate(Long oldAddress, int newSize) {
        return getAllocator().reAllocate(oldAddress, newSize);
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
        long id = Thread.currentThread().getId();
        if (!allocators.containsKey(id))
            allocators.put(id, new SingleThreadedAllocatorImplementation());

        return allocators.get(id);
    }
}
