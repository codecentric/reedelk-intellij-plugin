package com.reedelk.plugin.editor.properties.componentinput;

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
import com.reedelk.plugin.service.module.impl.component.componentio.IOComponent;
import com.reedelk.plugin.service.module.impl.component.componentio.OnComponentIO;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.AncestorEvent;
import java.awt.*;

public class ComponentInput extends DisposablePanel implements OnComponentIO {

    private ComponentActualInput actualInput;
    private ComponentExpectedInput expectedInput;

    private final DisposablePanel loadingPanel;
    private MessageBusConnection connection;

    public ComponentInput(@NotNull Module module,
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

        actualInput = new ComponentActualInput();
        expectedInput = new ComponentExpectedInput();

        addAncestorListener(new AncestorListenerAdapter() {
            @Override
            public void ancestorAdded(AncestorEvent event) {
                PlatformModuleService.getInstance(module)
                        .inputOutputOf(context, componentFullyQualifiedName);
            }
        });

        add(componentInputHeader, BorderLayout.NORTH);
        add(loadingPanel, BorderLayout.CENTER);
        setBackground(JBColor.WHITE);
    }

    @Override
    public void onComponentIO(String inputFQCN, String outputFQCN, IOComponent IOComponent) {
        actualInput.onComponentIO(inputFQCN, outputFQCN, IOComponent);
        expectedInput.onComponentIO(inputFQCN, outputFQCN, IOComponent);

        ApplicationManager.getApplication().invokeLater(() -> {
            remove(loadingPanel);
            add(actualInput, BorderLayout.CENTER);
            revalidate();
            repaint();
        });
    }

    @Override
    public void dispose() {
        super.dispose();
        DisposableUtils.dispose(connection);
    }

    private void showExpectedInput() {
        SwingUtilities.invokeLater(() -> {
            remove(actualInput);
            add(expectedInput, BorderLayout.CENTER);
            revalidate();
            repaint();
        });
    }

    private void showInputMessage() {
        ApplicationManager.getApplication().invokeLater(() -> {
            remove(expectedInput);
            add(actualInput, BorderLayout.CENTER);
            revalidate();
            repaint();
        });
    }
}
