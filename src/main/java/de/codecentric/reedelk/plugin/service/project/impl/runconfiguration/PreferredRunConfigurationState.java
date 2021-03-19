package de.codecentric.reedelk.plugin.service.project.impl.runconfiguration;

public class PreferredRunConfigurationState {

    private String lastModuleRunConfiguration;
    private String lastRuntimeRunConfiguration;

    public String getLastModuleRunConfiguration() {
        return lastModuleRunConfiguration;
    }

    public void setLastModuleRunConfiguration(String lastModuleRunConfiguration) {
        this.lastModuleRunConfiguration = lastModuleRunConfiguration;
    }

    public String getLastRuntimeRunConfiguration() {
        return lastRuntimeRunConfiguration;
    }

    public void setLastRuntimeRunConfiguration(String lastRuntimeRunConfiguration) {
        this.lastRuntimeRunConfiguration = lastRuntimeRunConfiguration;
    }
}
