package com.reedelk.plugin.commons;

import com.intellij.ui.Gray;
import com.intellij.ui.JBColor;

import java.awt.*;

public class Colors {

    private Colors() {
    }

    public static final JBColor PROPERTIES_EMPTY_SELECTION_BACKGROUND = new JBColor(new Color(237, 237, 237), new Color(237, 237, 237));
    public static final JBColor FOREGROUND_TEXT = new JBColor(new Color(153, 153, 153), new Color(153, 153, 153));

    public static final Color SCRIPT_EDITOR_CONTEXT_PANEL_BORDER = JBColor.LIGHT_GRAY;
    public static final Color SCRIPT_EDITOR_CONTEXT_PANEL_TITLE_BG = new Color(226, 230, 236);
    public static final Color SCRIPT_EDITOR_CONTEXT_PANEL_BORDER_BOTTOM = new Color(201, 201, 201);

    public static final Color SCRIPT_EDITOR_INLINE_ICON_BORDER = Color.LIGHT_GRAY;

    public static final JBColor DESIGNER_BG = JBColor.WHITE;
    public static final JBColor SCOPE_VERTICAL_DIVIDER = new JBColor(Gray._200, Gray._30);
    public static final JBColor DESIGNER_SELECTED_COMPONENT_BG = new JBColor(Gray._245, Gray._245);
    public static final JBColor DESIGNER_UNSELECTED_SCOPE_BOX_BOUNDARIES = new JBColor(Gray._235, Gray._30);
    public static final JBColor DESIGNER_SELECTED_SCOPE_BOX_BOUNDARIES = new JBColor(Gray._170, Gray._30);
    public static final JBColor DESIGNER_ARROW = JBColor.lightGray;
    public static final JBColor DESIGNER_INBOUND_LANE_VERTICAL_BAR = JBColor.GRAY;

    public static final JBColor TEXT_FLOW_DESCRIPTION = JBColor.GRAY;
    public static final JBColor TEXT_FLOW_TITLE = JBColor.GRAY;
    public static final JBColor TEXT_INBOUND_LANE = JBColor.GRAY;
    public static final JBColor TEXT_COMPONENT_DESCRIPTION = JBColor.LIGHT_GRAY;
    public static final JBColor TEXT_COMPONENT_DESCRIPTION_SELECTED = JBColor.GRAY;
    public static final JBColor TEXT_COMPONENT_TITLE = JBColor.GRAY;
    public static final JBColor TEXT_COMPONENT_TITLE_SELECTED = JBColor.DARK_GRAY;
    public static final JBColor TEXT_DEFAULT_ROUTE = JBColor.GRAY;

    public static final JBColor PALETTE_TEXT_SELECTED = JBColor.WHITE;
    public static final JBColor PALETTE_TEXT_UNSELECTED = JBColor.DARK_GRAY;

    public static final JBColor CONTAINER_OBJECT_TYPE_COLLAPSIBLE_BORDER = JBColor.LIGHT_GRAY;
    public static final JBColor HINT_COLOR = new JBColor(new Color(224, 69, 67), new Color(224, 69, 67));

}
