package com.raman.global;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j;

import java.util.concurrent.locks.ReentrantLock;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Log4j
public class Account {
    private String id;
    private int money;
    private final ReentrantLock lock = new ReentrantLock();

    public void transferMoney(Account recipient, int amount) {
        try {
            this.lock.lock();
            log.info("Locking account " + this.getId());
            if (recipient.lock.tryLock()) {
                try {
                    log.info("Locking account " + recipient.getId());
                    if (this.money >= amount) {
                        this.money -= amount;
                        recipient.money += amount;
                        log.info("Transfer from account " + this.id + " to account " + recipient.getId() + " of amount " + amount + " completed.");
                    } else {
                        log.error("Insufficient funds in account " + this.id + " for transfer.");
                    }
                } finally {
                    recipient.lock.unlock();
                    log.info("Unlocking account " + recipient.getId());
                }
            } else
                log.error("Failed to acquire lock for account " + recipient.getId() + ". Unable to proceed with the transaction.");
        } finally {
            this.lock.unlock();
            log.info("Unlocking account " + this.getId());
        }
    }
}

