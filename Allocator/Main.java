package Allocator;

public class Main {

    public static void main(String[] args) {
        /* Tests */
        int amount1 = 2001;
        int amount2 = 2002;
        int amount3 = 2003;

        Long address1 = Allocator.instance.allocate(amount1);
        System.out.println("Allocated " + amount1 + " bytes at " + address1);
        Long address2 = Allocator.instance.allocate(amount2);
        System.out.println("Allocated " + amount2 + " bytes at " + address2);
        Long address3 = Allocator.instance.allocate(amount3);
        System.out.println("Allocated " + amount3 + " bytes at " + address3);

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