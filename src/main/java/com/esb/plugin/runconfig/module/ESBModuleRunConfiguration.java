package com.esb.plugin.runconfig.module;

import com.esb.plugin.runconfig.module.runprofile.DeployRunProfile;
import com.esb.plugin.runconfig.module.runprofile.UndeployRunProfile;
import com.esb.plugin.runner.ESBModuleDeployExecutor;
import com.esb.plugin.runner.ESBModuleUnDeployExecutor;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.ExecutionTarget;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.execution.configurations.RunConfigurationBase;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.runners.RunConfigurationWithSuppressedDefaultRunAction;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.JDOMExternalizerUtil;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ESBModuleRunConfiguration extends RunConfigurationBase implements RunConfigurationWithSuppressedDefaultRunAction {

    private static final String PREFIX = "ESBModuleRunConfiguration-";
    private static final String MODULE_NAME = PREFIX + "ModuleName";
    private static final String RUNTIME_HOME_DIRECTORY = PREFIX + "HomeDirectory";

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

        Project project = getProject();

        // Deploy Executor
        if (ESBModuleDeployExecutor.EXECUTOR_ID.equals(executor.getId())) {
            return new DeployRunProfile(project, moduleName);

            // UnDeploy Executor
        } else if (ESBModuleUnDeployExecutor.EXECUTOR_ID.equals(executor.getId())) {
            return new UndeployRunProfile(project, moduleName);

        } else {
            throw new ExecutionException("Executor not valid!");
        }
    }


    @Override
    public void readExternal(@NotNull Element element) throws InvalidDataException {
        super.readExternal(element);
        runtimePath = JDOMExternalizerUtil.readField(element, RUNTIME_HOME_DIRECTORY);
        moduleName = JDOMExternalizerUtil.readField(element, MODULE_NAME);
    }

    @Override
    public void writeExternal(@NotNull Element element) {
        super.writeExternal(element);
        JDOMExternalizerUtil.writeField(element, RUNTIME_HOME_DIRECTORY, runtimePath);
        JDOMExternalizerUtil.writeField(element, MODULE_NAME, moduleName);
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

    public String getModuleName() {
        return moduleName;
    }
}
