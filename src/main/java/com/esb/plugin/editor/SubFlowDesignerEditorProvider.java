package com.esb.plugin.editor;

import com.esb.plugin.filetype.SubFlowFileType;
import com.esb.plugin.graph.FlowGraphProvider;
import com.esb.plugin.graph.GraphSnapshot;
import com.esb.plugin.graph.manager.GraphManager;
import com.esb.plugin.graph.manager.SubFlowGraphManager;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorPolicy;
import com.intellij.openapi.fileEditor.FileEditorProvider;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

import static com.google.common.base.Preconditions.checkState;

public class SubFlowDesignerEditorProvider implements FileEditorProvider, DumbAware {

    @Override
    public boolean accept(@NotNull Project project, @NotNull VirtualFile file) {
        return file.getFileType() == SubFlowFileType.INSTANCE;
    }

    @NotNull
    @Override
    public FileEditor createEditor(@NotNull Project project, @NotNull VirtualFile file) {
        Module module = ModuleUtil.findModuleForFile(file, project);
        // TODO: The module here might be null!
        checkState(module != null, "Module must not be null");

        FlowGraphProvider graphProvider = new FlowGraphProvider();
        GraphSnapshot snapshot = new GraphSnapshot(graphProvider);
        GraphManager graphManager = new SubFlowGraphManager(project, module, file, snapshot, graphProvider);

        SubFlowDesignerPanelActionHandler handler = new SubFlowDesignerPanelActionHandler(module, snapshot);
        return new SubFlowDesignerEditor(snapshot, graphManager, handler);
    }

    @NotNull
    @Override
    public String getEditorTypeId() {
        return "esb-subflow-designer";
    }

    @NotNull
    @Override
    public FileEditorPolicy getPolicy() {
        return FileEditorPolicy.PLACE_BEFORE_DEFAULT_EDITOR;
    }
}
