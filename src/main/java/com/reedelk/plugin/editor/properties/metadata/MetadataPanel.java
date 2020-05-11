package com.reedelk.plugin.editor.properties.metadata;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.module.Module;
import com.intellij.ui.AncestorListenerAdapter;
import com.intellij.ui.JBColor;
import com.intellij.util.messages.MessageBusConnection;
import com.reedelk.plugin.commons.DisposableUtils;
import com.reedelk.plugin.commons.Topics;
import com.reedelk.plugin.editor.properties.commons.DisposablePanel;
import com.reedelk.plugin.editor.properties.commons.PanelWithText;
import com.reedelk.plugin.editor.properties.context.ContainerContext;
import com.reedelk.plugin.service.module.PlatformModuleService;
import com.reedelk.plugin.service.module.impl.component.metadata.ComponentMetadata;
import com.reedelk.plugin.service.module.impl.component.metadata.OnComponentMetadata;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.AncestorEvent;
import java.awt.*;

public class MetadataPanel extends DisposablePanel implements OnComponentMetadata {

    private final MetadataActualInput metadataActualInput;
    private final MetadataExpectedInput metadataExpectedInput;

    private final DisposablePanel loadingPanel;
    private MessageBusConnection connection;

    public MetadataPanel(@NotNull Module module, @NotNull ContainerContext context) {
        super(new BorderLayout());

        loadingPanel = new PanelWithText.LoadingContentPanel();
        loadingPanel.setOpaque(true);
        loadingPanel.setBackground(JBColor.WHITE);

        MetadataPanelHeader metadataPanelHeader = new MetadataPanelHeader(
                "Input Message", this::showInputMessage,
                "Expected Payload", this::showExpectedInput);

        this.connection = module.getMessageBus().connect();
        this.connection.subscribe(Topics.ON_COMPONENT_IO, this);

        metadataActualInput = new MetadataActualInput();
        metadataExpectedInput = new MetadataExpectedInput();

        addAncestorListener(new AncestorListenerAdapter() {
            @Override
            public void ancestorAdded(AncestorEvent event) {
                PlatformModuleService.getInstance(module).componentMetadataOf(context);
            }
        });

        add(metadataPanelHeader, BorderLayout.NORTH);
        add(loadingPanel, BorderLayout.CENTER);
        setBackground(JBColor.WHITE);
    }

    @Override
    public void onComponentMetadata(ComponentMetadata componentMetadata) {
        ApplicationManager.getApplication().invokeLater(() -> {
            remove(loadingPanel);
            add(metadataActualInput, BorderLayout.CENTER);
            revalidate();
            repaint();
        });

        metadataActualInput.onComponentMetadata(componentMetadata);
        metadataExpectedInput.onComponentMetadata(componentMetadata);
    }

    @Override
    public void onComponentMetadataError(String message) {
        ApplicationManager.getApplication().invokeLater(() -> {
            remove(loadingPanel);
            add(metadataActualInput, BorderLayout.CENTER);
            revalidate();
            repaint();
        });

        metadataActualInput.onComponentMetadataError(message);
        metadataExpectedInput.onComponentMetadataError(message);
    }

    @Override
    public void dispose() {
        super.dispose();
        DisposableUtils.dispose(connection);
    }

    private void showExpectedInput() {
        SwingUtilities.invokeLater(() -> {
            remove(metadataActualInput);
            add(metadataExpectedInput, BorderLayout.CENTER);
            revalidate();
            repaint();
        });
    }

    private void showInputMessage() {
        ApplicationManager.getApplication().invokeLater(() -> {
            remove(metadataExpectedInput);
            add(metadataActualInput, BorderLayout.CENTER);
            revalidate();
            repaint();
        });
    }
}
