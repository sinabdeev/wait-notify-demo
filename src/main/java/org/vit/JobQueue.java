package org.vit;

import java.util.ArrayList;
import java.util.List;

public class JobQueue {

    List<Runnable> jobs = new ArrayList<>();

    public synchronized void put(Runnable job) {
        jobs.add(job);
        this.notifyAll();
    }

    public synchronized Runnable getJob() throws InterruptedException {
        while (jobs.isEmpty()) {
            this.wait();
        }

        return jobs.removeFirst();
    }
}