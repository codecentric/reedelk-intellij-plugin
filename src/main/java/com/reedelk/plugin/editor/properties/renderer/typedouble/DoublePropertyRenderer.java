package com.reedelk.plugin.editor.properties.renderer.typedouble;

import com.reedelk.plugin.editor.properties.renderer.AbstractNumericPropertyRenderer;
import com.reedelk.plugin.editor.properties.renderer.commons.InputField;

public class DoublePropertyRenderer extends AbstractNumericPropertyRenderer<Double> {
    @Override
    protected InputField<Double> getInputField(String hint) {
        return new DoubleInputField(hint);
    }
}
