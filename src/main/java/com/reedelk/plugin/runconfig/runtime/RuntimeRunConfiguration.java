package com.reedelk.plugin.runconfig.runtime;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.*;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.JDOMExternalizerUtil;
import com.reedelk.plugin.commons.NetworkUtils;
import com.reedelk.plugin.service.project.SourceChangeService;
import com.reedelk.runtime.api.commons.StringUtils;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Collections;

import static com.reedelk.plugin.message.ReedelkBundle.message;

public class RuntimeRunConfiguration extends RunConfigurationBase<RuntimeRunConfiguration> implements ModuleRunProfile {

    private static final String PREFIX = "RuntimeRunConfiguration-";
    private static final String VM_OPTIONS = PREFIX + "VmOptions";
    private static final String PORT = PREFIX + "Port";
    private static final String RUNTIME_HOME_DIRECTORY = PREFIX + "RuntimeHomeDirectory";

    private String vmOptions;
    private String runtimePort = message("runtime.run.default.port");
    private String runtimeBindAddress = message("runtime.run.default.bindAddress");
    private String runtimeHomeDirectory;

    protected RuntimeRunConfiguration(@NotNull Project project, @Nullable ConfigurationFactory factory, @Nullable String name) {
        super(project, factory, name);
    }

    @NotNull
    @Override
    public SettingsEditor<RuntimeRunConfiguration> getConfigurationEditor() {
        RuntimeRunConfigurationSettings runtimeRunConfigurationSettings = new RuntimeRunConfigurationSettings(getProject());
        // Runtime Run Config does not have any Before Task to be executed prior to Runtime Launch.
        setBeforeRunTasks(Collections.emptyList());
        return runtimeRunConfigurationSettings;
    }

    @Override
    public void checkConfiguration() throws RuntimeConfigurationException {
        super.checkConfiguration();
        checkOrThrow(StringUtils.isNotBlank(runtimeHomeDirectory), message("runtime.run.error.home.directory.empty"));
        checkOrThrow(new File(runtimeHomeDirectory).exists(), message("runtime.run.error.home.directory.missing"));
        checkOrThrow(isNumberGreaterOrEqualToZero(runtimePort), message("runtime.run.error.port.greater.than.zero"));
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

        return new RuntimeRunCommandLine(this, environment);
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
        boolean isPortAvailable = NetworkUtils.available(runtimeBindAddress, runtimeBindPort);
        if (!isPortAvailable) {
            String message = message("runtime.run.error.port.in.use", runtimeBindPort);
            throw new ExecutionException(message);
        }
    }
}
