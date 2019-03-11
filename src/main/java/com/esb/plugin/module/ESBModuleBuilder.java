package com.esb.plugin.module;

import com.esb.plugin.ESBIcons;
import com.esb.plugin.module.wizard.step.ConfigureRuntimeStep;
import com.esb.plugin.module.wizard.step.EmptyStep;
import com.esb.plugin.module.wizard.step.MavenConfigStep;
import com.esb.plugin.templating.POMTemplate;
import com.intellij.ide.util.projectWizard.ModuleBuilder;
import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.ide.util.projectWizard.WizardContext;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.module.JavaModuleType;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.roots.ui.configuration.ModulesProvider;
import com.intellij.openapi.util.Disposer;
import freemarker.template.TemplateException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.io.IOException;

public class ESBModuleBuilder extends ModuleBuilder {

    private String groupId;
    private String version;
    private String artifactId;
    private String javaVersion = "1.8";

    private String runtimeHome;


    @Override
    public void setupRootModel(@NotNull ModifiableRootModel rootModel) {
        final Project project = rootModel.getProject();
        if (myJdk != null){
            rootModel.setSdk(myJdk);
        } else {
            rootModel.inheritSdk();
        }
    }

    @Override
    public Project createProject(String name, String path) {

        Project project = super.createProject(name, path);

        POMTemplate template = new POMTemplate();
        try {
            template.create(groupId, version, artifactId, javaVersion, project.getBasePath());
        } catch (IOException | TemplateException e) {
            e.printStackTrace();
        }

        return project;
    }

    @Override
    public ModuleWizardStep[] createWizardSteps(@NotNull WizardContext wizardContext, @NotNull ModulesProvider modulesProvider) {
        return new ModuleWizardStep[] {
                new MavenConfigStep(wizardContext, this) };
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

    @Override
    public String getPresentableName() {
        return "ESB Designer";
    }

    @Override
    public Icon getNodeIcon() {
        return ESBIcons.Module;
    }

    @Nullable
    @Override
    public ModuleWizardStep getCustomOptionsStep(WizardContext context, Disposable parentDisposable) {
        ConfigureRuntimeStep step = new ConfigureRuntimeStep(context, this);
        Disposer.register(parentDisposable, step);
        return step;
    }

    @Override
    public String getParentGroup() {
        return JavaModuleType.JAVA_GROUP;
    }

    @Override
    public ModuleType getModuleType() {
        return ESBModuleType.getInstance();
    }
}
