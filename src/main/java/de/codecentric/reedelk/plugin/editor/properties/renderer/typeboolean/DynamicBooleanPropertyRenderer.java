package de.codecentric.reedelk.plugin.editor.properties.renderer.typeboolean;

import de.codecentric.reedelk.plugin.editor.properties.commons.DynamicValueInputFieldAdapter;
import de.codecentric.reedelk.plugin.editor.properties.commons.ScriptEditorChangeListener;
import de.codecentric.reedelk.plugin.editor.properties.renderer.AbstractDynamicPropertyTypeRenderer;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

public class DynamicBooleanPropertyRenderer extends AbstractDynamicPropertyTypeRenderer {

    @Override
    protected DynamicValueInputFieldAdapter inputFieldAdapter(String hint) {
        return new DynamicValueInputFieldAdapter() {

            private BooleanCheckbox inputField = new BooleanCheckbox();

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
                inputField.setValue((Boolean) value);
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
