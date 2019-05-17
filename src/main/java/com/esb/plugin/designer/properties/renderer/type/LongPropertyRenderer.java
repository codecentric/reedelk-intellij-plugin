package com.esb.plugin.designer.properties.renderer.type;

import com.esb.plugin.designer.properties.widget.InputField;
import com.esb.plugin.designer.properties.widget.LongInputField;

public class LongPropertyRenderer extends AbstractPropertyRenderer<Long> {

    @Override
    protected InputField<Long> getInputField() {
        return new LongInputField();
    }
}
