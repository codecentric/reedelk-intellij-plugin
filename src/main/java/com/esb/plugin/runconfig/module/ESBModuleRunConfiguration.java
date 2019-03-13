package com.esb.plugin.runconfig.module;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.ExecutionTarget;
import com.intellij.execution.Executor;
import com.intellij.execution.configuration.EmptyRunProfileState;
import com.intellij.execution.configurations.*;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collection;

public class ESBModuleRunConfiguration extends ModuleBasedConfiguration<JavaRunConfigurationModule, Element> {


    public ESBModuleRunConfiguration(String name, @NotNull Project project, @NotNull ConfigurationFactory factory) {
        super(name, new JavaRunConfigurationModule(project, true), factory);
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
        return EmptyRunProfileState.INSTANCE;
    }


    @Override
    public Collection<Module> getValidModules() {
        // TODO:  Create Utility which check which one is actually an ESB module!!! (e.g contains marker)
        return Arrays.asList(ModuleManager.getInstance(getProject()).getModules());
    }
}
