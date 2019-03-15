package com.esb.plugin.module;

import com.esb.plugin.module.wizard.step.ConfigureRuntimeStep;
import com.esb.plugin.runconfig.module.ESBModuleRunConfiguration;
import com.esb.plugin.runconfig.module.ESBModuleRunConfigurationFactory;
import com.esb.plugin.runconfig.module.ESBModuleRunConfigurationType;
import com.esb.plugin.runconfig.runtime.ESBRuntimeRunConfiguration;
import com.esb.plugin.runconfig.runtime.ESBRuntimeRunConfigurationFactory;
import com.esb.plugin.runconfig.runtime.ESBRuntimeRunConfigurationType;
import com.esb.plugin.utils.ESBIcons;
import com.esb.plugin.utils.ESBLabel;
import com.intellij.execution.RunManager;
import com.intellij.execution.RunnerAndConfigurationSettings;
import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.ide.util.projectWizard.WizardContext;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.module.JavaModuleType;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.DumbAwareRunnable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.idea.maven.model.MavenId;
import org.jetbrains.idea.maven.utils.MavenUtil;
import org.jetbrains.idea.maven.wizards.MavenModuleBuilder;

import javax.swing.*;

public class ESBModuleBuilder extends MavenModuleBuilder {

    private boolean isNewProject;
    private String runtimeConfigName;
    private String runtimeHomeDirectory;

    public ESBModuleBuilder() {
        setProjectId(defaultMavenId());
    }

    @Override
    public void setupRootModel(@NotNull ModifiableRootModel rootModel) {
        super.setupRootModel(rootModel);
        final Project project = rootModel.getProject();

        VirtualFile root = LocalFileSystem.getInstance().findFileByPath(getContentEntryPath());


        if (isNewProject) {
            // Create Runtime Run Configuration
            // Associate to this module the run configuration just created above
            RunnerAndConfigurationSettings runConfigurationSettings = RunManager.getInstance(project).createConfiguration(runtimeConfigName, new ESBRuntimeRunConfigurationFactory(new ESBRuntimeRunConfigurationType()));
            ESBRuntimeRunConfiguration  configuration = (ESBRuntimeRunConfiguration) runConfigurationSettings.getConfiguration();
            configuration.setRuntimeHomeDirectory(runtimeHomeDirectory);
            configuration.setName(runtimeConfigName);

            RunManager.getInstance(project).addConfiguration(runConfigurationSettings);
            RunManager.getInstance(project).setSelectedConfiguration(runConfigurationSettings);
        }

        // Add Module Config with this Runtime ID
        Module module = rootModel.getModule();

        RunnerAndConfigurationSettings moduleConfigurationSettings = RunManager.getInstance(project).createConfiguration(module.getName(), new ESBModuleRunConfigurationFactory(new ESBModuleRunConfigurationType()));

        ESBModuleRunConfiguration esbModuleRunConfiguration = (ESBModuleRunConfiguration) moduleConfigurationSettings.getConfiguration();
        esbModuleRunConfiguration.setModule(module.getName());
        esbModuleRunConfiguration.setRuntimeConfigName(runtimeConfigName);
        RunManager.getInstance(project).addConfiguration(moduleConfigurationSettings);



        final MavenId projectId = getProjectId();
        final MavenId parentId = getParentMavenId();
        final String sdkVersion = rootModel.getSdkName();

        MavenUtil.runWhenInitialized(project, (DumbAwareRunnable) () -> {
            try {
                new ESBMavenProjectBuilderHelper().configure(project, projectId, parentId, root, sdkVersion);
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        });
    }

    @Override
    public String getBuilderId() {
        return getClass().getName();
    }

    @Override
    public Icon getNodeIcon() {
        return ESBIcons.Module;
    }

    @Override
    public String getName() {
        return ESBLabel.MODULE_BUILDER_NAME.get();
    }

    @Override
    public String getPresentableName() {
        return ESBLabel.MODULE_BUILDER_NAME.get();
    }

    @Override
    public String getDescription() {
        return ESBLabel.MODULE_BUILDER_DESCRIPTION.get();
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
                ESBLabel.DEFAULT_GROUP_ID.get(),
                ESBLabel.DEFAULT_ARTIFACT_ID.get(),
                ESBLabel.DEFAULT_VERSION.get());
    }

    public void setRuntimeConfigName(String runtimeConfigName) {
        this.runtimeConfigName = runtimeConfigName;
    }

    public void setRuntimeHomeDirectory(String runtimeHomeDirectory) {
        this.runtimeHomeDirectory = runtimeHomeDirectory;
    }

    public void isNewProject(boolean isNewProject) {
        this.isNewProject = isNewProject;
    }
}
