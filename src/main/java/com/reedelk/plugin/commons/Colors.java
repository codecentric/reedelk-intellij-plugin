package com.reedelk.plugin.commons;

import com.intellij.ui.Gray;
import com.intellij.ui.JBColor;

import java.awt.*;

public class Colors {

    private Colors() {
    }

    // Base Reedelk Color Theme
    private static final Color REEDELK_COLOR_TERTIARY = new Color(224, 69, 67);

    public static final JBColor PROPERTIES_TABS_TITLE = JBColor.DARK_GRAY;

    public static final JBColor PROPERTIES_EMPTY_SELECTION_BACKGROUND = new JBColor(JBColor.background(), JBColor.background());
    public static final JBColor FOREGROUND_TEXT = new JBColor(Gray._153, Gray._153);

    public static final Color SCRIPT_EDITOR_CONTEXT_PANEL_BORDER = JBColor.LIGHT_GRAY;
    public static final Color SCRIPT_EDITOR_CONTEXT_PANEL_TITLE_BG = new Color(226, 230, 236);
    public static final Color SCRIPT_EDITOR_CONTEXT_PANEL_BORDER_BOTTOM = Gray._201;
    public static final Color SCRIPT_EDITOR_INLINE_ICON_BORDER = JBColor.LIGHT_GRAY;
    public static final Color SCRIPT_EDITOR_DIALOG_BACKGROUND = JBColor.WHITE;

    public static final JBColor DESIGNER_BG = JBColor.WHITE;

    public static final JBColor DESIGNER_STROKE_COLOR = new JBColor(Gray._192, Gray._128);

    public static final JBColor SCOPE_VERTICAL_DIVIDER = DESIGNER_STROKE_COLOR;
    public static final JBColor DESIGNER_SELECTED_COMPONENT_BG = new JBColor(Gray._245, Gray._90);
    public static final JBColor DESIGNER_UNSELECTED_SCOPE_BOX_BOUNDARIES = DESIGNER_STROKE_COLOR;
    public static final JBColor DESIGNER_SELECTED_SCOPE_BOX_BOUNDARIES = new JBColor(Gray._120, Gray._160);


    public static final JBColor DESIGNER_INBOUND_LANE_VERTICAL_BAR = JBColor.GRAY;

    public static final JBColor TEXT_COMPONENT_DESCRIPTION = new JBColor(Gray._128, Gray._180);
    public static final JBColor TEXT_COMPONENT_DESCRIPTION_SELECTED = new JBColor(Gray._64, Gray._210);
    public static final JBColor TEXT_COMPONENT_TITLE = new JBColor(Gray._192, Gray._150);
    public static final JBColor TEXT_COMPONENT_TITLE_SELECTED = new JBColor(Gray._128, Gray._192);
    public static final JBColor TEXT_DEFAULT_ROUTE = JBColor.GRAY;

    public static final JBColor TEXT_FLOW_DESCRIPTION = TEXT_COMPONENT_DESCRIPTION;
    public static final JBColor TEXT_FLOW_TITLE = TEXT_COMPONENT_DESCRIPTION;
    public static final JBColor TEXT_INBOUND_LANE = TEXT_COMPONENT_DESCRIPTION;

    public static final JBColor PALETTE_TEXT_SELECTED = new JBColor(Color.white, Color.white);
    public static final JBColor PALETTE_TEXT_UNSELECTED = JBColor.DARK_GRAY;

    public static final JBColor HINT_COLOR = new JBColor(REEDELK_COLOR_TERTIARY, REEDELK_COLOR_TERTIARY);

    public static final JBColor INPUT_FIELD_HINT = JBColor.LIGHT_GRAY;
    public static final JBColor FILE_INPUT_FIELD_HINT = JBColor.DARK_GRAY;

    public static final Color TYPE_OBJECT_HORIZONTAL_SEPARATOR = JBColor.LIGHT_GRAY;

    public static final Color TOOL_WINDOW_PROPERTIES_TEXT = JBColor.DARK_GRAY;

    public static final JBColor METADATA_PANEL_BTN_ON_HOVER_BG = new JBColor(Gray._225, Gray._225);
    public static final JBColor METADATA_PANEL_BTN_BOTTOM_BORDER = new JBColor(new Color(59, 121, 197, 200), new Color(59, 121, 197, 200));

}
