package com.esb.plugin.editor;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.AncestorListenerAdapter;

import javax.swing.event.AncestorEvent;

public class AncestorListener extends AncestorListenerAdapter {

    private final DesignerVisibleNotifier designerVisibleNotifier;
    private final VirtualFile file;

    public AncestorListener(Project project, VirtualFile file) {
        this.designerVisibleNotifier = project.getMessageBus().syncPublisher(DesignerVisibleNotifier.DESIGNER_VISIBLE);
        this.file = file;
    }

    @Override
    public void ancestorAdded(AncestorEvent event) {
        this.designerVisibleNotifier.onDesignerVisible(file);
    }
}
