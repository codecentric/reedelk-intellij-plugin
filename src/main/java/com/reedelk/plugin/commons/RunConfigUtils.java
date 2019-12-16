package com.reedelk.plugin.commons;

import com.intellij.execution.RunManager;
import com.intellij.execution.RunnerAndConfigurationSettings;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.ConfigurationType;
import com.intellij.openapi.project.Project;
import com.reedelk.plugin.runconfig.module.ModuleRunConfigurationType;
import com.reedelk.plugin.runconfig.runtime.RuntimeRunConfigurationType;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class RunConfigUtils {

    private RunConfigUtils() {
    }

    public static class RuntimeRunConfiguration {

        private RuntimeRunConfiguration() {
        }

        public static Optional<RuntimeRunConfigurationType> type() {
            return getConfigurationType(RuntimeRunConfigurationType.class);
        }

        public static boolean same(ConfigurationType toTest) {
            Optional<RuntimeRunConfigurationType> optionalType = type();
            return optionalType.map(runtimeRunConfigurationType -> runtimeRunConfigurationType.equals(toTest))
                    .orElse(false);
        }

        public static Optional<RunnerAndConfigurationSettings> create(Project project, String configName) {
            return type().flatMap(runtimeRunConfigType ->
                    createConfig(project, configName, runtimeRunConfigType.getConfigurationFactories()));
        }
    }

    public static class ModuleRunConfiguration {

        private ModuleRunConfiguration() {
        }

        public static Optional<ModuleRunConfigurationType> type() {
            return getConfigurationType(ModuleRunConfigurationType.class);
        }

        public static Optional<RunnerAndConfigurationSettings> create(Project project, String configName) {
            return type().flatMap(moduleRunConfigType ->
                    createConfig(project, configName, moduleRunConfigType.getConfigurationFactories()));
        }
    }

    @NotNull
    private static Optional<RunnerAndConfigurationSettings> createConfig(Project project, String configName, ConfigurationFactory[] factories) {
        if (factories.length != 0) {
            ConfigurationFactory factory = factories[0];
            return Optional.of(RunManager.getInstance(project).createConfiguration(configName, factory));
        }
        return Optional.empty();
    }

    @SuppressWarnings("unchecked")
    private static <T extends ConfigurationType> Optional<T> getConfigurationType(Class<T> expectedConfigType) {
        for (ConfigurationType type : ConfigurationType.CONFIGURATION_TYPE_EP.getExtensionList()) {
            if (type.getClass() == expectedConfigType) {
                return (Optional<T>) Optional.of(type);
            }
        }
        return Optional.empty();
    }
}
