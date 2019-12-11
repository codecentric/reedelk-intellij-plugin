package com.reedelk.plugin.service.project.impl.runconfiguration;

import com.intellij.execution.ExecutionListener;
import com.intellij.execution.ExecutionManager;
import com.intellij.execution.RunManager;
import com.intellij.execution.RunnerAndConfigurationSettings;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.components.State;
import com.intellij.openapi.project.Project;
import com.reedelk.plugin.commons.RunConfigUtils;
import com.reedelk.plugin.service.project.PreferredRunConfigurationService;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@State(name = "reedelk-preferred-run-configuration")
public class PreferredRunConfigurationServiceImpl implements PreferredRunConfigurationService, ExecutionListener {

    private PreferredRunConfigurationState state = new PreferredRunConfigurationState();

    public PreferredRunConfigurationServiceImpl(Project project) {
        project.getMessageBus().connect().subscribe(ExecutionManager.EXECUTION_TOPIC, this);
    }

    @Override
    public void setLastModuleRunConfiguration(String name) {
        this.state.setLastModuleRunConfiguration(name);
    }

    @Override
    public void setLastRuntimeRunConfiguration(String name) {
        this.state.setLastRuntimeRunConfiguration(name);
    }

    @Override
    public void processTerminated(@NotNull String executorId, @NotNull ExecutionEnvironment env, @NotNull ProcessHandler handler, int exitCode) {
        RunnerAndConfigurationSettings runnerAndConfigurationSettings = env.getRunnerAndConfigurationSettings();
        if (runnerAndConfigurationSettings == null) return;

        RunConfigUtils.RuntimeRunConfiguration.type().ifPresent(runtimeRunConfigurationType -> {
            // If the process was terminated for a RuntimeRunConfiguration, then we select as current
            // selected configuration the  last runtime run configuration (or the first one of the list
            // if last runtime configuration is not present).
            if (runtimeRunConfigurationType.equals(runnerAndConfigurationSettings.getType())) {
                List<RunnerAndConfigurationSettings> configurationSettingsList =
                        RunManager.getInstance(env.getProject()).getConfigurationSettingsList(runtimeRunConfigurationType);
                setSelectedConfigurationMatching(env.getProject(), configurationSettingsList, state.getLastRuntimeRunConfiguration());
            }
        });
    }

    @Override
    public void processStarted(@NotNull String executorId, @NotNull ExecutionEnvironment env, @NotNull ProcessHandler handler) {
        RunnerAndConfigurationSettings runnerAndConfigurationSettings = env.getRunnerAndConfigurationSettings();
        if (runnerAndConfigurationSettings == null) return;

        RunConfigUtils.RuntimeRunConfiguration.type().ifPresent(runtimeRunConfigurationType -> {
            if (runtimeRunConfigurationType.equals(runnerAndConfigurationSettings.getType())) {
                RunConfigUtils.ModuleRunConfiguration.type().ifPresent(moduleRunConfigurationType -> {
                    List<RunnerAndConfigurationSettings> configurationSettingsList =
                            RunManager.getInstance(env.getProject()).getConfigurationSettingsList(moduleRunConfigurationType);
                    setSelectedConfigurationMatching(env.getProject(), configurationSettingsList, state.getLastModuleRunConfiguration());
                });
            }
        });
    }

    @Nullable
    @Override
    public PreferredRunConfigurationState getState() {
        return state;
    }

    @Override
    public void loadState(@NotNull PreferredRunConfigurationState state) {
        this.state = state;
    }

    private void setSelectedConfigurationMatching(Project project, List<RunnerAndConfigurationSettings> configurationSettingsList, String targetName) {
        RunnerAndConfigurationSettings toSelect = configurationSettingsList
                .stream()
                .filter(runnerAndConfigSettings -> targetName != null && targetName.equals(runnerAndConfigSettings.getName()))
                .findFirst()
                .orElseGet(() -> configurationSettingsList.isEmpty() ? null : configurationSettingsList.get(0));
        if (toSelect != null) {
            RunManager.getInstance(project).setSelectedConfiguration(toSelect);
        }
    }
}
