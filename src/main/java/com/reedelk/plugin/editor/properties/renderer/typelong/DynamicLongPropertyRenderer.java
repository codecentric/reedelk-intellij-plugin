package com.reedelk.plugin.editor.properties.renderer.typelong;

import com.reedelk.plugin.editor.properties.renderer.AbstractDynamicPropertyTypeRenderer;
import com.reedelk.plugin.editor.properties.renderer.commons.DynamicValueField;
import com.reedelk.plugin.editor.properties.renderer.commons.DynamicValueInputFieldAdapter;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

public class DynamicLongPropertyRenderer extends AbstractDynamicPropertyTypeRenderer {

    @Override
    protected DynamicValueInputFieldAdapter inputFieldAdapter(String hint) {
        return new DynamicValueInputFieldAdapter() {

            private LongInputField inputField = new LongInputField(hint);

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
                inputField.setValue((Long) value);
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
