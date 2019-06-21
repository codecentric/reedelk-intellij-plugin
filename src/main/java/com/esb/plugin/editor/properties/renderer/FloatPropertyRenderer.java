package com.esb.plugin.editor.properties.renderer;

import com.esb.plugin.editor.properties.widget.input.FloatInputField;
import com.esb.plugin.editor.properties.widget.input.InputField;

public class FloatPropertyRenderer extends NumericPropertyRenderer<Float> {

    @Override
    protected InputField<Float> getInputField() {
        return new FloatInputField();
    }
}
