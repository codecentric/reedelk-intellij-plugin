package com.esb.plugin.designer.graph.drawable;

import com.esb.plugin.designer.editor.component.Component;

public interface ComponentAware {

    default Component component() {
        return null;
    }

    default String displayName() {
        return null;
    }
}
