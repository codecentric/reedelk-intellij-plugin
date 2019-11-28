package com.reedelk.plugin.editor.properties.renderer.typebiginteger;

import com.reedelk.plugin.editor.properties.renderer.AbstractDynamicPropertyTypeRenderer;
import com.reedelk.plugin.editor.properties.renderer.commons.DynamicValueField;
import com.reedelk.plugin.editor.properties.renderer.commons.DynamicValueInputFieldAdapter;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.math.BigInteger;

public class DynamicBigIntegerPropertyRenderer extends AbstractDynamicPropertyTypeRenderer {

    @Override
    protected DynamicValueInputFieldAdapter inputFieldAdapter(String hint) {
        return new DynamicValueInputFieldAdapter() {

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
