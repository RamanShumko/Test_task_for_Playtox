package com.raman.global;

import lombok.extern.log4j.Log4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Log4j
public class Main {
    private static final int NUM_ACCOUNTS = 4;
    private static final int INITIAL_BALANCE = 10000;
    private static final int NUM_TRANSACTIONS = 30;
    private static final int NUM_THREADS = 5;
    private static final List<Account> accounts = new ArrayList<>();
    private static final Random random = new Random();

    public static void main(String[] args) {
        createAccounts();
        executeTransactions();
    }

    private static void executeTransactions() {
        ExecutorService executor = Executors.newFixedThreadPool(NUM_THREADS);

        for (int i = 0; i < NUM_TRANSACTIONS; i++) {
            Account sender = accounts.get(random.nextInt(NUM_ACCOUNTS));
            Account recipient = accounts.get(random.nextInt(NUM_ACCOUNTS));

            while (sender.getId().equals(recipient.getId()))
                recipient = accounts.get(random.nextInt(NUM_ACCOUNTS));
            Account finalRecipient = recipient;

            executor.submit(() -> {
                try {
                    Thread.sleep(random.nextInt(1001) + 1000);
                    int amount = random.nextInt(1001) + 1;
                    sender.transferMoney(finalRecipient, amount);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    log.error("Transaction thread interrupted.", e);
                }
            });
        }

        executor.shutdown();

        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.MINUTES);
            log.info("All transactions completed. Exiting application.");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Error waiting for executor to terminate.", e);
        }
    }

    private static void createAccounts() {
        for (int i = 0; i < NUM_ACCOUNTS; i++) {
            Account account = new Account(UUID.randomUUID().toString(), INITIAL_BALANCE);
            accounts.add(account);
            log.info("Initialized account " + account.getId());
        }
    }
}