package com.reedelk.plugin.editor.designer.widget;

import com.reedelk.plugin.commons.Colors;

import java.awt.*;

public class SelectedScopeBox extends ScopeBox {

    public SelectedScopeBox() {
        super(Colors.DESIGNER_SELECTED_SCOPE_BOX_BOUNDARIES,
                new BasicStroke(1f));
    }
}
