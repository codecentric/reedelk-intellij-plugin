package com.esb.plugin.designer.properties.renderer.type;

import com.esb.plugin.designer.properties.widget.LongInputField;
import com.esb.plugin.designer.properties.widget.NumericInputField;

public class LongPropertyRenderer extends AbstractPropertyRenderer<Long> {

    @Override
    protected NumericInputField<Long> getInputField() {
        return new LongInputField();
    }
}
