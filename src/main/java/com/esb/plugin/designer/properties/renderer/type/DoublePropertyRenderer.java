package com.esb.plugin.designer.properties.renderer.type;

import com.esb.plugin.designer.properties.widget.input.DoubleInputField;
import com.esb.plugin.designer.properties.widget.input.InputField;

public class DoublePropertyRenderer extends NumericPropertyRenderer<Double> {

    @Override
    protected InputField<Double> getInputField() {
        return new DoubleInputField();
    }

}
