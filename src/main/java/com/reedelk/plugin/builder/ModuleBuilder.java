package com.reedelk.plugin.builder;

import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.ide.util.projectWizard.WizardContext;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.JavaModuleType;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.DumbAwareRunnable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.reedelk.plugin.commons.Icons;
import com.reedelk.plugin.commons.Label;
import com.reedelk.plugin.runconfig.module.ModuleRunConfigurationBuilder;
import com.reedelk.plugin.runconfig.runtime.RuntimeRunConfigurationBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.idea.maven.model.MavenId;
import org.jetbrains.idea.maven.utils.MavenUtil;
import org.jetbrains.idea.maven.wizards.MavenModuleBuilder;

import javax.swing.*;

public class ModuleBuilder extends MavenModuleBuilder {

    private static final Logger LOG = Logger.getInstance(ModuleBuilder.class);

    private boolean createRuntimeConfig;
    private String runtimeConfigName;
    private String runtimeHomeDirectory;

    public ModuleBuilder() {
        setProjectId(defaultMavenId());
    }

    @Override
    public void setupRootModel(@NotNull ModifiableRootModel rootModel) {
        super.setupRootModel(rootModel);
        final Project project = rootModel.getProject();

        VirtualFile root = LocalFileSystem.getInstance().findFileByPath(getContentEntryPath());

        if (createRuntimeConfig) {
            // Create Runtime Run Configuration
            RuntimeRunConfigurationBuilder.build()
                    .withRuntimeConfigName(runtimeConfigName)
                    .withRuntimeHomeDirectory(runtimeHomeDirectory)
                    .add(project);
        }

        // Add Module Run Configuration
        Module module = rootModel.getModule();

        ModuleRunConfigurationBuilder.build()
                .withModuleName(module.getName())
                .withRuntimeConfigName(runtimeConfigName)
                .add(project);

        final MavenId projectId = getProjectId();
        final MavenId parentId = getParentMavenId();
        final String sdkVersion = rootModel.getSdkName();

        MavenUtil.runWhenInitialized(project, (DumbAwareRunnable) () -> {
            MavenProjectBuilderHelper projectBuilder = new MavenProjectBuilderHelper();
            try {
                projectBuilder.configure(project, projectId, parentId, root, sdkVersion);
            } catch (Throwable throwable) {
                LOG.error("Error while configuring Maven project", throwable);
            }
        });
    }

    @Override
    public String getBuilderId() {
        return getClass().getName();
    }

    @Override
    public Icon getNodeIcon() {
        return Icons.Module;
    }

    @Override
    public String getName() {
        return Label.MODULE_BUILDER_NAME.get();
    }

    @Override
    public String getPresentableName() {
        return Label.MODULE_BUILDER_NAME.get();
    }

    @Override
    public String getDescription() {
        return Label.MODULE_BUILDER_DESCRIPTION.get();
    }

    @Nullable
    @Override
    public ModuleWizardStep getCustomOptionsStep(WizardContext context, Disposable parentDisposable) {
        ConfigureRuntimeStep step = new ConfigureRuntimeStep(context, this, context.getProject());
        Disposer.register(parentDisposable, step);
        return step;
    }

    @Override
    public String getParentGroup() {
        return JavaModuleType.BUILD_TOOLS_GROUP;
    }

    private MavenId getParentMavenId() {
        return getParentProject() != null ?
                getParentProject().getMavenId() :
                null;
    }

    private MavenId defaultMavenId() {
        return new MavenId(
                Label.DEFAULT_GROUP_ID.get(),
                Label.DEFAULT_ARTIFACT_ID.get(),
                Label.DEFAULT_VERSION.get());
    }

    public void setRuntimeConfigName(String runtimeConfigName) {
        this.runtimeConfigName = runtimeConfigName;
    }

    public void setRuntimeHomeDirectory(String runtimeHomeDirectory) {
        this.runtimeHomeDirectory = runtimeHomeDirectory;
    }

    public void createRuntimeConfig(boolean createRuntimeConfig) {
        this.createRuntimeConfig = createRuntimeConfig;
    }
}
