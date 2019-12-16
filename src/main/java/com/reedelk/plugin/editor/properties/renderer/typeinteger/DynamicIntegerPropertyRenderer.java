package com.reedelk.plugin.editor.properties.renderer.typeinteger;

import com.reedelk.plugin.editor.properties.renderer.AbstractDynamicPropertyTypeRenderer;
import com.reedelk.plugin.editor.properties.renderer.commons.DynamicValueInputFieldAdapter;
import com.reedelk.plugin.editor.properties.renderer.commons.DynamicValueInputFieldBaseAdapter;

public class DynamicIntegerPropertyRenderer extends AbstractDynamicPropertyTypeRenderer {

    @Override
    protected DynamicValueInputFieldAdapter inputFieldAdapter(String hint) {
        return new DynamicValueInputFieldBaseAdapter<>(new IntegerInputField(hint));
    }
}
