package com.reedelk.plugin.runconfig.module.runner;

import com.intellij.execution.Executor;
import com.intellij.openapi.wm.ToolWindowId;
import com.reedelk.plugin.commons.Icons;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class ModuleDeployExecutor extends Executor {

    @NonNls
    public static final String EXECUTOR_ID = "deployESBModule";

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
        return Icons.ModuleDeploy;
    }

    @Override
    public Icon getDisabledIcon() {
        return Icons.ModuleDeployDisabled;
    }

    @Override
    public String getDescription() {
        return "Deploy ESB Module to Runtime";
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
        return "deploy.esb.id";
    }

}
