package com.reedelk.plugin.editor.properties.renderer;

import com.reedelk.plugin.editor.properties.widget.input.FloatInputField;
import com.reedelk.plugin.editor.properties.widget.input.script.DynamicValueField;
import com.reedelk.plugin.editor.properties.widget.input.script.InputFieldAdapter;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

public class DynamicFloatPropertyRenderer extends AbstractDynamicPropertyRenderer {

    @Override
    protected InputFieldAdapter inputFieldAdapter(String hint) {
        return new InputFieldAdapter() {

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
            public void addListener(DynamicValueField.OnChangeListener listener) {
                inputField.addListener(listener::onChange);
            }
        };
    }
}
