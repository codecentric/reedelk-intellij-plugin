package com.esb.plugin.designer.properties;

import com.esb.component.Choice;
import com.esb.component.FlowReference;
import com.esb.component.Fork;
import com.esb.component.Stop;
import com.esb.plugin.component.choice.ChoicePropertiesRenderer;
import com.esb.plugin.component.flowreference.FlowReferencePropertyRenderer;
import com.esb.plugin.component.forkjoin.ForkJoinPropertyRenderer;
import com.esb.plugin.component.generic.GenericComponentPropertyRenderer;
import com.esb.plugin.component.stop.StopPropertyRenderer;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class PropertyRendererFactory {

    private static final Class<? extends PropertyRenderer> GENERIC_RENDERER = GenericComponentPropertyRenderer.class;

    private static final Map<String, Class<? extends PropertyRenderer>> RENDERER;

    static {
        Map<String, Class<? extends PropertyRenderer>> tmp = new HashMap<>();
        tmp.put(Stop.class.getName(), StopPropertyRenderer.class);
        tmp.put(Fork.class.getName(), ForkJoinPropertyRenderer.class);
        tmp.put(Choice.class.getName(), ChoicePropertiesRenderer.class);
        tmp.put(FlowReference.class.getName(), FlowReferencePropertyRenderer.class);
        RENDERER = Collections.unmodifiableMap(tmp);
    }

    private PropertyRendererFactory() {
    }


}
