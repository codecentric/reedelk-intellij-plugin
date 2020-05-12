package com.reedelk.plugin.editor.properties.renderer.typescript.scriptactions;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.module.Module;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.util.messages.MessageBusConnection;
import com.reedelk.plugin.commons.Colors;
import com.reedelk.plugin.commons.DisposableUtils;
import com.reedelk.plugin.commons.Topics;
import com.reedelk.plugin.editor.properties.commons.DisposablePanel;
import com.reedelk.plugin.editor.properties.context.ContainerContext;
import com.reedelk.plugin.service.module.PlatformModuleService;
import com.reedelk.plugin.service.module.impl.component.ComponentContext;
import com.reedelk.plugin.service.module.impl.component.completion.Suggestion;
import com.reedelk.runtime.api.commons.StringUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import java.awt.*;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static com.intellij.util.ui.JBUI.Borders.*;
import static com.reedelk.plugin.message.ReedelkBundle.message;
import static java.awt.BorderLayout.CENTER;
import static java.awt.BorderLayout.NORTH;
import static javax.swing.BorderFactory.createMatteBorder;

class ScriptEditorContextPanel extends DisposablePanel implements PlatformModuleService.OnCompletionEvent {

    private final transient Module module;
    private final transient MessageBusConnection connect;
    private final String componentPropertyPath;
    private final DisposablePanel panelVariables;
    private final ComponentContext componentContext;

    ScriptEditorContextPanel(@NotNull Module module, @NotNull ContainerContext context) {
        this.module = module;
        this.componentContext = context.componentContext();
        this.componentPropertyPath = context.componentPropertyPath();

        this.connect = module.getMessageBus().connect();
        this.connect.subscribe(Topics.COMPLETION_EVENT_TOPIC, this);

        this.panelVariables = new DisposablePanel();
        BoxLayout boxLayout = new BoxLayout(panelVariables, BoxLayout.PAGE_AXIS);
        this.panelVariables.setLayout(boxLayout);
        this.panelVariables.setBorder(empty(5));
        suggestions().forEach(suggestion -> panelVariables.add(new ContextVariableLabel(suggestion)));

        JLabel panelTitle = new JLabel(message("script.editor.context.vars.title"));
        JPanel panelTitleWrapper = new JPanel();
        panelTitleWrapper.setBackground(Colors.SCRIPT_EDITOR_CONTEXT_PANEL_TITLE_BG);
        panelTitleWrapper.setBorder(COMPOUND_BORDER);
        panelTitleWrapper.setLayout(new BorderLayout());
        panelTitleWrapper.add(panelTitle, NORTH);

        JBScrollPane panelVariablesScrollPane = new JBScrollPane(panelVariables);
        panelVariablesScrollPane.setBorder(empty());

        add(panelTitleWrapper, NORTH);
        add(panelVariablesScrollPane, CENTER);
        setLayout(new BorderLayout());
        setBorder(MATTE_BORDER);
    }

    @Override
    public void dispose() {
        super.dispose();
        DisposableUtils.dispose(connect);
    }

    @Override
    public void onCompletionsUpdated() {
        List<Suggestion> suggestions = suggestions();
        ApplicationManager.getApplication().invokeLater(() -> {
            panelVariables.removeAll();
            suggestions.forEach(suggestion -> panelVariables.add(new ContextVariableLabel(suggestion)));
            panelVariables.repaint();
        });
    }

    static class ContextVariableLabel extends JLabel {
        ContextVariableLabel(Suggestion suggestion) {
            super(message("script.editor.context.vars.html.template", suggestion.lookupString(), suggestion.presentableType()));
            setIcon(suggestion.icon());
            setBorder(emptyTop(4));
        }
    }

    private List<Suggestion> suggestions() {
        return PlatformModuleService.getInstance(module)
                .variablesOf(componentContext, componentPropertyPath)
                .stream()
                .filter(suggestion -> StringUtils.isNotBlank(suggestion.lookupString()))
                .sorted(Comparator.comparing(Suggestion::lookupString).reversed())
                .collect(Collectors.toList());
    }

    private static final Border MATTE_BORDER = createMatteBorder(1, 1, 1, 0,
            Colors.SCRIPT_EDITOR_CONTEXT_PANEL_BORDER);
    private static final Border COMPOUND_BORDER = new CompoundBorder(
            customLine(Colors.SCRIPT_EDITOR_CONTEXT_PANEL_BORDER_BOTTOM, 0, 0, 1, 0),
            empty(5));
}
