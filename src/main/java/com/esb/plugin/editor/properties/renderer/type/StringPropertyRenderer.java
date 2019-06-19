package com.esb.plugin.editor.properties.renderer.type;

import com.esb.plugin.component.domain.ComponentPropertyDescriptor;
import com.esb.plugin.editor.properties.accessor.PropertyAccessor;
import com.esb.plugin.editor.properties.widget.input.InputField;
import com.esb.plugin.editor.properties.widget.input.StringInputField;
import com.esb.plugin.graph.FlowSnapshot;
import com.intellij.openapi.module.Module;

import javax.swing.*;

public class StringPropertyRenderer implements TypePropertyRenderer {

    @Override
    public JComponent render(Module module, ComponentPropertyDescriptor descriptor, PropertyAccessor accessor, FlowSnapshot snapshot) {
        InputField<String> field = new StringInputField();
        field.setValue(accessor.get());
        field.addListener(val -> {
            accessor.set(val);
            snapshot.onDataChange();
        });
        return field;
    }
}
