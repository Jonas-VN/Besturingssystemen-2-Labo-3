package Allocator;
import java.util.BitSet;

public class OperatingSystem {
    private static OperatingSystem os = new OperatingSystem();
    public static OperatingSystem getInstance() { return os; }

    private BitSet alloccedPages = new BitSet();

    private OperatingSystem () {}

    private void assert_true(boolean condition) {
        if (!condition)
            throw new AllocatorException();
    }

    private long numPages(long size) {
        if (size % 0x1000L == 0)
            return size/0x1000L;
        return size/0x1000L + 1;
    }

    private boolean isAllocated(Long address) {
        assert_true(address/0x1000L < 2147483647L);
        int idx = (int) (long) (address/0x1000L);
        if (alloccedPages.get(idx) == true)
            return true;
        return false;
    }

    private boolean isAllocated(Long start, long size) {
        assert_true(start % 0x1000L == 0);
        assert_true(start >= 0);
        assert_true(size > 0);

        for (Long i = start; i < start + size; i += 0x1000L)
            if (!isAllocated(i))
                return false;
        return true;
    }

    /* 
     * Returns a 4096-byte aligned chunk of memory that is at least as big as the requested size
     * In reality, the size is aligned up to the next multiple of 4096 (page size).
     */
    public synchronized Long mmap(long size) {
        Thread.yield();
        assert_true(size > 0);
        final long numPages = numPages(size);
        assert_true(numPages > 0);

        int searchStart = 0;
        while (true) {
            int earliestFree = alloccedPages.nextClearBit(searchStart);
            assert_true(earliestFree != -1);
            int until = alloccedPages.nextSetBit(earliestFree);
            if (until == -1 || (until - earliestFree) >= numPages) {
                for (int i = earliestFree; i < earliestFree + numPages; i++) {
                    alloccedPages.set(i, true);
                }
                long ret = earliestFree * 0x1000L;
                // System.out.println("Mapping region [" + ret + ", " + (ret + size) + "[");
                return ret;
            }
            searchStart = until;
        }
    }

    private void munmapPage(Long address) {
        assert_true(address % 0x1000L == 0);
        long bitIdx = address/0x1000L;
        assert_true(bitIdx < 2147483647);
        int idx = (int) bitIdx;
        alloccedPages.clear(idx);
        assert_true(!isAllocated(address));
    }

    /*
     * Unmaps the pages referred to by the region [`address`, `address + size`[. 
     * `address` must be 4096-byte aligned. The `size` argument does not have to be, but will
     *  be aligned up to the next multiple of 4096.
     * The unmapped pages may be contained in a longer region previously mapped at once
     *  using `mmap`. 
     * Trying to unmap pages that are not currently mmap'ed results in an AllocatorException.
     */
    public synchronized void munmap(Long address, long size) {
        Thread.yield();
        // System.out.println("Unmapping region [" + address + ", " + (address + size) + "[");
        assert_true(isAllocated(address, size));
        for (Long a = address;  a < address + size; a += 0x1000L)
            munmapPage(a);
    }
}
