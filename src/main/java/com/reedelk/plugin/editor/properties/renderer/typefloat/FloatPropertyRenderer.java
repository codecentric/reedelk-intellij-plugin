package com.reedelk.plugin.editor.properties.renderer.typefloat;

import com.reedelk.plugin.editor.properties.renderer.AbstractNumericPropertyRenderer;
import com.reedelk.plugin.editor.properties.renderer.commons.InputField;

public class FloatPropertyRenderer extends AbstractNumericPropertyRenderer<Float> {
    @Override
    protected InputField<Float> getInputField(String hint) {
        return new FloatInputField(hint);
    }
}
