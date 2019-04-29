package com.esb.plugin.designer.graph.drawable;

import com.esb.component.Stop;
import com.esb.plugin.designer.editor.component.Component;
import com.esb.plugin.designer.editor.component.ComponentDescriptor;

public class StopDrawable extends AbstractDrawable {

    private static final ComponentDescriptor descriptor = ComponentDescriptor.create()
            .fullyQualifiedName(Stop.class.getName())
            .displayName(Stop.class.getSimpleName())
            .build();

    public StopDrawable() {
        super(new Component(descriptor));
    }

}
