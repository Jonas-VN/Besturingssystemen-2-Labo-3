package Allocator;

import java.util.concurrent.ConcurrentHashMap;

public class MultiThreadedAllocator implements Allocator {
    private ConcurrentHashMap<Long, Allocator> allocators = new ConcurrentHashMap<>();

    @Override
    public Long allocate(int size) {
        return getAllocator().allocate(size);
    }

    @Override
    public void free(Long address) {
//        int i = 0;
        for (Allocator allocator : allocators.values()) {
            try {
                allocator.free(address);
//                System.out.println("Free after " + i + " tries with thread " + Thread.currentThread().getId());
                return;
            } catch (AllocatorException ae) {
//                i++;
            }
        }
    }

    @Override
    public Long reAllocate(Long oldAddress, int newSize) {
        free(oldAddress);
        return getAllocator().allocate(newSize);
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
