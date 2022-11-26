package Allocator;

public class Main {

    public static void main(String[] args) {
        /* Tests */
        int amount1 = 9;
        int amount2 = 9;
        int amount3 = 9;

        Long address1 = Allocator.instance.allocate(amount1);
        System.out.println("Allocated " + amount1 + " bytes at " + address1);
        Long address2 = Allocator.instance.allocate(amount2);
        System.out.println("Allocated " + amount2 + " bytes at " + address2);
        Long address3 = Allocator.instance.allocate(amount3);
        System.out.println("Allocated " + amount3 + " bytes at " + address3);

        System.out.println(Allocator.instance.isAccessible(address1, amount1));
        System.out.println(Allocator.instance.isAccessible(address2, amount2));
        System.out.println(Allocator.instance.isAccessible(address3, amount3));

        address3 = Allocator.instance.reAllocate(address3, amount3);
        System.out.println("Reallocated " + amount3 + " bytes at " + address3);
        address2 = Allocator.instance.reAllocate(address2, amount2);
        System.out.println("Reallocated " + amount2 + " bytes at " + address2);
        address1 = Allocator.instance.reAllocate(address1, amount1);
        System.out.println("Reallocated " + amount1 + " bytes at " + address1);

        System.out.println(Allocator.instance.isAccessible(address1, amount1));
        System.out.println(Allocator.instance.isAccessible(address2, amount2));
        System.out.println(Allocator.instance.isAccessible(address3, amount3));

        Allocator.instance.free(address1);
        System.out.println("Freed " + amount1 + " bytes at " + address1);
        Allocator.instance.free(address2);
        System.out.println("Freed " + amount2 + " bytes at " + address2);
        Allocator.instance.free(address3);
        System.out.println("Freed " + amount3 + " bytes at " + address3);
    }

}