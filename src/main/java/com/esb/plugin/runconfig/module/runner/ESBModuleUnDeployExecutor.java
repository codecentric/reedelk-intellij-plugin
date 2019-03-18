package com.esb.plugin.runconfig.module.runner;

import com.esb.plugin.utils.ESBIcons;
import com.intellij.execution.Executor;
import com.intellij.openapi.wm.ToolWindowId;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class ESBModuleUnDeployExecutor extends Executor {

    @NonNls
    public static final String EXECUTOR_ID = "unDeployESBModule";

    @Override
    public String getToolWindowId() {
        return ToolWindowId.RUN;
    }

    @Override
    public Icon getToolWindowIcon() {
        return ESBIcons.Module;
    }

    @NotNull
    @Override
    public Icon getIcon() {
        return ESBIcons.ModuleUnDeploy;
    }

    @Override
    public Icon getDisabledIcon() {
        return ESBIcons.ModuleUnDeployDisabled;
    }

    @Override
    public String getDescription() {
        return "UnDeploy ESB Module to Runtime";
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
        return "UnDeploy ESB Module";
    }

    @Override
    public String getContextActionId() {
        return "unDeployModule";
    }

    @Override
    public String getHelpId() {
        return "unDeploy.esb.id";
    }
}
