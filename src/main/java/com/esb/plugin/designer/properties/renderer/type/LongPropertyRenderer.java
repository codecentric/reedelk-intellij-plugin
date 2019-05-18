package com.esb.plugin.designer.properties.renderer.type;

import com.esb.plugin.designer.properties.widget.input.LongInputField;
import com.esb.plugin.designer.properties.widget.input.NumericInputField;

public class LongPropertyRenderer extends NumericPropertyRenderer<Long> {

    @Override
    protected NumericInputField<Long> getInputField() {
        return new LongInputField();
    }
}
