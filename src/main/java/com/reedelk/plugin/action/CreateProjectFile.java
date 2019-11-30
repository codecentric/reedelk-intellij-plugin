package com.reedelk.plugin.action;


import com.intellij.ide.actions.CreateFileAction;
import com.intellij.ide.actions.CreateFileFromTemplateAction;
import com.intellij.ide.actions.CreateFileFromTemplateDialog;
import com.intellij.ide.fileTemplates.FileTemplate;
import com.intellij.ide.fileTemplates.FileTemplateManager;
import com.intellij.ide.fileTemplates.FileTemplateUtil;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.SmartPointerManager;
import com.intellij.psi.SmartPsiElementPointer;
import com.intellij.psi.impl.file.PsiDirectoryFactory;
import com.intellij.util.IncorrectOperationException;
import com.reedelk.plugin.commons.Icons;
import com.reedelk.plugin.commons.IsResourceDirectory;
import com.reedelk.plugin.commons.Template;
import com.reedelk.plugin.template.FlowOrSubFlowFileProperties;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

import static com.reedelk.plugin.message.ReedelkBundle.message;
import static com.reedelk.runtime.commons.ModuleProperties.Flow.RESOURCE_DIRECTORY;

public class CreateProjectFile extends CreateFileFromTemplateAction implements DumbAware {

    public CreateProjectFile() {
        super(message("action.create.flow.title"),
                message("action.create.flow.description"),
                Icons.FileTypeFlow);
    }

    @Override
    protected void buildDialog(Project project, PsiDirectory directory, CreateFileFromTemplateDialog.Builder builder) {
        builder.setTitle(message("action.create.flow.title"))
                .addKind(message("action.create.kind.flow"), Icons.FileTypeFlow, Template.ProjectFile.FLOW)
                .addKind(message("action.create.kind.subflow"), Icons.FileTypeSubFlow, Template.ProjectFile.SUBFLOW);
    }

    @Override
    protected String getActionName(PsiDirectory directory, String newName, String templateName) {
        return message("action.create.flow.name");
    }

    @Override
    protected PsiFile createFile(String name, String templateName, PsiDirectory dir) {
        PsiDirectory realDir = getBestDirectoryForFlowFiles(dir);
        if (name != null) {
            CreateFileAction.MkDirs mkdirs = new CreateFileAction.MkDirs(name, realDir);
            name = mkdirs.newName;
            realDir = mkdirs.directory;
        }

        final FileTemplate template = FileTemplateManager.getInstance(realDir.getProject()).getInternalTemplate(templateName);
        final Properties templateProperties = new FlowOrSubFlowFileProperties(name, templateName);
        final Project project = realDir.getProject();
        try {
            PsiFile psiFile = FileTemplateUtil
                    .createFromTemplate(template, name, templateProperties, realDir)
                    .getContainingFile();
            SmartPsiElementPointer<PsiFile> pointer = SmartPointerManager.getInstance(project)
                    .createSmartPsiElementPointer(psiFile);
            VirtualFile virtualFile = psiFile.getVirtualFile();
            if (virtualFile != null) {
                // Open the newly created file.
                FileEditorManager.getInstance(project).openFile(virtualFile, true);
                return pointer.getElement();
            }
            return null;
        } catch (Exception exception) {
            LOG.error(exception);
            throw new IncorrectOperationException(
                    message("action.create.file.error.template.parsing",
                            templateName,
                            exception.getMessage()));
        }
    }

    /**
     * This method tries to help out the user by returning the flows directory if the parent is 'resources'.
     * Flows can only be in the flows directory.
     */
    private PsiDirectory getBestDirectoryForFlowFiles(PsiDirectory current) {
        PsiDirectory realDir = current;
        if (IsResourceDirectory.of(current)) {
            PsiDirectory flows = current.findSubdirectory(RESOURCE_DIRECTORY);
            if (flows != null) {
                // The 'Flows' directory was found, so we return it right away.
                realDir = flows;
            } else {
                // We create 'Flows' directory
                String presentableUrl = current.getVirtualFile().getPresentableUrl();
                Path flowsDirectory = Paths.get(presentableUrl, RESOURCE_DIRECTORY);
                VirtualFile directoryIfMissing;
                try {
                    directoryIfMissing = VfsUtil.createDirectoryIfMissing(flowsDirectory.toString());
                } catch (IOException exception) {
                    return current;
                }
                if (directoryIfMissing != null) {
                    realDir = PsiDirectoryFactory.getInstance(current.getProject()).createDirectory(directoryIfMissing);
                }
            }
        }
        return realDir;
    }
}
