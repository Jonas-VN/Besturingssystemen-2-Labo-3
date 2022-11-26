package Allocator;


public class MultiThreadedLocalAllocator implements Allocator {
    private static ThreadLocal<Allocator> threadLocalAllocator = ThreadLocal.withInitial(() -> new SingleThreadedAllocatorImplementation());// new ThreadLocal<>();

    @Override
    public Long allocate(int size) {
        return threadLocalAllocator.get().allocate(size);
    }

    @Override
    public void free(Long address) {
        threadLocalAllocator.get().free(address);
    }

    @Override
    public Long reAllocate(Long oldAddress, int newSize) {
        return threadLocalAllocator.get().reAllocate(oldAddress, newSize);
    }

    @Override
    public boolean isAccessible(Long address) {
        return threadLocalAllocator.get().isAccessible(address);
    }

    @Override
    public boolean isAccessible(Long address, int size) {
        return threadLocalAllocator.get().isAccessible(address, size);
    }
}

