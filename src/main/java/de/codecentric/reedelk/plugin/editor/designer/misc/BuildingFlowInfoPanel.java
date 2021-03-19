package de.codecentric.reedelk.plugin.editor.designer.misc;

import de.codecentric.reedelk.plugin.commons.Images;

import static de.codecentric.reedelk.plugin.message.ReedelkBundle.message;

public class BuildingFlowInfoPanel extends BaseInfoPanel {

    public BuildingFlowInfoPanel() {
        super(message("flow.building"), Images.Flow.Loading);
    }
}
