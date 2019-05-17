package com.esb.plugin.designer.properties.renderer.type;

import com.esb.plugin.designer.properties.widget.IntegerInputField;
import com.esb.plugin.designer.properties.widget.NumericInputField;

public class IntegerPropertyRenderer extends AbstractPropertyRenderer<Integer> {

    @Override
    protected NumericInputField<Integer> getInputField() {
        return new IntegerInputField();
    }
}
