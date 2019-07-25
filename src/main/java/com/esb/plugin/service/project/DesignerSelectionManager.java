package com.esb.plugin.service.project;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.util.messages.Topic;

import java.util.Optional;

public interface DesignerSelectionManager {

    static DesignerSelectionManager getInstance() {
        return ServiceManager.getService(DesignerSelectionManager.class);
    }

    Optional<SelectableItem> getCurrentSelection();


    interface CurrentSelectionListener {

        Topic<CurrentSelectionListener> CURRENT_SELECTION_TOPIC = Topic.create("Designer Panel Selection", CurrentSelectionListener.class);

        void onSelection(SelectableItem selectedItem);

        void onUnSelected(SelectableItem selectableItem);
    }

}
