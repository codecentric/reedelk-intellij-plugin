package com.reedelk.plugin.executor;

import com.intellij.util.concurrency.AppExecutorUtil;
import com.intellij.util.concurrency.SequentialTaskExecutor;

import java.util.concurrent.ExecutorService;

/**
 * A global plugin executor used to run all the background tasks
 * in this plugin. This executor is single threaded.
 */
public class PluginExecutors {

    private static final ExecutorService executor =
            SequentialTaskExecutor.createSequentialApplicationPoolExecutor("Reedelk Plugin Sequential Executor");

    private static final ExecutorService parallelExecutor =
            AppExecutorUtil.createBoundedApplicationPoolExecutor("Reedelk Plugin Parallel Executor", 3);


    public static ExecutorService sequential() {
        return executor;
    }

    public static ExecutorService parallel() {
        return parallelExecutor;
    }
}
