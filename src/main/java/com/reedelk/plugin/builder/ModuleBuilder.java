package com.reedelk.plugin.builder;

import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.ide.util.projectWizard.WizardContext;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.module.JavaModuleType;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.DumbAwareRunnable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.roots.ui.configuration.ModulesProvider;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.reedelk.plugin.commons.Icons;
import com.reedelk.plugin.commons.ReedelkPluginUtil;
import com.reedelk.plugin.runconfig.module.ModuleRunConfigurationBuilder;
import com.reedelk.plugin.runconfig.runtime.RuntimeRunConfigurationBuilder;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.idea.maven.model.MavenId;
import org.jetbrains.idea.maven.utils.MavenUtil;
import org.jetbrains.idea.maven.wizards.MavenModuleBuilder;
import org.jetbrains.idea.maven.wizards.MavenModuleWizardStep;
import org.jetbrains.idea.maven.wizards.SelectPropertiesStep;

import javax.swing.*;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static com.intellij.openapi.ui.Messages.showErrorDialog;
import static com.reedelk.plugin.message.ReedelkBundle.message;
import static javax.swing.SwingUtilities.invokeLater;

public class ModuleBuilder extends MavenModuleBuilder {

    private Path tmpDownloadDistributionPath;
    private String runtimeConfigName;
    private String runtimeHomeDirectory;
    private boolean createRuntimeConfig;
    private boolean downloadDistribution;

    public ModuleBuilder() {
        setProjectId(defaultMavenId());
    }

    @Override
    public void setupRootModel(@NotNull ModifiableRootModel rootModel) {
        super.setupRootModel(rootModel);

        @NotNull
        final String contentEntryPath = getContentEntryPath(); // Not null because it is initialized by the setup of the root model.
        final Project project = rootModel.getProject();

        VirtualFile root = LocalFileSystem.getInstance().findFileByPath(contentEntryPath);

        if (createRuntimeConfig) {
            MavenUtil.runWhenInitialized(project, (DumbAwareRunnable) () -> {

                if (shouldUseDownloadedDistribution()) {
                    this.runtimeConfigName = message("runtimeBuilder.name.default.value");
                    String unzippedRuntimeDirectoryName = message("unzipped.runtime.directory.name");
                    Path destination = Paths.get(contentEntryPath, unzippedRuntimeDirectoryName);
                    try {
                        FileUtils.copyDirectory(tmpDownloadDistributionPath.toFile(), destination.toFile());
                    } catch (IOException e) {
                        // We cannot recover here. We should just display an error message.
                        invokeLater(() -> showErrorDialog(
                                message("moduleBuilder.copy.distribution.error.message",
                                        tmpDownloadDistributionPath.toString(),
                                        destination.toString()),
                                message("moduleBuilder.copy.distribution.error.title")));
                    }

                    // Create Runtime Run Configuration
                    RuntimeRunConfigurationBuilder.build()
                            .withRuntimeConfigName(runtimeConfigName)
                            .withRuntimeHomeDirectory(destination.toString())
                            .add(project);

                } else {
                    // Create Runtime Run Configuration
                    RuntimeRunConfigurationBuilder.build()
                            .withRuntimeConfigName(runtimeConfigName)
                            .withRuntimeHomeDirectory(runtimeHomeDirectory)
                            .add(project);
                }
            });
        }

        MavenUtil.runWhenInitialized(project, (DumbAwareRunnable) () -> {
            // Add Module Run Configuration
            Module module = rootModel.getModule();
            ModuleRunConfigurationBuilder.build()
                    .withModuleName(module.getName())
                    .withRuntimeConfigName(runtimeConfigName)
                    .add(project);
        });

        MavenUtil.runWhenInitialized(project, (DumbAwareRunnable) () -> {
            // Create Maven project files
            MavenId projectId = getProjectId();
            MavenId parentId = getParentMavenId();
            String sdkVersion = rootModel.getSdkName();

            MavenProjectBuilderHelper projectBuilder = new MavenProjectBuilderHelper();
            projectBuilder.configure(project, projectId, parentId, root, sdkVersion);
        });

        ReedelkPluginUtil.runWhenInitialized(project, () -> {
            // Create Hello world flow and config
            HelloWorldProjectBuilderHelper helloWorldProjectBuilderHelper = new HelloWorldProjectBuilderHelper(project, root);
            helloWorldProjectBuilderHelper.configure();
        });
    }

    @Override
    public String getBuilderId() {
        return getClass().getName();
    }

    @Override
    public Icon getNodeIcon() {
        return Icons.ModuleBuilderStart;
    }

    @Override
    public String getName() {
        return message("moduleBuilder.name");
    }

    @Override
    public String getPresentableName() {
        return message("moduleBuilder.displayName");
    }

    @Override
    public String getDescription() {
        return message("moduleBuilder.description");
    }

    @Override
    public ModuleWizardStep[] createWizardSteps(@NotNull WizardContext wizardContext, @NotNull ModulesProvider modulesProvider) {
        return new ModuleWizardStep[]{
                new ConfigureRuntimeStep(wizardContext, this),
                new MavenModuleWizardStep(this, wizardContext, !wizardContext.isNewWizard()),
                new SelectPropertiesStep(wizardContext.getProject(), this)
        };
    }

    @Nullable
    @Override
    public ModuleWizardStep getCustomOptionsStep(WizardContext context, Disposable parentDisposable) {
        return new SelectRuntimeSourceStep(this);
    }

    @Override
    public String getParentGroup() {
        return JavaModuleType.BUILD_TOOLS_GROUP;
    }

    private MavenId getParentMavenId() {
        return getParentProject() != null ? getParentProject().getMavenId() : null;
    }

    private MavenId defaultMavenId() {
        return new MavenId(
                message("moduleBuilder.maven.defaultGroupId"),
                message("moduleBuilder.maven.defaultArtifactId"),
                message("moduleBuilder.maven.defaultVersion"));
    }

    public void setDownloadDistribution(boolean downloadDistribution) {
        this.downloadDistribution = downloadDistribution;
    }

    public boolean isDownloadDistribution() {
        return downloadDistribution;
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

    public void setTmpDownloadDistributionPath(Path tmpDownloadDistributionPath) {
        this.tmpDownloadDistributionPath = tmpDownloadDistributionPath;
    }

    public Optional<Path> getTmpDownloadDistributionPath() {
        return Optional.ofNullable(tmpDownloadDistributionPath);
    }

    private boolean shouldUseDownloadedDistribution() {
        return downloadDistribution && tmpDownloadDistributionPath != null;
    }
}
