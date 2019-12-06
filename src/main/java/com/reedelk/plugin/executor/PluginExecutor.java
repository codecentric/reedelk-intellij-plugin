package com.reedelk.plugin.executor;

import com.intellij.util.concurrency.SequentialTaskExecutor;

import java.util.concurrent.ExecutorService;

/**
 * A global plugin executor used to run all the background tasks
 * in this plugin. This executor is single threaded.
 */
public class PluginExecutor {

    private static final ExecutorService executor =
            SequentialTaskExecutor.createSequentialApplicationPoolExecutor("Reedelk Plugin Executor");

    public static ExecutorService getInstance() {
        return executor;
    }
}
