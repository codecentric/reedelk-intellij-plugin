package com.reedelk.plugin.executor;

import com.intellij.openapi.progress.ProgressIndicator;
import org.jetbrains.annotations.NotNull;

public interface AsyncProgressTask {

    void run(@NotNull ProgressIndicator indicator);

    default void onFinished() {}

    default void onThrowable(Throwable error) {}

    default void onSuccess() {}

    default void onCancel() {}
}
