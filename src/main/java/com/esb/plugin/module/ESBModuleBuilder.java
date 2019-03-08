package com.esb.plugin.module;

import com.esb.plugin.module.wizard.step.ConfigureRuntime;
import com.intellij.ide.util.projectWizard.JavaModuleBuilder;
import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.ide.util.projectWizard.WizardContext;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.roots.ui.configuration.ModulesProvider;
import org.jetbrains.annotations.NotNull;

public class ESBModuleBuilder extends JavaModuleBuilder {

    @Override
    public ModuleType getModuleType() {
        return new ESBModuleType();
    }

    @Override
    public ModuleWizardStep[] createWizardSteps(@NotNull WizardContext wizardContext, @NotNull ModulesProvider modulesProvider) {
        return new ModuleWizardStep[]{
                new ConfigureRuntime(wizardContext, this)
        };
    }

}
