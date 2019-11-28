package com.reedelk.plugin.editor.properties.renderer.typelong;

import com.reedelk.plugin.editor.properties.renderer.AbstractNumericPropertyRenderer;
import com.reedelk.plugin.editor.properties.renderer.commons.InputField;

public class LongPropertyRenderer extends AbstractNumericPropertyRenderer<Long> {
    @Override
    protected InputField<Long> getInputField(String hint) {
        return new LongInputField(hint);
    }
}
