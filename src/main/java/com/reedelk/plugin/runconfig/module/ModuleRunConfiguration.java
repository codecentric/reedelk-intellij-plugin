package com.reedelk.plugin.runconfig.module;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.ExecutionTarget;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.*;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.runners.RunConfigurationWithSuppressedDefaultRunAction;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.JDOMExternalizerUtil;
import com.reedelk.plugin.runconfig.module.runner.ModuleDeployExecutor;
import com.reedelk.plugin.runconfig.module.runner.ModuleUnDeployExecutor;
import com.reedelk.plugin.runconfig.module.runprofile.DeployRunProfile;
import com.reedelk.plugin.runconfig.module.runprofile.UndeployRunProfile1;
import com.reedelk.plugin.service.project.PreferredRunConfigurationService;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ModuleRunConfiguration extends RunConfigurationBase implements
        RunConfigurationWithSuppressedDefaultRunAction,
        RunConfigurationWithSuppressedDefaultDebugAction {

    private static final String PREFIX = "ModuleRunConfiguration-";
    private static final String MODULE_NAME = PREFIX + "ModuleName";
    private static final String RUNTIME_CONFIG_NAME = PREFIX + "RuntimeConfigName";

    private String runtimeConfigName;
    private String moduleName;

    ModuleRunConfiguration(@NotNull Project project, @Nullable ConfigurationFactory factory, @Nullable String name) {
        super(project, factory, name);
    }

    @NotNull
    @Override
    public SettingsEditor<? extends RunConfiguration> getConfigurationEditor() {
        return new ModuleRunConfigurationSettings(getProject());
    }

    @Override
    public boolean canRunOn(@NotNull ExecutionTarget target) {
        return true;
    }

    @Nullable
    @Override
    public RunProfileState getState(@NotNull Executor executor, @NotNull ExecutionEnvironment environment) throws ExecutionException {
        Project project = getProject();

        // Deploy Executor
        if (ModuleDeployExecutor.EXECUTOR_ID.equals(executor.getId())) {
            PreferredRunConfigurationService.getInstance(project)
                    .setLastModuleRunConfiguration(getName());
            return new DeployRunProfile(project, moduleName, runtimeConfigName);

            // UnDeploy Executor
        } else if (ModuleUnDeployExecutor.EXECUTOR_ID.equals(executor.getId())) {
            return new UndeployRunProfile1(project, moduleName, runtimeConfigName);

        } else {
            throw new ExecutionException("Executor not valid!");
        }
    }

    @Override
    public void readExternal(@NotNull Element element) throws InvalidDataException {
        super.readExternal(element);
        runtimeConfigName = JDOMExternalizerUtil.readField(element, RUNTIME_CONFIG_NAME);
        moduleName = JDOMExternalizerUtil.readField(element, MODULE_NAME);
    }

    @Override
    public void writeExternal(@NotNull Element element) {
        super.writeExternal(element);
        JDOMExternalizerUtil.writeField(element, RUNTIME_CONFIG_NAME, runtimeConfigName);
        JDOMExternalizerUtil.writeField(element, MODULE_NAME, moduleName);
    }

    public void setRuntimeConfigName(String runtimePath) {
        this.runtimeConfigName = runtimePath;
    }

    public String getRuntimeConfigName() {
        return runtimeConfigName;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModule(String moduleName) {
        this.moduleName = moduleName;
    }
}
