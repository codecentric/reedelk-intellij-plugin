package com.reedelk.plugin.editor.properties.renderer.typestring;

import com.reedelk.plugin.editor.properties.commons.DynamicValueInputFieldAdapter;
import com.reedelk.plugin.editor.properties.commons.DynamicValueInputFieldBaseAdapter;
import com.reedelk.plugin.editor.properties.commons.StringInputField;
import com.reedelk.plugin.editor.properties.renderer.AbstractDynamicPropertyTypeRenderer;

public class DynamicStringPropertyRenderer extends AbstractDynamicPropertyTypeRenderer {

    @Override
    protected DynamicValueInputFieldAdapter inputFieldAdapter(String hint) {
        return new DynamicValueInputFieldBaseAdapter<>(new StringInputField(hint));
    }
}
