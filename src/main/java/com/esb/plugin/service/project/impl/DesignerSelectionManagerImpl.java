package com.esb.plugin.service.project.impl;

import com.esb.plugin.service.project.DesignerSelectionManager;
import com.esb.plugin.service.project.SelectableItem;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.project.Project;
import com.intellij.util.messages.MessageBusConnection;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class DesignerSelectionManagerImpl implements DesignerSelectionManager, DesignerSelectionManager.CurrentSelectionListener, Disposable {

    private final MessageBusConnection connection;

    private SelectableItem currentSelection;

    public DesignerSelectionManagerImpl(@NotNull Project project) {
        this.connection = project.getMessageBus().connect();
        this.connection.subscribe(CurrentSelectionListener.CURRENT_SELECTION_TOPIC, this);
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
