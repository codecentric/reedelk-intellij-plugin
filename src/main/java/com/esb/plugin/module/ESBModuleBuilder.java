package com.esb.plugin.module;

import com.esb.plugin.ESBIcons;
import com.esb.plugin.module.wizard.step.ConfigureRuntimeStep;
import com.intellij.ide.util.projectWizard.ModuleBuilderListener;
import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.ide.util.projectWizard.WizardContext;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.module.JavaModuleType;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.project.DumbAwareRunnable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.idea.maven.model.MavenId;
import org.jetbrains.idea.maven.utils.MavenUtil;
import org.jetbrains.idea.maven.wizards.MavenModuleBuilder;

import javax.swing.*;
import java.io.File;

public class ESBModuleBuilder extends MavenModuleBuilder {

    private String groupId;
    private String version;
    private String artifactId;
    private String javaVersion = "1.8";

    private String runtimeHome;

    public ESBModuleBuilder() {
        setProjectId(new MavenId("com.esb.module", "sample-module", "1.0.0-SNAPSHOT"));
    }


    @Override
    public void setupRootModel(@NotNull ModifiableRootModel rootModel) {
        super.setupRootModel(rootModel);

        addListener(new ModuleBuilderListener() {
            @Override
            public void moduleCreated(@NotNull Module module) {
                // After module created
            }
        });

        final Project project = rootModel.getProject();
        final VirtualFile root = createAndGetContentEntry();

        rootModel.addContentEntry(root);

        final MavenId parentId = getParentProject() != null ? getParentProject().getMavenId() : null;
        final MavenId projectId = getProjectId();

        MavenUtil.runWhenInitialized(project, (DumbAwareRunnable) () ->
                new ESBMavenProjectBuilderHelper().configure(project, projectId, parentId, root));
    }

    private VirtualFile createAndGetContentEntry() {
        String path = FileUtil.toSystemIndependentName(this.getContentEntryPath());
        (new File(path)).mkdirs();
        return LocalFileSystem.getInstance().refreshAndFindFileByPath(path);
    }


    /**
    @Override
    public ModuleWizardStep[] createWizardSteps(@NotNull WizardContext wizardContext, @NotNull ModulesProvider modulesProvider) {
        return new ModuleWizardStep[] {
                new MavenConfigStep(wizardContext, this) };
    }*/

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
