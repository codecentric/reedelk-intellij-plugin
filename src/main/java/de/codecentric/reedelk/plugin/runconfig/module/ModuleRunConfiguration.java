package de.codecentric.reedelk.plugin.runconfig.module;

import de.codecentric.reedelk.plugin.runconfig.module.runner.ModuleDeployExecutor;
import de.codecentric.reedelk.plugin.runconfig.module.runner.ModuleUnDeployExecutor;
import de.codecentric.reedelk.plugin.runconfig.module.runprofile.DeployRunProfile;
import com.intellij.CommonBundle;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.ExecutionTarget;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.*;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.runners.RunConfigurationWithSuppressedDefaultRunAction;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.JDOMExternalizerUtil;
import de.codecentric.reedelk.plugin.commons.ProjectSdk;
import de.codecentric.reedelk.plugin.runconfig.module.runprofile.UnDeployRunProfile;
import de.codecentric.reedelk.plugin.service.project.PreferredRunConfigurationService;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

import static de.codecentric.reedelk.plugin.message.ReedelkBundle.message;

public class ModuleRunConfiguration extends RunConfigurationBase<ModuleRunConfiguration> implements
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

        Optional<Sdk> optionalSdk = ProjectSdk.of(project);
        if (!optionalSdk.isPresent()) {
            Messages.showMessageDialog(project, message("error.sdk.not.selected"), CommonBundle.getErrorTitle(), Messages.getErrorIcon());
            return null;
        }

        // Deploy Executor
        if (ModuleDeployExecutor.EXECUTOR_ID.equals(executor.getId())) {
            PreferredRunConfigurationService.getInstance(project)
                    .setLastModuleRunConfiguration(getName());
            return new DeployRunProfile(project, moduleName, runtimeConfigName, getName());

            // UnDeploy Executor
        } else if (ModuleUnDeployExecutor.EXECUTOR_ID.equals(executor.getId())) {
            return new UnDeployRunProfile(project, moduleName, runtimeConfigName, getName());

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
