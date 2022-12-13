package Allocator;

import java.util.Random;

public class Main {
    public static void main(String[] args) {
        SingleThreadedAllocator allocator = new SingleThreadedAllocator();
        Random random = new Random();

        int size1 = random.nextInt(99);
        int size2 = random.nextInt(999);
        int size3 = random.nextInt(9999);
        int size4 = random.nextInt(99999);
        int size5 = random.nextInt(999999);

        Long ret1 = allocator.allocate(size1);
        Long ret2 = allocator.allocate(size2);
        Long ret3 = allocator.allocate(size3);
        Long ret4 = allocator.allocate(size4);
        Long ret5 = allocator.allocate(size5);

        System.out.println(size1 + " bytes are" + (allocator.isAccessible(ret1, size1) ? "" : " NOT") + " allocated at address " + ret1);
        System.out.println(size2 + " bytes are" + (allocator.isAccessible(ret2, size2) ? "" : " NOT") + " allocated at address " + ret2);
        System.out.println(size3 + " bytes are" + (allocator.isAccessible(ret3, size3) ? "" : " NOT") + " allocated at address " + ret3);
        System.out.println(size4 + " bytes are" + (allocator.isAccessible(ret4, size4) ? "" : " NOT") + " allocated at address " + ret4);
        System.out.println(size5 + " bytes are" + (allocator.isAccessible(ret5, size5) ? "" : " NOT") + " allocated at address " + ret5);
        System.out.println();

        size1 = random.nextInt(100) > 50 ? size1*2 : size1/2;
        size2 = random.nextInt(100) > 50 ? size2*2 : size2/2;
        size3 = random.nextInt(100) > 50 ? size3*2 : size3/2;
        size4 = random.nextInt(100) > 50 ? size4*2 : size4/2;
        size5 = random.nextInt(100) > 50 ? size5*2 : size5/2;

        ret1 = allocator.reAllocate(ret1, size1);
        ret2 = allocator.reAllocate(ret2, size2);
        ret3 = allocator.reAllocate(ret3, size3);
        ret4 = allocator.reAllocate(ret4, size4);
        ret5 = allocator.reAllocate(ret5, size5);

        System.out.println(size1 + " bytes are" + (allocator.isAccessible(ret1, size1) ? "" : " NOT") + " reallocated at address " + ret1);
        System.out.println(size2 + " bytes are" + (allocator.isAccessible(ret2, size2) ? "" : " NOT") + " reallocated at address " + ret2);
        System.out.println(size3 + " bytes are" + (allocator.isAccessible(ret3, size3) ? "" : " NOT") + " reallocated at address " + ret3);
        System.out.println(size4 + " bytes are" + (allocator.isAccessible(ret4, size4) ? "" : " NOT") + " reallocated at address " + ret4);
        System.out.println(size5 + " bytes are" + (allocator.isAccessible(ret5, size5) ? "" : " NOT") + " reallocated at address " + ret5);
        System.out.println();

        allocator.free(ret1);
        allocator.free(ret2);
        allocator.free(ret3);
        allocator.free(ret4);
        allocator.free(ret5);

        System.out.println(size1 + " bytes are" + (allocator.isAccessible(ret1) ? " NOT" : "") + " freed at address " + ret1);
        System.out.println(size2 + " bytes are" + (allocator.isAccessible(ret2) ? " NOT" : "") + " freed at address " + ret2);
        System.out.println(size3 + " bytes are" + (allocator.isAccessible(ret3) ? " NOT" : "") + " freed at address " + ret3);
        System.out.println(size4 + " bytes are" + (allocator.isAccessible(ret4) ? " NOT" : "") + " freed at address " + ret4);
        System.out.println(size5 + " bytes are" + (allocator.isAccessible(ret5) ? " NOT" : "") + " freed at address " + ret5);
    }
}
