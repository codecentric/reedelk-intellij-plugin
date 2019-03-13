package com.esb.plugin.runner;

import com.esb.plugin.utils.ESBIcons;
import com.intellij.execution.Executor;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class ESBExecutor extends Executor {

    @NonNls
    public static final String EXECUTOR_ID = "deployESBModule";

    @Override
    public String getToolWindowId() {
        return EXECUTOR_ID;
    }

    @Override
    public Icon getToolWindowIcon() {
        return ESBIcons.Module;
    }

    @NotNull
    @Override
    public Icon getIcon() {
        return ESBIcons.ModuleDeploy;
    }

    @Override
    public Icon getDisabledIcon() {
        return ESBIcons.ModuleDeployDisabled;
    }

    @Override
    public String getDescription() {
        return "Deploying ESB Module to Runtime";
    }

    @NotNull
    @Override
    public String getActionName() {
        return "Deploy Module";
    }

    @NotNull
    @Override
    public String getId() {
        return EXECUTOR_ID;
    }

    @NotNull
    @Override
    public String getStartActionText() {
        return "Deploy ESB Module";
    }

    @Override
    public String getContextActionId() {
        return "deployModule";
    }

    @Override
    public String getHelpId() {
        return "id";
    }
}
