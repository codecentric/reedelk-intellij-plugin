package com.reedelk.plugin.editor.properties.renderer.typeinteger;

import com.reedelk.plugin.editor.properties.commons.NumericInputField;
import com.reedelk.plugin.editor.properties.renderer.AbstractNumericPropertyRenderer;

public class IntegerPropertyRenderer extends AbstractNumericPropertyRenderer<Integer> {
    @Override
    protected NumericInputField<Integer> getInputField(String hint) {
        return new IntegerInputField(hint);
    }
}
