package com.reedelk.plugin.editor.properties.renderer.typefloat;

import com.reedelk.plugin.editor.properties.commons.InputField;
import com.reedelk.plugin.editor.properties.renderer.AbstractNumericPropertyRenderer;

public class FloatPropertyRenderer extends AbstractNumericPropertyRenderer<Float> {
    @Override
    protected InputField<Float> getInputField(String hint) {
        return new FloatInputField(hint);
    }
}
