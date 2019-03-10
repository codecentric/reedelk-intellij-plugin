package com.esb.plugin.module;

import com.esb.plugin.module.wizard.step.ConfigureRuntime;
import com.esb.plugin.templating.POMTemplate;
import com.intellij.ide.util.projectWizard.ModuleBuilder;
import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.ide.util.projectWizard.WizardContext;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.roots.ui.configuration.ModulesProvider;
import freemarker.template.TemplateException;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class ESBModuleBuilder extends ModuleBuilder {

    private String groupId;
    private String version;
    private String artifactId;
    private String javaVersion;

    private String runtimeHome;

    @Override
    public ModuleType getModuleType() {
        return ESBModuleType.getInstance();
    }

    @Override
    public ModuleWizardStep[] createWizardSteps(@NotNull WizardContext wizardContext, @NotNull ModulesProvider modulesProvider) {
        return new ModuleWizardStep[]{ new ConfigureRuntime(wizardContext, this)};
    }

    @Override
    public void setupRootModel(ModifiableRootModel modifiableRootModel) throws ConfigurationException {

    }

    @Override
    public Project createProject(String name, String path) {
        Project project = super.createProject(name, path);
        // Create pom.xml template
        POMTemplate template = new POMTemplate();
        try {
            template.create(groupId, version, artifactId, javaVersion);
        } catch (IOException | TemplateException e) {
            e.printStackTrace();
        }

        String basePath = project.getBasePath();
        return project;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setArtifactId(String artifactId) {
        this.artifactId = artifactId;
    }

    public void setRuntimeHome(String runtimeHome) {
        this.runtimeHome = runtimeHome;
    }

}
