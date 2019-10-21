package com.reedelk.plugin.component.type.router;

import com.intellij.openapi.module.Module;
import com.reedelk.plugin.commons.Labels;
import com.reedelk.plugin.component.domain.ComponentData;
import com.reedelk.plugin.component.domain.ComponentPropertyDescriptor;
import com.reedelk.plugin.component.type.generic.GenericComponentPropertiesRenderer;
import com.reedelk.plugin.component.type.router.widget.ConditionRouteTableModel;
import com.reedelk.plugin.component.type.router.widget.RouterRouteTable;
import com.reedelk.plugin.editor.properties.widget.ContainerContext;
import com.reedelk.plugin.editor.properties.widget.ContainerFactory;
import com.reedelk.plugin.editor.properties.widget.DisposablePanel;
import com.reedelk.plugin.editor.properties.widget.JComponentHolder;
import com.reedelk.plugin.editor.properties.widget.input.InputChangeListener;
import com.reedelk.plugin.editor.properties.widget.input.script.ScriptContextManager;
import com.reedelk.plugin.graph.FlowSnapshot;
import com.reedelk.plugin.graph.node.GraphNode;

import javax.swing.*;
import java.awt.*;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

import static com.reedelk.plugin.component.type.router.RouterNode.DATA_CONDITION_ROUTE_PAIRS;
import static java.awt.BorderLayout.CENTER;
import static java.awt.BorderLayout.NORTH;

public class RouterPropertiesRenderer extends GenericComponentPropertiesRenderer {

    public RouterPropertiesRenderer(FlowSnapshot snapshot, Module module) {
        super(snapshot, module);
    }

    @Override
    public DisposablePanel render(GraphNode routerNode) {
        DisposablePanel genericProperties = super.render(routerNode);

        ScriptContextManager scriptContextManager = new ScriptContextManager(module, new EmptyContainerContext(), Collections.emptyList());


        ComponentData componentData = routerNode.componentData();
        List<RouterConditionRoutePair> conditionRoutePairList = componentData.get(DATA_CONDITION_ROUTE_PAIRS);

        ConditionRouteTableModel model = new ConditionRouteTableModel(conditionRoutePairList, snapshot);
        RouterRouteTable routerRouteTable = new RouterRouteTable(module, model, scriptContextManager);

        DisposablePanel routerTableContainer = ContainerFactory.createObjectTypeContainer(routerRouteTable, Labels.ROUTER_TABLE_CONTAINER_TITLE);

        DisposablePanel container = new DisposablePanel();
        container.setLayout(new BorderLayout());
        container.add(genericProperties, NORTH);
        container.add(routerTableContainer, CENTER);
        return container;
    }

    class EmptyContainerContext implements ContainerContext {

        @Override
        public <T> T propertyValueFrom(String propertyName) {
            return null;
        }

        @Override
        public void subscribePropertyChange(String propertyName, InputChangeListener inputChangeListener) {

        }

        @Override
        public Optional<ComponentPropertyDescriptor> getPropertyDescriptor(Predicate<ComponentPropertyDescriptor> filter) {
            return Optional.empty();
        }

        @Override
        public void addComponent(JComponentHolder componentHolder) {

        }

        @Override
        public Optional<JComponent> getComponentMatchingMetadata(BiPredicate<String, String> keyValuePredicate) {
            return Optional.empty();
        }

        @Override
        public <T> void notifyPropertyChanged(String propertyName, T object) {

        }
    }
}
