package com.reedelk.plugin.editor.properties.renderer.typemap;

import com.intellij.openapi.module.Module;
import com.reedelk.plugin.editor.properties.accessor.PropertyAccessor;

import javax.swing.*;

public class MapPropertyRenderer extends BaseMapPropertyRenderer {

    @Override
    protected JComponent getMapTabContainer(Module module, PropertyAccessor propertyAccessor) {
        return new MapPropertyTabContainer(propertyAccessor);
    }
}
