package com.reedelk.plugin.executor;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import org.jetbrains.annotations.NotNull;

public class AsyncProgressTaskExecutor extends Task.Backgroundable {

    private final AsyncProgressTask executor;

    public AsyncProgressTaskExecutor(@NotNull Module module, @NotNull String indicatorTitle, @NotNull AsyncProgressTask executor) {
        super(module.getProject(), indicatorTitle, true);
        this.executor = executor;
    }

    @Override
    public void run(@NotNull ProgressIndicator indicator) {
        indicator.setIndeterminate(true);
        this.executor.run(indicator);
    }

    @Override
    public void onCancel() {
        this.executor.onCancel();
    }

    @Override
    public void onSuccess() {
        this.executor.onSuccess();
    }

    @Override
    public void onThrowable(@NotNull Throwable error) {
        this.executor.onThrowable(error);
    }

    @Override
    public void onFinished() {
        this.executor.onFinished();
    }
}
