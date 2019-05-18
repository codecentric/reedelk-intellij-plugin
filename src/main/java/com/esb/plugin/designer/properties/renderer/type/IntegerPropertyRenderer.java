package com.esb.plugin.designer.properties.renderer.type;

import com.esb.plugin.designer.properties.widget.input.IntegerInputField;
import com.esb.plugin.designer.properties.widget.input.NumericInputField;

public class IntegerPropertyRenderer extends NumericPropertyRenderer<Integer> {

    @Override
    protected NumericInputField<Integer> getInputField() {
        return new IntegerInputField();
    }

}
