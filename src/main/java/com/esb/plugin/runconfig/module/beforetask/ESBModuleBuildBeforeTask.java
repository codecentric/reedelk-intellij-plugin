package com.esb.plugin.runconfig.module.beforetask;

import com.intellij.execution.BeforeRunTask;
import com.intellij.openapi.util.Key;
import org.jetbrains.annotations.NotNull;

public class ESBModuleBuildBeforeTask extends BeforeRunTask<ESBModuleBuildBeforeTask> {

    protected ESBModuleBuildBeforeTask(@NotNull Key<ESBModuleBuildBeforeTask> providerId) {
        super(providerId);
    }
}
