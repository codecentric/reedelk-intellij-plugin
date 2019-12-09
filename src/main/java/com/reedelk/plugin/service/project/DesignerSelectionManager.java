package com.reedelk.plugin.service.project;

import com.intellij.openapi.project.Project;
import com.reedelk.plugin.service.project.impl.SelectableItem;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public interface DesignerSelectionManager {

    static DesignerSelectionManager getInstance(@NotNull Project project) {
        return project.getComponent(DesignerSelectionManager.class);
    }

    Optional<SelectableItem> getCurrentSelection();


    interface CurrentSelectionListener {

        void onSelection(SelectableItem selectedItem);

        void onUnSelected(SelectableItem selectableItem);

        default void refresh() {
        }
    }

}
