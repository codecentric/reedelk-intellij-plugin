package com.reedelk.plugin.editor.designer.misc;

import com.reedelk.plugin.commons.Images;

import static com.reedelk.plugin.commons.Messages.Flow;

public class BuildingFlowInfoPanel extends BaseInfoPanel {

    public BuildingFlowInfoPanel() {
        super(Flow.BUILDING_FLOW.format(), Images.Flow.Loading);
    }
}
