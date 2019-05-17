package com.esb.plugin.designer.properties.renderer.type;

import com.esb.plugin.designer.properties.widget.InputField;
import com.esb.plugin.designer.properties.widget.StringInputField;

public class StringPropertyRenderer extends AbstractPropertyRenderer<String> {

    @Override
    protected InputField<String> getInputField() {
        return new StringInputField();
    }

}
