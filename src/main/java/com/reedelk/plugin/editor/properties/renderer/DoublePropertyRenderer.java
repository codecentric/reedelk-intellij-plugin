package com.reedelk.plugin.editor.properties.renderer;

import com.reedelk.plugin.editor.properties.widget.input.DoubleInputField;
import com.reedelk.plugin.editor.properties.widget.input.InputField;

public class DoublePropertyRenderer extends NumericPropertyRenderer<Double> {
    @Override
    protected InputField<Double> getInputField() {
        return new DoubleInputField();
    }
}
