package BankingSimulation;

public class Logger extends Thread {
    private Bank bank;

    public Logger (Bank bank) {
        this.bank = bank;
    }

    @Override
    public void run() {
        long oldNumTransactionsProcessed = 0;
        long oldNumTransactionsInserted = 0;
        long oldTime = System.currentTimeMillis();
        while (true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            long numTransactionsProcessed = bank.getNumTransactionsProcessed();
            long numTransactionsInserted = bank.getNumTransactionsInserted();
            long sleepTime = System.currentTimeMillis() - oldTime;
            long change = numTransactionsProcessed - oldNumTransactionsProcessed;
            System.out.println("Bank is processing " + ((float)change)/(sleepTime/1000) + " transactions/s.");
            System.out.println("\tNumber of transactions still in-flight: " + (numTransactionsInserted - numTransactionsProcessed) + ".");
            change = numTransactionsInserted - oldNumTransactionsInserted;
            System.out.println("\tBank is receiving " +  ((float)change)/(sleepTime/1000)  + " transactions/s.");
            System.out.println("\tAverage allocation size: " + Transaction.totalSize.get()/Transaction.numTransactions.get());
            System.out.println("\t% large allocations : " + (100f*Transaction.numTransactionsBiggerThan2Pages.get())/((float)Transaction.numTransactions.get()) + "%");
            oldNumTransactionsProcessed = numTransactionsProcessed;
            oldNumTransactionsInserted = numTransactionsInserted;
            oldTime = System.currentTimeMillis();
        }
    }
}
