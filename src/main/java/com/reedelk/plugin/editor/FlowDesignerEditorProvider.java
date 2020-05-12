package com.reedelk.plugin.editor;

import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorPolicy;
import com.intellij.openapi.fileEditor.FileEditorProvider;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.reedelk.plugin.editor.designer.action.DesignerPanelFlowActionHandler;
import com.reedelk.plugin.editor.designer.dnd.DesignerPanelActionHandler;
import com.reedelk.plugin.filetype.FlowFileType;
import com.reedelk.plugin.graph.FlowGraphProvider;
import com.reedelk.plugin.graph.FlowSnapshot;
import com.reedelk.plugin.graph.manager.FlowGraphManager;
import org.jetbrains.annotations.NotNull;

import static com.google.common.base.Preconditions.checkState;

public class FlowDesignerEditorProvider implements FileEditorProvider, DumbAware {

    /**
     * We create an editor if and only if the given file belongs to a module
     * (it might not necessarily belong to a module) AND the file type is 'Flow' type.
     */
    @Override
    public boolean accept(@NotNull Project project, @NotNull VirtualFile file) {
        Module module = ModuleUtil.findModuleForFile(file, project);
        return FlowFileType.class.equals(file.getFileType().getClass()) && module != null;
    }

    @NotNull
    @Override
    public FileEditor createEditor(@NotNull Project project, @NotNull VirtualFile file) {
        Module module = ModuleUtil.findModuleForFile(file, project);
        checkState(module != null, "module");

        FlowSnapshot snapshot = new FlowSnapshot();
        FlowGraphProvider graphProvider = new FlowGraphProvider();
        FlowGraphManager graphManager = new FlowGraphManager(module, file, snapshot, graphProvider);

        DesignerPanelActionHandler handler = new DesignerPanelFlowActionHandler(module, snapshot);
        return new FlowDesignerEditor(module, snapshot, graphManager, handler);
    }

    @NotNull
    @Override
    public String getEditorTypeId() {
        return "reedelk-flow-designer";
    }

    @NotNull
    @Override
    public FileEditorPolicy getPolicy() {
        return FileEditorPolicy.PLACE_BEFORE_DEFAULT_EDITOR;
    }
}
