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
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.SmartPointerManager;
import com.intellij.psi.SmartPsiElementPointer;
import com.intellij.util.IncorrectOperationException;
import com.reedelk.plugin.commons.Icons;
import com.reedelk.plugin.commons.Template;
import org.apache.velocity.runtime.parser.ParseException;

import java.util.Properties;

import static com.reedelk.plugin.message.ReedelkBundle.message;

/**
 * TODO: This one  creates flow and sublfows. We must put them inside the resources/flow and so  on.
 */
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
        if (name != null) {
            CreateFileAction.MkDirs mkdirs = new CreateFileAction.MkDirs(name, dir);
            name = mkdirs.newName;
            dir = mkdirs.directory;
        }

        final FileTemplate template = FileTemplateManager.getInstance(dir.getProject()).getInternalTemplate(templateName);
        Properties templateProperties = new ProjectFileProperties(name, templateName);

        Project project = dir.getProject();
        try {
            PsiFile psiFile =
                    FileTemplateUtil.createFromTemplate(template, name, templateProperties, dir).getContainingFile();
            SmartPsiElementPointer<PsiFile> pointer =
                    SmartPointerManager.getInstance(project).createSmartPsiElementPointer(psiFile);
            VirtualFile virtualFile = psiFile.getVirtualFile();
            if (virtualFile != null) {
                FileEditorManager.getInstance(project).openFile(virtualFile, true);
                return pointer.getElement();
            }
            return null;
        } catch (ParseException e) {
            throw new IncorrectOperationException("Error parsing template: " + e.getMessage(), (Throwable) e);
        } catch (IncorrectOperationException e) {
            throw e;
        } catch (Exception e) {
            LOG.error(e);
            return null;
        }
    }
}
