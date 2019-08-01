package com.esb.plugin.action;

import com.esb.plugin.commons.Icons;
import com.esb.plugin.commons.Template;
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
import org.apache.velocity.runtime.parser.ParseException;

import java.util.Properties;

public class CreateProjectFile extends CreateFileFromTemplateAction implements DumbAware {

    private static final String CREATE_FILE = "Reedelk project file";

    public CreateProjectFile() {
        super(CREATE_FILE, "Create a new Reedelk project file", Icons.FileTypeFlow);
    }

    @Override
    protected void buildDialog(Project project, PsiDirectory directory, CreateFileFromTemplateDialog.Builder builder) {
        builder.setTitle(CREATE_FILE)
                .addKind("Flow", Icons.FileTypeFlow, Template.ProjectFile.FLOW)
                .addKind("Subflow", Icons.FileTypeSubFlow, Template.ProjectFile.SUBFLOW);
    }

    @Override
    protected String getActionName(PsiDirectory directory, String newName, String templateName) {
        return "Create Reedelk project file";
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
            throw new IncorrectOperationException("Error parsing Velocity template: " + e.getMessage(), (Throwable) e);
        } catch (IncorrectOperationException e) {
            throw e;
        } catch (Exception e) {
            LOG.error(e);
            return null;
        }
    }
}