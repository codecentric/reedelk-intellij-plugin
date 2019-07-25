package com.esb.plugin.editor.properties.renderer;

import com.esb.plugin.editor.properties.widget.input.DoubleInputField;
import com.esb.plugin.editor.properties.widget.input.InputField;

public class DoublePropertyRenderer extends NumericPropertyRenderer<Double> {
    @Override
    protected InputField<Double> getInputField() {
        return new DoubleInputField();
    }
}
