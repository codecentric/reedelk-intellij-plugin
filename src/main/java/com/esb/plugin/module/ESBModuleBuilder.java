package com.esb.plugin.module;

import com.esb.plugin.module.wizard.step.ConfigureRuntimeStep;
import com.esb.plugin.service.runtime.ESBRuntime;
import com.esb.plugin.service.runtime.ESBRuntimeService;
import com.esb.plugin.utils.ESBIcons;
import com.esb.plugin.utils.ESBLabel;
import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.ide.util.projectWizard.WizardContext;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.module.JavaModuleType;
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

    private ESBRuntime runtime;

    public ESBModuleBuilder() {
        setProjectId(defaultMavenId());
    }

    @Override
    public void setupRootModel(@NotNull ModifiableRootModel rootModel) {
        super.setupRootModel(rootModel);

        final Project project = rootModel.getProject();
        ESBRuntimeService runtimeService = ServiceManager.getService(ESBRuntimeService.class);
        if (!runtimeService.contains(runtime)) {
            runtimeService.addRuntime(runtime);
        }


        // Associate to this module the runtime




        final VirtualFile root = createAndGetContentEntry();
        rootModel.addContentEntry(root);

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
        ConfigureRuntimeStep step = new ConfigureRuntimeStep(context, this);
        Disposer.register(parentDisposable, step);
        return step;
    }

    @Override
    public String getParentGroup() {
        return JavaModuleType.BUILD_TOOLS_GROUP;
    }

    //TODO: To be removed since done by super.setRootModel
    private VirtualFile createAndGetContentEntry() {
        String path = FileUtil.toSystemIndependentName(getContentEntryPath());
        (new File(path)).mkdirs();
        return LocalFileSystem.getInstance().refreshAndFindFileByPath(path);
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

    public void setRuntime(ESBRuntime runtime) {
        this.runtime = runtime;
    }


}
