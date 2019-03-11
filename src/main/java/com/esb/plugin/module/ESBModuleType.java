package com.esb.plugin.module;

import com.esb.plugin.ESBIcons;
import com.intellij.ide.util.projectWizard.*;
import com.intellij.openapi.module.JavaModuleType;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.module.ModuleTypeManager;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.ui.configuration.ModulesProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

    @NotNull
    @Override
    public ModuleWizardStep[] createWizardSteps(@NotNull WizardContext wizardContext, @NotNull ESBModuleBuilder moduleBuilder, @NotNull ModulesProvider modulesProvider) {
        return new ModuleWizardStep[]{};
    }

    @Nullable
    @Override
    public ModuleWizardStep modifyProjectTypeStep(@NotNull SettingsStep settingsStep, @NotNull final ModuleBuilder moduleBuilder) {
        return ProjectWizardStepFactory.getInstance().createJavaSettingsStep(
                settingsStep,
                moduleBuilder,
                moduleBuilder::isSuitableSdkType);
    }

    @Override
    public boolean isValidSdk(@NotNull final Module module, final Sdk projectSdk) {
        return JavaModuleType.isValidJavaSdk(module);
    }
}
