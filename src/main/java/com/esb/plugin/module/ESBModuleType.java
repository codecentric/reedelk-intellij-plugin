package com.esb.plugin.module;

import com.esb.plugin.ESBIcons;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.module.ModuleTypeManager;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class ESBModuleType extends ModuleType<ESBModuleBuilder> {

    private static final String ID = "ESB_MODULE";

    public static ESBModuleType getInstance() {
        return (ESBModuleType) ModuleTypeManager.getInstance().findByID(ID);
    }

    private static final String MODULE_NAME = "ESB Module";
    private static final String MODULE_DESCRIPTION = "Flow Designer for ESB";

    public ESBModuleType() {
        super("ESB_MODULE");
    }

    @NotNull
    @Override
    public ESBModuleBuilder createModuleBuilder() {
        return new ESBModuleBuilder();
    }

    @NotNull
    @Override
    public String getName() {
        return MODULE_NAME;
    }

    @NotNull
    @Override
    public String getDescription() {
        return MODULE_DESCRIPTION;
    }

    @Override
    public Icon getNodeIcon(boolean isOpened) {
        return ESBIcons.Module;
    }

}
