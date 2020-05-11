package com.reedelk.plugin.editor.properties.componentmetadata;

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

public class ComponentMetadataPanel extends DisposablePanel implements OnComponentMetadata {

    private MetadataInput metadataInput;
    private MetadataExpectedInput metadataExpectedInput;

    private final DisposablePanel loadingPanel;
    private MessageBusConnection connection;

    public ComponentMetadataPanel(@NotNull Module module,
                                  @NotNull ContainerContext context,
                                  @NotNull String componentFullyQualifiedName) {
        super(new BorderLayout());

        loadingPanel = new PanelWithText.LoadingContentPanel();
        loadingPanel.setOpaque(true);
        loadingPanel.setBackground(JBColor.WHITE);

        ComponentInputHeader componentInputHeader = new ComponentInputHeader(
                "Input Message", this::showInputMessage,
                "Expected Input", this::showExpectedInput);

        this.connection = module.getMessageBus().connect();
        this.connection.subscribe(Topics.ON_COMPONENT_IO, this);

        metadataInput = new MetadataInput();
        metadataExpectedInput = new MetadataExpectedInput();

        addAncestorListener(new AncestorListenerAdapter() {
            @Override
            public void ancestorAdded(AncestorEvent event) {
                PlatformModuleService.getInstance(module)
                        .componentMetadataOf(context, componentFullyQualifiedName);
            }
        });

        add(componentInputHeader, BorderLayout.NORTH);
        add(loadingPanel, BorderLayout.CENTER);
        setBackground(JBColor.WHITE);
    }

    @Override
    public void onComponentMetadata(ComponentMetadata componentMetadata) {
        ApplicationManager.getApplication().invokeLater(() -> {
            remove(loadingPanel);
            add(metadataInput, BorderLayout.CENTER);
            revalidate();
            repaint();
        });

        metadataInput.onComponentMetadata(componentMetadata);
        metadataExpectedInput.onComponentMetadata(componentMetadata);
    }

    @Override
    public void onComponentMetadataError(String message) {
        ApplicationManager.getApplication().invokeLater(() -> {
            remove(loadingPanel);
            add(metadataInput, BorderLayout.CENTER);
            revalidate();
            repaint();
        });

        metadataInput.onComponentMetadataError(message);
        metadataExpectedInput.onComponentMetadataError(message);
    }

    @Override
    public void dispose() {
        super.dispose();
        DisposableUtils.dispose(connection);
    }

    private void showExpectedInput() {
        SwingUtilities.invokeLater(() -> {
            remove(metadataInput);
            add(metadataExpectedInput, BorderLayout.CENTER);
            revalidate();
            repaint();
        });
    }

    private void showInputMessage() {
        ApplicationManager.getApplication().invokeLater(() -> {
            remove(metadataExpectedInput);
            add(metadataInput, BorderLayout.CENTER);
            revalidate();
            repaint();
        });
    }
}
