package com.esb.plugin.runconfig.runtime;

import com.esb.plugin.commons.ESBNetworkUtils;
import com.esb.plugin.service.project.sourcechange.SourceChangeService;
import com.esb.plugin.service.project.toolwindow.ESBToolWindowService;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.*;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.JDOMExternalizerUtil;
import org.apache.commons.lang3.StringUtils;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Collections;

public class ESBRuntimeRunConfiguration extends RunConfigurationBase<ESBRuntimeRunConfiguration> implements ModuleRunProfile {

    private static final String PREFIX = "ESBRuntimeRunConfiguration-";
    private static final String VM_OPTIONS = PREFIX + "VmOptions";
    private static final String PORT = PREFIX + "Port";
    private static final String RUNTIME_HOME_DIRECTORY = PREFIX + "RuntimeHomeDirectory";

    private String vmOptions;
    private String runtimePort = "9988";
    private String runtimeBindAddress = "localhost";
    private String runtimeHomeDirectory;

    protected ESBRuntimeRunConfiguration(@NotNull Project project, @Nullable ConfigurationFactory factory, @Nullable String name) {
        super(project, factory, name);
    }

    @NotNull
    @Override
    public SettingsEditor<ESBRuntimeRunConfiguration> getConfigurationEditor() {
        RuntimeRunConfigurationSettings runtimeRunConfigurationSettings = new RuntimeRunConfigurationSettings(getProject());
        // Runtime Run Config does not have any Before Task to be executed prior to Runtime Launch.
        setBeforeRunTasks(Collections.emptyList());
        return runtimeRunConfigurationSettings;
    }

    @Override
    public void checkConfiguration() throws RuntimeConfigurationException {
        super.checkConfiguration();
        checkOrThrow(StringUtils.isNotBlank(runtimeHomeDirectory), "ESB Runtime home directory can not be empty");
        checkOrThrow(new File(runtimeHomeDirectory).exists(), "ESB Runtime home directory must be present");
        checkOrThrow(isNumberGreaterOrEqualToZero(runtimePort), "ESB Runtime port must be a number greater than zero");
    }

    @Override
    public void readExternal(@NotNull Element element) throws InvalidDataException {
        super.readExternal(element);
        runtimePort = JDOMExternalizerUtil.readField(element, PORT);
        vmOptions = JDOMExternalizerUtil.readField(element, VM_OPTIONS);
        runtimeHomeDirectory = JDOMExternalizerUtil.readField(element, RUNTIME_HOME_DIRECTORY);
    }

    @Override
    public void writeExternal(@NotNull Element element) {
        super.writeExternal(element);
        JDOMExternalizerUtil.writeField(element, PORT, runtimePort);
        JDOMExternalizerUtil.writeField(element, VM_OPTIONS, vmOptions);
        JDOMExternalizerUtil.writeField(element, RUNTIME_HOME_DIRECTORY, runtimeHomeDirectory);
    }

    @Nullable
    @Override
    public RunProfileState getState(@NotNull Executor executor, @NotNull ExecutionEnvironment environment) throws ExecutionException {
        // Check if port is available or throw exception (the runtime could not be started if port not available)
        checkPortAvailableOrThrow(runtimeBindAddress, Integer.parseInt(runtimePort));

        // Reset the state of the modules for this runtime
        SourceChangeService.getInstance(getProject()).reset(getName());

        // Store the ToolWindowId associated to this RunConfig. It will be used
        // later by a ModuleRun Configuration to switch to this tool window when a
        // deploy/un-deploy action is completed.
        ESBToolWindowService toolWindowService = ServiceManager.getService(getProject(), ESBToolWindowService.class);
        toolWindowService.put(getName(), executor.getToolWindowId());
        return new ESBRuntimeRunCommandLine(this, environment);
    }

    public void setVmOptions(String vmOptions) {
        this.vmOptions = vmOptions;
    }

    public void setRuntimePort(String runtimePort) {
        this.runtimePort = runtimePort;
    }

    public void setRuntimeHomeDirectory(String runtimeHomeDirectory) {
        this.runtimeHomeDirectory = runtimeHomeDirectory;
    }

    public String getVmOptions() {
        return vmOptions;
    }

    public String getRuntimePort() {
        return runtimePort;
    }

    public String getRuntimeHomeDirectory() {
        return runtimeHomeDirectory;
    }

    private void checkOrThrow(boolean condition, String message) throws RuntimeConfigurationException {
        if (!condition) {
            throw new RuntimeConfigurationException(message);
        }
    }

    private boolean isNumberGreaterOrEqualToZero(String number) {
        try {
            Integer integer = Integer.valueOf(number);
            return integer >= 0;
        } catch (Exception e) {
            return false;
        }
    }

    public void setRuntimeBindAddress(String runtimeBindAddress) {
        this.runtimeBindAddress = runtimeBindAddress;
    }

    public String getRuntimeBindAddress() {
        return runtimeBindAddress;
    }

    private static void checkPortAvailableOrThrow(String runtimeBindAddress, int runtimeBindPort) throws ExecutionException {
        boolean isPortAvailable = ESBNetworkUtils.available(runtimeBindAddress, runtimeBindPort);
        if (!isPortAvailable) {
            throw new ExecutionException(String.format("Could not start runtime on port %s. The port is in use.", runtimeBindPort));
        }
    }
}
