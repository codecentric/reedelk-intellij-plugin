package com.esb.plugin.editor;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.messages.Topic;

public interface DesignerVisibleNotifier {

    Topic<DesignerVisibleNotifier> DESIGNER_VISIBLE = Topic.create("Designer Visible", DesignerVisibleNotifier.class);

    void onDesignerVisible(VirtualFile virtualFile);

}
