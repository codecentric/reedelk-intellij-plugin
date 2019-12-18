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
import com.intellij.util.io.ZipUtil;
import com.reedelk.plugin.commons.Defaults;
import com.reedelk.plugin.commons.Icons;
import com.reedelk.plugin.commons.ReedelkPluginUtil;
import com.reedelk.plugin.exception.PluginException;
import com.reedelk.plugin.runconfig.module.ModuleRunConfigurationBuilder;
import com.reedelk.plugin.runconfig.runtime.RuntimeRunConfigurationBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.idea.maven.model.MavenId;
import org.jetbrains.idea.maven.utils.MavenUtil;
import org.jetbrains.idea.maven.wizards.MavenModuleBuilder;
import org.jetbrains.idea.maven.wizards.MavenModuleWizardStep;
import org.jetbrains.idea.maven.wizards.SelectPropertiesStep;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static com.reedelk.plugin.message.ReedelkBundle.message;

public class ModuleBuilder extends MavenModuleBuilder {

    private Path downloadDistributionPath;
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
        final Project project = rootModel.getProject();

        String contentEntryPath = getContentEntryPath();
        VirtualFile root = LocalFileSystem.getInstance().findFileByPath(contentEntryPath);

        if (createRuntimeConfig) {
            MavenUtil.runWhenInitialized(project, (DumbAwareRunnable) () -> {

                if (shouldDownloadDistribution()) {
                    // Copy runtime distribution from tmp folder, unzip it inside
                    // the module root directory, set the runtime home directory
                    // to the module root/runtime-folder-name.
                    // TODO: This extract should be done on the download.
                    // TODO: Here we just copy the distribution folder into th eproject module directory.
                    File destination = new File(root.getPath());
                    try {
                        ZipUtil.extract(downloadDistributionPath.toFile(), destination, (dir, name) -> true);
                        String[] reedelkRuntime = destination.list((dir, name) -> dir.isDirectory() &&
                                name.startsWith(Defaults.NameConvention.RUNTIME_DISTRIBUTION_ROOT_FOLDER_PREFIX));
                        if (reedelkRuntime == null || reedelkRuntime.length == 0) {
                            throw new PluginException("Error could not find reedelk runtime");
                        }
                        // Create Runtime Run Configuration
                        RuntimeRunConfigurationBuilder.build()
                                .withRuntimeConfigName(runtimeConfigName)
                                .withRuntimeHomeDirectory(Paths.get(root.getPath(), reedelkRuntime[0]).toString())
                                .add(project);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
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

    public void setDownloadDistributionPath(Path downloadDistributionPath) {
        this.downloadDistributionPath = downloadDistributionPath;
    }

    public Optional<Path> getDownloadDistributionPath() {
        return Optional.ofNullable(downloadDistributionPath);
    }

    private boolean shouldDownloadDistribution() {
        return downloadDistribution && downloadDistributionPath != null;
    }
}
