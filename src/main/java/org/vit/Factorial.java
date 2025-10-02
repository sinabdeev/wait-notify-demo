package org.vit;

public class Factorial implements Runnable {

    private final int n;

    public Factorial(int n) {
        IO.println("Factorial: " + n);
        this.n = n;
    }

    @Override
    public void run() {
        long result = 1;
        try {
            Thread.sleep(10_000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        for (int i = 2; i <= n; i++) {
            result *= i;
        }
        IO.println(String.format("Factorial: %d. Done.  Result: %d", n, result));
    }

}
