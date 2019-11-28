package com.reedelk.plugin.editor.properties.renderer.typefloat;

import com.reedelk.plugin.editor.properties.renderer.AbstractDynamicPropertyTypeRenderer;
import com.reedelk.plugin.editor.properties.renderer.commons.DynamicValueInputFieldAdapter;
import com.reedelk.plugin.editor.properties.renderer.commons.ScriptEditorChangeListener;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

public class DynamicFloatPropertyRenderer extends AbstractDynamicPropertyTypeRenderer {

    @Override
    protected DynamicValueInputFieldAdapter inputFieldAdapter(String hint) {
        return new DynamicValueInputFieldAdapter() {

            private FloatInputField inputField = new FloatInputField(hint);

            @Override
            public Object getValue() {
                return inputField.getValue();
            }

            @Override
            public JComponent getComponent() {
                return inputField;
            }

            @Override
            public void setFont(Font font) {
                inputField.setFont(font);
            }

            @Override
            public void setValue(Object value) {
                inputField.setValue((Float) value);
            }

            @Override
            public void setMargin(Insets insets) {
                inputField.setMargin(insets);
            }

            @Override
            public void setBorder(Border border) {
                inputField.setBorder(border);
            }

            @Override
            public void addListener(ScriptEditorChangeListener listener) {
                inputField.addListener(listener::onChange);
            }
        };
    }
}
