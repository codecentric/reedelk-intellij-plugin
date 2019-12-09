package com.reedelk.plugin.service.project.impl.designerselection;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.project.Project;
import com.intellij.util.messages.MessageBusConnection;
import com.reedelk.plugin.service.project.DesignerSelectionService;
import com.reedelk.plugin.topic.ReedelkTopics;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class DesignerSelectionServiceImpl implements DesignerSelectionService, DesignerSelectionService.CurrentSelectionListener, Disposable {

    private final MessageBusConnection connection;

    private SelectableItem currentSelection;

    public DesignerSelectionServiceImpl(@NotNull Project project) {
        this.connection = project.getMessageBus().connect();
        this.connection.subscribe(ReedelkTopics.CURRENT_COMPONENT_SELECTION_EVENTS, this);
    }

    @Override
    public Optional<SelectableItem> getCurrentSelection() {
        return Optional.ofNullable(currentSelection);
    }

    @Override
    public void dispose() {
        this.connection.disconnect();
    }

    @Override
    public void onSelection(SelectableItem selectedItem) {
        this.currentSelection = selectedItem;
    }

    @Override
    public void onUnSelected(SelectableItem selectableItem) {
        if (currentSelection == selectableItem) {
            this.currentSelection = null;
        }
    }
}
