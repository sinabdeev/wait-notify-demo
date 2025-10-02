package org.vit;

public class ClientProducer implements Runnable {

    private final JobQueue queue;

    public ClientProducer(JobQueue queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        int x = (int)(Math.random() * 99);
        int n = 1 + x % 7;
        Factorial job = new Factorial(n);
        queue.put(job);
    }

}
