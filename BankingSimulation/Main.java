package BankingSimulation;

import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        Bank bank = new Bank();
        ArrayList<Transferer> transferers = new ArrayList<>();
        for (int i = 0; i < 1; i++)
            transferers.add(new Transferer(bank));
        for (Transferer transferer : transferers)
            transferer.start();
    }
}
