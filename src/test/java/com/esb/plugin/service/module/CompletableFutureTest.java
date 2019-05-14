package com.esb.plugin.service.module;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class CompletableFutureTest {

    private final Random random = new Random();

    private List<Integer> returned = new ArrayList<>();

    @Test
    void shouldDoSomething() throws ExecutionException, InterruptedException {
        List<CompletableFuture<Integer>> futures = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            CompletableFuture<Integer> completableFuture = scanComponent(i);
            futures.add(completableFuture);
        }

        CompletableFuture<Void> complete = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .whenComplete((aVoid, throwable) -> System.out.println("All Completed: " + returned));
        complete.get();

    }

    private CompletableFuture<Integer> scanComponent(final int value) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(random.nextInt(10) * 100);
                returned.add(value);
                System.out.println(String.format("Running %d , %s", value, Thread.currentThread().getName()));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return value;
        });
    }
}
