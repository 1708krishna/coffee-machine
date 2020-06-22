package com.example.coffeemachine.service;


import com.example.coffeemachine.exceptions.NotAllowedException;


public class OutletService {
    private Integer locks;
    private Integer maxLocks;
    private Object mutex = new Object();

    public OutletService(Integer numberOfLocks) {
        this.locks = numberOfLocks;
        this.maxLocks = numberOfLocks;
    }

    public void lockOutlet() {
        while (true) {
            if (locks > 0) {
                synchronized (mutex) {
                    if (locks > 0) {
                        locks--;
                        return;
                    }
                }
            }
        }
    }

    public void UnlockOutlet() {
        if (!locks.equals(maxLocks)) {
            synchronized (mutex) {
                if (!locks.equals(maxLocks)) {
                    locks++;
                    return;
                } else {
                    throw new NotAllowedException("Reached max locks");
                }
            }
        } else {
            throw new NotAllowedException("Reached max locks");
        }
    }
}
