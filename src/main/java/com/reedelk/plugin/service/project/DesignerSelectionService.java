package com.reedelk.plugin.service.project;

import com.intellij.openapi.project.Project;
import com.reedelk.plugin.service.project.impl.designerselection.SelectableItem;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public interface DesignerSelectionService {

    static DesignerSelectionService getInstance(@NotNull Project project) {
        return project.getComponent(DesignerSelectionService.class);
    }

    Optional<SelectableItem> getCurrentSelection();


    interface CurrentSelectionListener {

        void onSelection(SelectableItem selectedItem);

        void onUnSelected(SelectableItem selectableItem);

        default void refresh() {
        }
    }

}
