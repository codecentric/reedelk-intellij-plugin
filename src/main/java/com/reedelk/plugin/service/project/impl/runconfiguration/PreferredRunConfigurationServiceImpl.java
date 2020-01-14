package com.reedelk.plugin.service.project.impl.runconfiguration;

import com.intellij.execution.*;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.runners.ExecutionEnvironmentBuilder;
import com.intellij.openapi.components.State;
import com.intellij.openapi.project.Project;
import com.reedelk.plugin.commons.RunConfigUtils;
import com.reedelk.plugin.executor.PluginExecutors;
import com.reedelk.plugin.runconfig.module.runner.ModuleDeployExecutor;
import com.reedelk.plugin.runconfig.runtime.RuntimeRunConfiguration;
import com.reedelk.plugin.service.module.impl.runtimeapi.RuntimeApiServiceWaitRuntime;
import com.reedelk.plugin.service.project.PreferredRunConfigurationService;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

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
            Project project = env.getProject();

            if (runtimeRunConfigurationType.equals(runnerAndConfigurationSettings.getType())) {
                List<RunnerAndConfigurationSettings> configurationSettingsList =
                        RunManager.getInstance(project).getConfigurationSettingsList(runtimeRunConfigurationType);

                String lastRuntimeRunConfiguration = state.getLastRuntimeRunConfiguration();
                getRunConfigurationSettingByName(configurationSettingsList, lastRuntimeRunConfiguration)
                        .ifPresent(matchingConfiguration -> RunManager.getInstance(project).setSelectedConfiguration(matchingConfiguration));
            }
        });
    }

    @Override
    public void processStarted(@NotNull String executorId, @NotNull ExecutionEnvironment env, @NotNull ProcessHandler handler) {
        RunnerAndConfigurationSettings runnerAndConfigurationSettings = env.getRunnerAndConfigurationSettings();
        if (runnerAndConfigurationSettings == null) return;

        final Project project = env.getProject();

        RunConfigUtils.RuntimeRunConfiguration.type().ifPresent(runtimeRunConfigurationType -> {
            if (runtimeRunConfigurationType.equals(runnerAndConfigurationSettings.getType())) {
                RunConfigUtils.ModuleRunConfiguration.type().ifPresent(moduleRunConfigurationType -> {
                    List<RunnerAndConfigurationSettings> moduleRunConfigurationSettingList =
                            RunManager.getInstance(project).getConfigurationSettingsList(moduleRunConfigurationType);

                    String lastModuleRunConfigurationName = state.getLastModuleRunConfiguration();
                    getRunConfigurationSettingByName(moduleRunConfigurationSettingList, lastModuleRunConfigurationName)
                            .ifPresent(matchingConfiguration -> {
                                // Select the last module run configuration in the run configuration drop down
                                RunManager.getInstance(project).setSelectedConfiguration(matchingConfiguration);

                                // Deploy the last module when runtime started.
                                deployLastModuleWhenRuntimeStarted(project, runnerAndConfigurationSettings, matchingConfiguration);
                            });
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

    private Optional<RunnerAndConfigurationSettings> getRunConfigurationSettingByName(List<RunnerAndConfigurationSettings> configurationSettingsList, String targetName) {
        return Optional.ofNullable(configurationSettingsList
                .stream()
                .filter(runnerAndConfigSettings -> targetName != null && targetName.equals(runnerAndConfigSettings.getName()))
                .findFirst()
                .orElseGet(() -> configurationSettingsList.isEmpty() ? null : configurationSettingsList.get(0)));
    }

    /**
     * Start the run configuration with the given executor.
     */
    private void deployLastModuleWhenRuntimeStarted(Project project,
                                                    RunnerAndConfigurationSettings runtimeRunSettings,
                                                    RunnerAndConfigurationSettings moduleDeploySettings) {
        Executor executor = ExecutorRegistry.getInstance().getExecutorById(ModuleDeployExecutor.EXECUTOR_ID);
        final ExecutionEnvironmentBuilder builder = ExecutionEnvironmentBuilder.createOrNull(executor, moduleDeploySettings);
        if (builder != null) {
            RuntimeRunConfiguration runtimeRunConfiguration = (RuntimeRunConfiguration) runtimeRunSettings.getConfiguration();
            int port = Integer.parseInt(runtimeRunConfiguration.getRuntimePort());
            String address = runtimeRunConfiguration.getRuntimeBindAddress();
            PluginExecutors.runSmartReadAction(project, new RuntimeApiServiceWaitRuntime(port, address,
                    onRuntimeStartedVoid -> ExecutionManager.getInstance(project).restartRunProfile(builder.build())));
        }
    }
}