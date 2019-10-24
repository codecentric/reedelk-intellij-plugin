package com.reedelk.plugin.editor;

import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorPolicy;
import com.intellij.openapi.fileEditor.FileEditorProvider;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.reedelk.plugin.editor.designer.DesignerPanelActionHandler;
import com.reedelk.plugin.filetype.FlowFileType;
import com.reedelk.plugin.graph.FlowGraphProvider;
import com.reedelk.plugin.graph.FlowSnapshot;
import com.reedelk.plugin.graph.manager.FlowGraphManager;
import org.jetbrains.annotations.NotNull;

import static com.google.common.base.Preconditions.checkState;

public class FlowDesignerEditorProvider implements FileEditorProvider, DumbAware {

    @Override
    public boolean accept(@NotNull Project project, @NotNull VirtualFile file) {
        Module module = ModuleUtil.findModuleForFile(file, project);
        // We cannot accept to create the editor if the module is not present yet.
        // This check (module != null) fixes a but where a null pointer is thrown
        // when a newly imported project is imported and modules have not been added
        // yet to the Idea Project.
        return file.getFileType() == FlowFileType.INSTANCE && module != null;
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
