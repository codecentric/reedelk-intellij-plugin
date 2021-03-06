package de.codecentric.reedelk.plugin.executor;

import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.util.concurrency.AppExecutorUtil;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ExecutorService;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

/**
 * A global plugin executor used to run all the background tasks
 * in this plugin. This executor is single threaded.
 */
public class PluginExecutors {

    private PluginExecutors() {
    }

    // There is only one executor across application to deserialize flows
    private static ExecutorService deserializerExecutor =
            AppExecutorUtil.createBoundedApplicationPoolExecutor("Reedelk Deserializer", 1);

    public static void run(@NotNull Module module, @NotNull String indicatorText, @NotNull AsyncProgressTask task) {
        ProgressManager.getInstance().run(new AsyncProgressTaskExecutor(module, indicatorText, task));
    }

    public static void runWithDelay(@NotNull Module module,
                                    long delayMillis,
                                    @NotNull String indicatorText,
                                    @NotNull AsyncProgressTask task) {
        AppExecutorUtil.getAppScheduledExecutorService().schedule(() ->
                ProgressManager.getInstance().run(new AsyncProgressTaskExecutor(module, indicatorText, task)),
                delayMillis, MILLISECONDS);
    }

    public static void runSmartReadAction(@NotNull Module module, @NotNull Runnable task) {
        runSmartReadAction(module.getProject(), task);
    }

    public static void runSmartReadAction(@NotNull Project project, @NotNull Runnable task) {
        ReadAction.nonBlocking(task)
                .inSmartMode(project)
                .submit(AppExecutorUtil.getAppExecutorService());
    }

    public static void onDeserializeExecutor(Runnable runnable) {
        deserializerExecutor.submit(runnable);
    }
}
