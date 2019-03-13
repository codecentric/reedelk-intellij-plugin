package com.esb.plugin.runner;

import com.esb.plugin.runconfig.module.ESBModuleRunConfiguration;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.ExecutionResult;
import com.intellij.execution.configurations.RunProfile;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.executors.DefaultRunExecutor;
import com.intellij.execution.runners.DefaultProgramRunnerKt;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.runners.GenericProgramRunner;
import com.intellij.execution.ui.RunContentDescriptor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ESBDeploymentRunner extends GenericProgramRunner {
    @NotNull
    @Override
    public String getRunnerId() {
        return "ESBDeploymentRunner";
    }

    @Override
    public boolean canRun(@NotNull String executorId, @NotNull RunProfile profile) {
        return executorId.equals(DefaultRunExecutor.EXECUTOR_ID) &&
                profile instanceof ESBModuleRunConfiguration;
    }

    @Nullable
    @Override
    protected RunContentDescriptor doExecute(@NotNull RunProfileState state, @NotNull ExecutionEnvironment environment) throws ExecutionException {
        RunProfile runProfile = environment.getRunProfile();



        ExecutionResult result = state.execute(environment.getExecutor(), this);
        return DefaultProgramRunnerKt.showRunContent(result, environment);
    }
}
