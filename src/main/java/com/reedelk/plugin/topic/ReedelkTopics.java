package com.reedelk.plugin.topic;

import com.intellij.util.messages.Topic;
import com.reedelk.plugin.editor.properties.CommitPropertiesListener;
import com.reedelk.plugin.service.module.CompletionService;
import com.reedelk.plugin.service.module.impl.component.scanner.ComponentListUpdateNotifier;
import com.reedelk.plugin.service.project.DesignerSelectionService;

public class ReedelkTopics {

    public static final Topic<ComponentListUpdateNotifier> COMPONENTS_UPDATE_EVENTS =
            Topic.create("component update events", ComponentListUpdateNotifier.class);

    public static final Topic<CommitPropertiesListener> COMMIT_COMPONENT_PROPERTIES_EVENTS =
            Topic.create("commit component properties events", CommitPropertiesListener.class);

    public static final Topic<DesignerSelectionService.CurrentSelectionListener> CURRENT_COMPONENT_SELECTION_EVENTS =
            Topic.create("current component selected events", DesignerSelectionService.CurrentSelectionListener.class);

    public static final Topic<CompletionService.OnCompletionEvent> COMPLETION_EVENT_TOPIC =
            Topic.create("completion updated events", CompletionService.OnCompletionEvent.class);
}
