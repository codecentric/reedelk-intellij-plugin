package de.codecentric.reedelk.plugin.commons;

import com.intellij.codeInsight.daemon.impl.analysis.FileHighlightingSetting;
import com.intellij.codeInsight.daemon.impl.analysis.HighlightingSettingsPerFile;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;

import java.nio.file.Paths;
import java.util.Optional;

public class DisableInspectionFor {

    private DisableInspectionFor() {
    }

    public static void file(Module module, String filePath) {
        PluginModuleUtils.getScriptsDirectory(module).ifPresent(scriptsFolder -> {
            VirtualFile theFile = VfsUtil.findFile(Paths.get(scriptsFolder, filePath), true);
            if (theFile == null) return;
            file(module.getProject(), theFile);
        });
    }

    public static void file(Project project, VirtualFile theFile) {
        Document document = FileDocumentManager.getInstance().getDocument(theFile);
        if (document == null) return;

        PsiFile psiFile = PsiDocumentManager.getInstance(project).getPsiFile(document);
        Optional.ofNullable(psiFile).ifPresent(file ->
                HighlightingSettingsPerFile.getInstance(project)
                        .setHighlightingSettingForRoot(file, FileHighlightingSetting.SKIP_INSPECTION));
    }
}
