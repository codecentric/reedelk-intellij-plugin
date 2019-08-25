package com.reedelk.plugin.editor.properties.renderer;

import com.reedelk.plugin.editor.properties.widget.input.IntegerInputField;
import com.reedelk.plugin.editor.properties.widget.input.NumericInputField;

public class IntegerPropertyRenderer extends NumericPropertyRenderer<Integer> {
    @Override
    protected NumericInputField<Integer> getInputField(String hint) {
        return new IntegerInputField(hint);
    }
}
