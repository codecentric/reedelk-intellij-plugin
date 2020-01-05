package com.reedelk.plugin.runconfig.module.runner;

import com.intellij.execution.Executor;
import com.intellij.openapi.wm.ToolWindowId;
import com.reedelk.plugin.commons.Icons;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class ModuleUnDeployExecutor extends Executor {

    @NonNls
    public static final String EXECUTOR_ID = "unDeployReedelkModule";

    @Override
    public String getToolWindowId() {
        return ToolWindowId.RUN;
    }

    @Override
    public Icon getToolWindowIcon() {
        return Icons.Module;
    }

    @NotNull
    @Override
    public Icon getIcon() {
        return Icons.ModuleUnDeploy;
    }

    @Override
    public Icon getDisabledIcon() {
        return Icons.ModuleUnDeployDisabled;
    }

    @Override
    public String getDescription() {
        return "UnDeploy Module to Runtime";
    }

    @NotNull
    @Override
    public String getActionName() {
        return "UnDeploy Module";
    }

    @NotNull
    @Override
    public String getId() {
        return EXECUTOR_ID;
    }

    @NotNull
    @Override
    public String getStartActionText() {
        return "UnDeploy Module";
    }

    @Override
    public String getContextActionId() {
        return "unDeployModule";
    }

    @Override
    public String getHelpId() {
        return "unDeploy.reedelk.id";
    }
}
