package com.esb.plugin.designer.properties.renderer.type;

import com.esb.plugin.designer.properties.widget.input.FloatInputField;
import com.esb.plugin.designer.properties.widget.input.InputField;

public class FloatPropertyRenderer extends NumericPropertyRenderer<Float> {

    @Override
    protected InputField<Float> getInputField() {
        return new FloatInputField();
    }
}
