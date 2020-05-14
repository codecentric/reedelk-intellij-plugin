package com.reedelk.plugin.editor.properties.renderer.typescript.scriptactions;

import com.intellij.openapi.module.Module;
import com.intellij.ui.components.JBLabel;
import com.reedelk.plugin.commons.Colors;
import com.reedelk.plugin.commons.HTMLUtils;
import com.reedelk.plugin.editor.properties.commons.DisposablePanel;
import com.reedelk.plugin.editor.properties.commons.DisposableScrollPane;
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

class ScriptEditorContextPanel extends DisposablePanel {

    private final transient Module module;
    private final transient DisposablePanel panelVariables;
    private final transient ComponentContext componentContext;
    private final String scriptPropertyPath;

    ScriptEditorContextPanel(@NotNull Module module, @NotNull String scriptPropertyPath, @NotNull ContainerContext context) {
        this.module = module;
        this.scriptPropertyPath = scriptPropertyPath;
        this.componentContext = context.componentContext();

        this.panelVariables = new DisposablePanel();
        BoxLayout boxLayout = new BoxLayout(panelVariables, BoxLayout.PAGE_AXIS);
        this.panelVariables.setLayout(boxLayout);
        this.panelVariables.setBorder(empty(5));
        suggestions().forEach(suggestion -> panelVariables.add(new ContextVariableLabel(suggestion)));
        DisposableScrollPane panelVariablesScrollPane = new DisposableScrollPane(panelVariables);
        panelVariablesScrollPane.setBorder(empty());

        JBLabel panelTitle = new JBLabel(message("script.editor.context.vars.title"));
        DisposablePanel panelTitleWrapper = new DisposablePanel();
        panelTitleWrapper.setBackground(Colors.SCRIPT_EDITOR_CONTEXT_PANEL_TITLE_BG);
        panelTitleWrapper.setBorder(COMPOUND_BORDER);
        panelTitleWrapper.setLayout(new BorderLayout());
        panelTitleWrapper.add(panelTitle, NORTH);

        setLayout(new BorderLayout());
        setBorder(MATTE_BORDER);
        add(panelTitleWrapper, NORTH);
        add(panelVariablesScrollPane, CENTER);
    }

    static class ContextVariableLabel extends JLabel {
        ContextVariableLabel(Suggestion suggestion) {
            super(message("script.editor.context.vars.html.template",
                    suggestion.getLookupToken(),
                    HTMLUtils.escape(suggestion.getReturnTypeDisplayValue())));
            setIcon(suggestion.getType().icon);
            setBorder(emptyTop(4));
        }
    }

    private List<Suggestion> suggestions() {
        return PlatformModuleService.getInstance(module)
                .variablesOf(componentContext, scriptPropertyPath)
                .stream()
                .filter(suggestion -> StringUtils.isNotBlank(suggestion.getLookupToken()))
                .sorted(Comparator.comparing(Suggestion::getLookupToken).reversed())
                .collect(Collectors.toList());
    }

    private static final Border MATTE_BORDER = createMatteBorder(1, 1, 1, 0,
            Colors.SCRIPT_EDITOR_CONTEXT_PANEL_BORDER);
    private static final Border COMPOUND_BORDER = new CompoundBorder(
            customLine(Colors.SCRIPT_EDITOR_CONTEXT_PANEL_BORDER_BOTTOM, 0, 0, 1, 0),
            empty(5));
}
