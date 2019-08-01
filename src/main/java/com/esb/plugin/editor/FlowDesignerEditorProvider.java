package com.esb.plugin.editor;

import com.esb.plugin.editor.designer.DesignerPanelActionHandler;
import com.esb.plugin.filetype.FlowFileType;
import com.esb.plugin.graph.FlowGraphProvider;
import com.esb.plugin.graph.FlowSnapshot;
import com.esb.plugin.graph.manager.FlowGraphManager;
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

public class FlowDesignerEditorProvider implements FileEditorProvider, DumbAware {

    @Override
    public boolean accept(@NotNull Project project, @NotNull VirtualFile file) {
        return file.getFileType() == FlowFileType.INSTANCE;
    }

    @NotNull
    @Override
    public FileEditor createEditor(@NotNull Project project, @NotNull VirtualFile file) {
        Module module = ModuleUtil.findModuleForFile(file, project);
        checkState(module != null, "Module must not be null");

        FlowSnapshot snapshot = new FlowSnapshot();
        FlowGraphProvider graphProvider = new FlowGraphProvider();
        FlowGraphManager graphManager = new FlowGraphManager(module, file, snapshot, graphProvider);

        DesignerPanelActionHandler handler = new FlowDesignerPanelActionHandler(module, snapshot);
        return new FlowDesignerEditor(module, snapshot, graphManager, handler);
    }

    @NotNull
    @Override
    public String getEditorTypeId() {
        return "esb-flow-designer";
    }

    @NotNull
    @Override
    public FileEditorPolicy getPolicy() {
        return FileEditorPolicy.PLACE_BEFORE_DEFAULT_EDITOR;
    }

}
