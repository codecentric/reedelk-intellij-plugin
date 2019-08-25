package com.reedelk.plugin.editor.properties.renderer;

import com.reedelk.plugin.editor.properties.widget.input.FloatInputField;
import com.reedelk.plugin.editor.properties.widget.input.InputField;

public class FloatPropertyRenderer extends NumericPropertyRenderer<Float> {
    @Override
    protected InputField<Float> getInputField(String hint) {
        return new FloatInputField(hint);
    }
}
