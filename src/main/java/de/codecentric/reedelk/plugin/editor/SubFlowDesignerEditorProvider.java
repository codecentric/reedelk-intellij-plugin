package de.codecentric.reedelk.plugin.editor;

import de.codecentric.reedelk.plugin.graph.FlowGraphProvider;
import de.codecentric.reedelk.plugin.graph.FlowSnapshot;
import de.codecentric.reedelk.plugin.graph.manager.GraphManager;
import de.codecentric.reedelk.plugin.graph.manager.SubFlowGraphManager;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorPolicy;
import com.intellij.openapi.fileEditor.FileEditorProvider;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import de.codecentric.reedelk.plugin.editor.designer.action.DesignerPanelSubFlowActionHandler;
import de.codecentric.reedelk.plugin.filetype.SubFlowFileType;
import org.jetbrains.annotations.NotNull;

import static com.google.common.base.Preconditions.checkState;

public class SubFlowDesignerEditorProvider implements FileEditorProvider, DumbAware {

    /**
     * We create an editor if and only if the given file belongs to a module
     * (it might not necessarily belong to a module) AND the file type is 'Flow' type.
     */
    @Override
    public boolean accept(@NotNull Project project, @NotNull VirtualFile file) {
        Module module = ModuleUtil.findModuleForFile(file, project);
        return SubFlowFileType .class.equals(file.getFileType().getClass()) && module != null;
    }

    @NotNull
    @Override
    public FileEditor createEditor(@NotNull Project project, @NotNull VirtualFile file) {
        Module module = ModuleUtil.findModuleForFile(file, project);
        checkState(module != null, "module");

        FlowSnapshot snapshot = new FlowSnapshot();
        FlowGraphProvider graphProvider = FlowGraphProvider.get();
        GraphManager graphManager = new SubFlowGraphManager(module, file, snapshot, graphProvider);

        DesignerPanelSubFlowActionHandler handler = new DesignerPanelSubFlowActionHandler(module, snapshot);
        return new SubFlowDesignerEditor(module, snapshot, graphManager, handler);
    }

    @NotNull
    @Override
    public String getEditorTypeId() {
        return "reedelk-subflow-designer";
    }

    @NotNull
    @Override
    public FileEditorPolicy getPolicy() {
        return FileEditorPolicy.PLACE_BEFORE_DEFAULT_EDITOR;
    }
}
