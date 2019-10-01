package com.reedelk.plugin.editor.properties.renderer;

import com.reedelk.plugin.editor.properties.widget.input.BigIntegerInputField;
import com.reedelk.plugin.editor.properties.widget.input.script.DynamicValueField;
import com.reedelk.plugin.editor.properties.widget.input.script.InputFieldAdapter;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.math.BigInteger;

public class DynamicBigIntegerPropertyRenderer extends AbstractDynamicPropertyRenderer {

    @Override
    protected InputFieldAdapter inputFieldAdapter(String hint) {
        return new InputFieldAdapter() {

            private BigIntegerInputField inputField = new BigIntegerInputField(hint);

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
                inputField.setValue((BigInteger) value);
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
