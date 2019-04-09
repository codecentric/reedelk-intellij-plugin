package com.esb.plugin.designer.graph.drawable;

import com.esb.plugin.designer.editor.component.Component;

public interface ComponentAware {

    Component component();

    String displayName();
}
