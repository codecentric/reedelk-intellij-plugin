package com.esb.plugin.designer.properties.renderer.type;

import com.esb.plugin.designer.properties.widget.input.InputField;
import com.esb.plugin.designer.properties.widget.input.StringInputField;

public class StringPropertyRenderer extends AbstractPropertyRenderer<String> {

    @Override
    protected InputField<String> getInputField() {
        return new StringInputField();
    }

}
