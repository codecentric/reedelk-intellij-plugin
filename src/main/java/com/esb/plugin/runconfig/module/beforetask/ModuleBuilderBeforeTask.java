package com.esb.plugin.runconfig.module.beforetask;

import com.intellij.execution.BeforeRunTask;
import com.intellij.openapi.util.Key;
import org.jetbrains.annotations.NotNull;

public class ModuleBuilderBeforeTask extends BeforeRunTask<ModuleBuilderBeforeTask> {

    protected ModuleBuilderBeforeTask(@NotNull Key<ModuleBuilderBeforeTask> providerId) {
        super(providerId);
    }
}
