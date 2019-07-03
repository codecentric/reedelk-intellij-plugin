package com.esb.plugin.component.type.router.functions;

import com.esb.plugin.component.domain.ComponentData;
import com.esb.plugin.component.type.router.RouterConditionRoutePair;
import com.esb.plugin.component.type.router.RouterNode;

import java.util.LinkedList;
import java.util.List;

public class ListConditionRoutePairs {

    private ListConditionRoutePairs() {
    }

    public static List<RouterConditionRoutePair> of(ComponentData componentData) {
        List<RouterConditionRoutePair> conditionRoutePair =
                componentData.get(RouterNode.DATA_CONDITION_ROUTE_PAIRS);
        if (conditionRoutePair == null) {
            conditionRoutePair = new LinkedList<>();
            componentData.set(RouterNode.DATA_CONDITION_ROUTE_PAIRS, conditionRoutePair);
        }
        return conditionRoutePair;
    }
}
