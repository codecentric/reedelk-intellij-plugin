package com.esb.plugin.editor.properties.renderer;

import com.esb.plugin.editor.properties.widget.input.LongInputField;
import com.esb.plugin.editor.properties.widget.input.NumericInputField;

public class LongPropertyRenderer extends NumericPropertyRenderer<Long> {
    @Override
    protected NumericInputField<Long> getInputField() {
        return new LongInputField();
    }
}
