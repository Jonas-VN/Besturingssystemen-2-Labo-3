package Allocator;

import java.util.concurrent.ConcurrentHashMap;


public class MultiThreadedAllocator implements Allocator {
    private final ConcurrentHashMap<Long, SingleThreadedAllocator> allocators;

    public MultiThreadedAllocator() {
        allocators = new ConcurrentHashMap<>();
    }

    public SingleThreadedAllocator getAllocator() {
        if (allocators.containsKey(Thread.currentThread().getId())) return allocators.get(Thread.currentThread().getId());

        SingleThreadedAllocator allocator = new SingleThreadedAllocator();
        allocators.put(Thread.currentThread().getId(), allocator);
        return allocator;
    }

    public Long allocate(int size) {
        SingleThreadedAllocator allocator = getAllocator();
        return allocator.allocate(size);
    }

    public void free(Long address) {
        for (SingleThreadedAllocator allocator : allocators.values()) {
            if (allocator.isAccessible(address)) {
                allocator.free(address);
                break;
            }
        }
    }

    public Long reAllocate(Long oldAddress, int newSize) {
        for (SingleThreadedAllocator allocator : allocators.values()) {
            if (allocator.isAccessible(oldAddress)) {
                allocator.free(oldAddress);
                break;
            }
        }
        // Reallocate to current thread
        SingleThreadedAllocator allocator = getAllocator();
        return allocator.allocate(newSize);

    }

    public boolean isAccessible(Long address) {
        return isAccessible(address, 1);
    }

    public boolean isAccessible(Long address, int size) {
        for (SingleThreadedAllocator allocator : allocators.values()) {
            if (allocator.isAccessible(address)) {
                return allocator.isAccessible(address, size);
            }
        }
        return false;
    }
}