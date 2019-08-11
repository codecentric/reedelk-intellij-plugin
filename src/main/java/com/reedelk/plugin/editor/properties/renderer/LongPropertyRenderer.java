package com.reedelk.plugin.editor.properties.renderer;

import com.reedelk.plugin.editor.properties.widget.input.LongInputField;
import com.reedelk.plugin.editor.properties.widget.input.NumericInputField;

public class LongPropertyRenderer extends NumericPropertyRenderer<Long> {
    @Override
    protected NumericInputField<Long> getInputField() {
        return new LongInputField();
    }
}
