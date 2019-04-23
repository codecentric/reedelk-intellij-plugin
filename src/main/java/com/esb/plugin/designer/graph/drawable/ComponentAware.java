package com.esb.plugin.designer.graph.drawable;

import com.esb.plugin.designer.editor.component.ComponentDescriptor;

public interface ComponentAware {

    default ComponentDescriptor component() {
        return null;
    }

    default String displayName() {
        return null;
    }
}
