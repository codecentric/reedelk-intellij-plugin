package com.reedelk.plugin.editor.properties.renderer;

import com.reedelk.plugin.editor.properties.widget.input.InputField;
import com.reedelk.plugin.editor.properties.widget.input.LongInputField;

public class LongPropertyRenderer extends NumericPropertyRenderer<Long> {
    @Override
    protected InputField<Long> getInputField(String hint) {
        return new LongInputField(hint);
    }
}
