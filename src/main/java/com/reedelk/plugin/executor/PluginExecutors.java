package com.reedelk.plugin.executor;

import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.util.concurrency.AppExecutorUtil;
import org.jetbrains.annotations.NotNull;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

/**
 * A global plugin executor used to run all the background tasks
 * in this plugin. This executor is single threaded.
 */
public class PluginExecutors {

    public static void run(@NotNull Module module, @NotNull AsyncProgressTask task) {
        ProgressManager.getInstance().run(new AsyncProgressTaskExecutor(module, task));
    }

    public static void runWithDelay(@NotNull Module module, long delayMillis, @NotNull AsyncProgressTask task) {
        AppExecutorUtil.getAppScheduledExecutorService().schedule(() ->
                ProgressManager.getInstance().run(new AsyncProgressTaskExecutor(module, task)), delayMillis, MILLISECONDS);
    }

    public static void runSmartReadAction(@NotNull Module module, @NotNull Runnable task) {
        ReadAction.nonBlocking(task)
                .inSmartMode(module.getProject())
                .submit(AppExecutorUtil.getAppExecutorService());

    }
}
