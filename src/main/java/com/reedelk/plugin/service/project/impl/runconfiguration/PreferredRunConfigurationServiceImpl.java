package com.reedelk.plugin.service.project.impl.runconfiguration;

import com.intellij.execution.ExecutionListener;
import com.intellij.execution.ExecutionManager;
import com.intellij.execution.RunManager;
import com.intellij.execution.RunnerAndConfigurationSettings;
import com.intellij.execution.configurations.ConfigurationType;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.components.State;
import com.intellij.openapi.project.Project;
import com.reedelk.plugin.runconfig.module.ModuleRunConfigurationType;
import com.reedelk.plugin.runconfig.runtime.RuntimeRunConfigurationType;
import com.reedelk.plugin.service.project.PreferredRunConfigurationService;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

@State(name = "reedelk-preferred-run-configuration")
public class PreferredRunConfigurationServiceImpl implements PreferredRunConfigurationService, ExecutionListener {

    private final Project project;
    private PreferredRunConfigurationState state = new PreferredRunConfigurationState();

    public PreferredRunConfigurationServiceImpl(Project project) {
        this.project = project;
        project.getMessageBus().connect().subscribe(ExecutionManager.EXECUTION_TOPIC, this);
    }

    @Override
    public void setLastModuleRunConfiguration(String name) {
        this.state.lastModuleRunConfiguration = name;
    }

    @Override
    public void setLastRuntimeRunConfiguration(String name) {
        this.state.lastRuntimeRunConfiguration = name;
    }

    @Override
    public void processTerminated(@NotNull String executorId, @NotNull ExecutionEnvironment env, @NotNull ProcessHandler handler, int exitCode) {
        RunnerAndConfigurationSettings runnerAndConfigurationSettings = env.getRunnerAndConfigurationSettings();
        if (runnerAndConfigurationSettings == null) return;

        getRuntimeRunConfigurationType().ifPresent(runtimeRunConfigType -> {
            if (runtimeRunConfigType.equals(runnerAndConfigurationSettings.getType())) {
                List<RunnerAndConfigurationSettings> configurationSettingsList =
                        RunManager.getInstance(env.getProject()).getConfigurationSettingsList(runtimeRunConfigType);

                if (state.lastRuntimeRunConfiguration != null) {
                    configurationSettingsList.forEach(runnerAndConfigSetting -> {
                        if (runnerAndConfigSetting.getName().equals(state.lastRuntimeRunConfiguration)) {
                            RunManager.getInstance(env.getProject()).setSelectedConfiguration(runnerAndConfigSetting);
                        }
                    });
                } else {
                    if (configurationSettingsList.size() > 0) {
                        RunnerAndConfigurationSettings runConfiguration = configurationSettingsList.get(0);
                        RunManager.getInstance(env.getProject()).setSelectedConfiguration(runConfiguration);
                    }
                }
            }
        });
    }

    @Override
    public void processStarted(@NotNull String executorId, @NotNull ExecutionEnvironment env, @NotNull ProcessHandler handler) {
        RunnerAndConfigurationSettings runnerAndConfigurationSettings = env.getRunnerAndConfigurationSettings();
        if (runnerAndConfigurationSettings == null) return;

        getRuntimeRunConfigurationType().ifPresent(runtimeRunConfigType -> {
            if (runtimeRunConfigType.equals(runnerAndConfigurationSettings.getType())) {
                // If the process started was due caused by the start of a RuntimeRunConfigurationType,
                // If 'lastModuleRunConfiguration' was not null, then we select the latest Module Run Configuration
                // chosen by the user, otherwise we take the first one of the Available.
                getModuleRunConfigurationType().ifPresent(moduleRunConfigType -> {
                    List<RunnerAndConfigurationSettings> configurationSettingsList =
                            RunManager.getInstance(env.getProject()).getConfigurationSettingsList(moduleRunConfigType);
                    if (state.lastModuleRunConfiguration != null) {
                        configurationSettingsList.forEach(runnerAndConfigSetting -> {
                            if (runnerAndConfigSetting.getName().equals(state.lastModuleRunConfiguration)) {
                                RunManager.getInstance(env.getProject()).setSelectedConfiguration(runnerAndConfigSetting);
                            }
                        });
                    } else {
                        // We take the first one
                        if (configurationSettingsList.size() > 0) {
                            RunnerAndConfigurationSettings runConfiguration = configurationSettingsList.get(0);
                            RunManager.getInstance(env.getProject()).setSelectedConfiguration(runConfiguration);
                        }
                    }
                });
            }
        });
    }

    private Optional<ConfigurationType> getModuleRunConfigurationType() {
        for (ConfigurationType type : ConfigurationType.CONFIGURATION_TYPE_EP.getExtensionList()) {
            if (type instanceof ModuleRunConfigurationType) {
                return Optional.of(type);
            }
        }
        return Optional.empty();
    }

    private Optional<ConfigurationType> getRuntimeRunConfigurationType() {
        for (ConfigurationType type : ConfigurationType.CONFIGURATION_TYPE_EP.getExtensionList()) {
            if (type instanceof RuntimeRunConfigurationType) {
                return Optional.of(type);
            }
        }
        return Optional.empty();
    }


    @Nullable
    @Override
    public PreferredRunConfigurationState getState() {
        return state;
    }

    @Override
    public void loadState(@NotNull PreferredRunConfigurationState state) {
        this.state = state;
        getRuntimeRunConfigurationType().ifPresent(runtimeRunConfigType -> {
            List<RunnerAndConfigurationSettings> configurationSettingsList =
                    RunManager.getInstance(project).getConfigurationSettingsList(runtimeRunConfigType);
            if (state.lastRuntimeRunConfiguration != null) {
                configurationSettingsList.forEach(runnerAndConfigSetting -> {
                    if (runnerAndConfigSetting.getName().equals(state.lastRuntimeRunConfiguration)) {
                        RunManager.getInstance(project).setSelectedConfiguration(runnerAndConfigSetting);
                    }
                });
            }
        });
    }
}
