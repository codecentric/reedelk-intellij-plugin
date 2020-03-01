package com.reedelk.plugin.editor.properties.renderer.typescript.scriptactions;

import com.intellij.openapi.module.Module;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.util.messages.MessageBusConnection;
import com.reedelk.plugin.commons.Colors;
import com.reedelk.plugin.editor.properties.commons.DisposablePanel;
import com.reedelk.plugin.service.module.CompletionService;
import com.reedelk.plugin.service.module.impl.completion.Suggestion;
import com.reedelk.plugin.topic.ReedelkTopics;
import com.reedelk.runtime.api.autocomplete.AutocompleteItemType;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import java.awt.*;
import java.util.List;

import static com.intellij.icons.AllIcons.Nodes.Function;
import static com.intellij.icons.AllIcons.Nodes.Variable;
import static com.intellij.util.ui.JBUI.Borders.*;
import static com.reedelk.plugin.message.ReedelkBundle.message;
import static java.awt.BorderLayout.CENTER;
import static java.awt.BorderLayout.NORTH;
import static javax.swing.BorderFactory.createMatteBorder;

class ScriptEditorContextPanel extends DisposablePanel implements CompletionService.OnCompletionEvent {

    private static final Border MATTE_BORDER = createMatteBorder(1, 1, 1, 0,
            Colors.SCRIPT_EDITOR_CONTEXT_PANEL_BORDER);
    private static final Border COMPOUND_BORDER = new CompoundBorder(
            customLine(Colors.SCRIPT_EDITOR_CONTEXT_PANEL_BORDER_BOTTOM, 0, 0, 1, 0),
            empty(5));

    private final transient Module module;
    private final transient MessageBusConnection connect;
    private final String componentFullyQualifiedName;
    private final DisposablePanel panelVariables;

    ScriptEditorContextPanel(Module module, String componentFullyQualifiedName) {
        this.module = module;
        this.componentFullyQualifiedName = componentFullyQualifiedName;
        setLayout(new BorderLayout());
        setBorder(MATTE_BORDER);

        this.connect = module.getMessageBus().connect();
        connect.subscribe(ReedelkTopics.COMPLETION_EVENT_TOPIC, this);

        JLabel panelTitle = new JLabel(message("script.editor.context.vars.title"));
        JPanel panelTitleWrapper = new JPanel();
        panelTitleWrapper.setBackground(Colors.SCRIPT_EDITOR_CONTEXT_PANEL_TITLE_BG);
        panelTitleWrapper.setBorder(COMPOUND_BORDER);
        panelTitleWrapper.setLayout(new BorderLayout());
        panelTitleWrapper.add(panelTitle, NORTH);
        add(panelTitleWrapper, NORTH);

        this.panelVariables = new DisposablePanel();
        BoxLayout boxLayout = new BoxLayout(panelVariables, BoxLayout.PAGE_AXIS);
        this.panelVariables.setLayout(boxLayout);
        this.panelVariables.setBorder(empty(5));

        CompletionService.getInstance(module)
                .contextVariablesOf(componentFullyQualifiedName)
                .forEach(suggestion -> panelVariables.add(new ContextVariableLabel(suggestion)));

        JBScrollPane panelVariablesScrollPane = new JBScrollPane(panelVariables);
        panelVariablesScrollPane.setBorder(empty());
        add(panelVariablesScrollPane, CENTER);
    }

    @Override
    public void dispose() {
        super.dispose();
        if (this.connect != null) {
            this.connect.disconnect();
        }
    }

    @Override
    public void onCompletionsUpdated() {
        List<Suggestion> suggestions =
                CompletionService.getInstance(module)
                .contextVariablesOf(componentFullyQualifiedName);

        SwingUtilities.invokeLater(() -> {
            panelVariables.removeAll();
            suggestions.forEach(suggestion ->
                    panelVariables.add(new ContextVariableLabel(suggestion)));
            panelVariables.repaint();
        });
    }

    static class ContextVariableLabel extends JLabel {
        ContextVariableLabel(Suggestion suggestion) {
            super(message("script.editor.context.vars.html.template", suggestion.getReplaceValue(), suggestion.getReturnType()));
            setIcon(suggestion.getItemType() == AutocompleteItemType.FUNCTION ? Function : Variable);
            setBorder(emptyTop(4));
        }
    }
}
