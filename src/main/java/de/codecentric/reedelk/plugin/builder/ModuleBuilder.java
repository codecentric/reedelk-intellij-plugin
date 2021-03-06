package de.codecentric.reedelk.plugin.builder;

import de.codecentric.reedelk.plugin.commons.ChangeFilesPermissions;
import de.codecentric.reedelk.plugin.runconfig.module.ModuleRunConfigurationBuilder;
import de.codecentric.reedelk.plugin.runconfig.runtime.RuntimeRunConfigurationBuilder;
import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.ide.util.projectWizard.WizardContext;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.module.JavaModuleType;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.DumbAwareRunnable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.roots.ui.configuration.ModulesProvider;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import de.codecentric.reedelk.plugin.commons.Icons;
import de.codecentric.reedelk.plugin.commons.ReedelkPluginUtil;
import de.codecentric.reedelk.plugin.commons.ReedelkRuntimeVersion;
import de.codecentric.reedelk.plugin.message.ReedelkBundle;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.idea.maven.model.MavenId;
import org.jetbrains.idea.maven.utils.MavenUtil;
import org.jetbrains.idea.maven.wizards.AbstractMavenModuleBuilder;
import org.jetbrains.idea.maven.wizards.MavenModuleWizardStep;
import org.jetbrains.idea.maven.wizards.SelectPropertiesStep;

import javax.swing.*;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static com.intellij.openapi.ui.Messages.showErrorDialog;
import static de.codecentric.reedelk.plugin.message.ReedelkBundle.message;

public class ModuleBuilder extends AbstractMavenModuleBuilder {

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

        @NotNull final String contentEntryPath = getContentEntryPath(); // Not null because it is initialized by the setup of the root model.
        final Project project = rootModel.getProject();

        VirtualFile root = LocalFileSystem.getInstance().findFileByPath(contentEntryPath);

        final String finalRuntimeHomeDirectory;
        if (shouldUseDownloadedDistribution()) {
            // Copy downloaded distribution in the project's root directory.
            Path destination = copyDownloadPathDistributionInProject(contentEntryPath);
            // We use the Runtime Home from the downloaded Reedelk distribution.
            finalRuntimeHomeDirectory = destination.toString();
        } else {
            // We use the Runtime Home selected by the user.
            finalRuntimeHomeDirectory = runtimeHomeDirectory;
        }
        String reedelkRuntimeVersion = ReedelkRuntimeVersion.from(finalRuntimeHomeDirectory);

        MavenUtil.runWhenInitialized(project, (DumbAwareRunnable) () -> {

            if (createRuntimeConfig) {
                // Create Runtime Run Configuration
                RuntimeRunConfigurationBuilder.build()
                        .withRuntimeConfigName(runtimeConfigName)
                        .withRuntimeHomeDirectory(finalRuntimeHomeDirectory)
                        .add(project);
            }

            // Add Module Run Configuration
            Module module = rootModel.getModule();
            ModuleRunConfigurationBuilder.build()
                    .withModuleName(module.getName())
                    .withRuntimeConfigName(runtimeConfigName)
                    .add(project);

            // Create Maven project files
            MavenId projectId = getProjectId();
            MavenId parentId = getParentMavenId();
            String sdkVersion = rootModel.getSdkName();

            MavenProjectBuilderHelper projectBuilder = new MavenProjectBuilderHelper(reedelkRuntimeVersion);
            projectBuilder.configure(project, projectId, parentId, root, sdkVersion);
        });


        // Create Default Init Project (Hello world):
        //  flows, configs, gitignore, dockerfile, heroku file ...
        ReedelkPluginUtil.runWhenInitialized(project, () -> {
            DefaultProjectBuilderHelper helper =
                    new DefaultProjectBuilderHelper(project, root, reedelkRuntimeVersion, isDownloadDistribution());
            helper.build();
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
                new MavenModuleWizardStep(this, wizardContext, false),
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

    @NotNull
    private Path copyDownloadPathDistributionInProject(String contentEntryPath) {
        String unzippedRuntimeDirectoryName = message("unzipped.runtime.directory.name");
        Path destination = Paths.get(contentEntryPath, unzippedRuntimeDirectoryName);
        try {
            // Copy all the files from TMP download path to project/reedelk-runtime directory.
            FileUtils.copyDirectory(tmpDownloadDistributionPath.toFile(), destination.toFile());

            // Change permission of reedelk-runtime.sh:
            // it needs to be executable for instance to start it on Heroku deployments.
            changeDistributionFilesPermissions(destination);

        } catch (IOException e) {
            // We cannot recover here. We should just display an error message.
            ApplicationManager.getApplication().invokeLater(() ->
                    showErrorDialog(
                            message("moduleBuilder.copy.distribution.error.message",
                                    tmpDownloadDistributionPath.toString(),
                                    destination.toString()),
                            message("moduleBuilder.copy.distribution.error.title")));
        }
        return destination;
    }

    private void changeDistributionFilesPermissions(Path destination) {
        Path runtimeStartSh = Paths.get(destination.toString(), ReedelkBundle.message("unzipped.runtime.start.sh.script"));
        String wantedPermissions = "rwxr-xr-x";
        ChangeFilesPermissions.to(runtimeStartSh, wantedPermissions);
    }
}
