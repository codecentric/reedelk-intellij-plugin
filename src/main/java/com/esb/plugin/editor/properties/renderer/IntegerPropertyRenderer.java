package com.esb.plugin.editor.properties.renderer;

import com.esb.plugin.editor.properties.widget.input.IntegerInputField;
import com.esb.plugin.editor.properties.widget.input.NumericInputField;

public class IntegerPropertyRenderer extends NumericPropertyRenderer<Integer> {

    @Override
    protected NumericInputField<Integer> getInputField() {
        return new IntegerInputField();
    }

}
