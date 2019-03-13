package com.esb.plugin.runconfig.module;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.ExecutionResult;
import com.intellij.execution.ExecutionTarget;
import com.intellij.execution.Executor;
import com.intellij.execution.configuration.EmptyRunProfileState;
import com.intellij.execution.configurations.*;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.runners.ProgramRunner;
import com.intellij.execution.runners.RunConfigurationWithSuppressedDefaultRunAction;
import com.intellij.execution.ui.ExecutionConsole;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.openapi.vfs.VirtualFileSystem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ESBModuleRunConfiguration extends RunConfigurationBase implements RunConfigurationWithSuppressedDefaultRunAction {

    private String runtimePath;
    private String moduleName;

    protected ESBModuleRunConfiguration(@NotNull Project project, @Nullable ConfigurationFactory factory, @Nullable String name) {
        super(project, factory, name);
    }

    @NotNull
    @Override
    public SettingsEditor<? extends RunConfiguration> getConfigurationEditor() {
        return new ESBModuleRunConfigurationSettings(getProject());
    }

    @Override
    public boolean canRunOn(@NotNull ExecutionTarget target) {
        return true;
    }

    @Nullable
    @Override
    public RunProfileState getState(@NotNull Executor executor, @NotNull ExecutionEnvironment environment) throws ExecutionException {
        // If executor is undeploy, then undeploy, otherwise deploy
        return (executor1, runner) -> {

            System.out.println("Executing");
            return null;
        };
    }

    public String getRuntimePath() {
        return runtimePath;
    }

    public void setRuntimePath(String runtimePath) {
        this.runtimePath = runtimePath;
    }

    public void setModule(String moduleName) {
        this.moduleName = moduleName;
    }

    public String getModule() {
        return moduleName;
    }
}
