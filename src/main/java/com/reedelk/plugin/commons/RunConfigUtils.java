package com.reedelk.plugin.commons;

import com.intellij.execution.configurations.ConfigurationType;
import com.reedelk.plugin.runconfig.module.ModuleRunConfigurationType;
import com.reedelk.plugin.runconfig.runtime.RuntimeRunConfigurationType;

import java.util.Optional;

public class RunConfigUtils {

    public static class RuntimeRunConfiguration {

        public static Optional<RuntimeRunConfigurationType> type() {
            return getConfigurationType(RuntimeRunConfigurationType.class);
        }

        public static boolean equals(ConfigurationType toTest) {
            Optional<RuntimeRunConfigurationType> optionalType = type();
            return optionalType.map(runtimeRunConfigurationType -> runtimeRunConfigurationType.equals(toTest))
                    .orElse(false);
        }
    }

    public static class ModuleRunConfiguration {


        public static Optional<ModuleRunConfigurationType> type() {
            return getConfigurationType(ModuleRunConfigurationType.class);
        }


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
