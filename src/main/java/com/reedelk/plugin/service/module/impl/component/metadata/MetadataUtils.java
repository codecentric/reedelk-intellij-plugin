package com.reedelk.plugin.service.module.impl.component.metadata;

import com.reedelk.plugin.service.module.impl.component.completion.TypeAndTries;
import com.reedelk.plugin.service.module.impl.component.completion.TypeDefault;
import com.reedelk.plugin.service.module.impl.component.completion.TypeProxy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MetadataUtils {

    public static MetadataTypeDTO merge(List<MetadataTypeDTO> metadataTypes, TypeAndTries typeAndTries) {
        Map<String, MetadataTypeItemDTO> nameMetadataType = new HashMap<>();
        metadataTypes.forEach(metadataTypeDTO ->
                metadataTypeDTO.getProperties().forEach(metadataTypeItemDTO -> {
                    MetadataTypeItemDTO item = nameMetadataType.get(metadataTypeItemDTO.name);
                    if (item != null) {
                        MetadataTypeItemDTO merged = merge(metadataTypeItemDTO, item);
                        nameMetadataType.put(metadataTypeItemDTO.name, merged);
                    } else {
                        nameMetadataType.put(metadataTypeItemDTO.name, metadataTypeItemDTO);
                    }
                }));
        TypeProxy typeProxy = TypeProxy.create(TypeDefault.DEFAULT_ATTRIBUTES);
        String displayName = typeProxy.toSimpleName(typeAndTries);
        return new MetadataTypeDTO(displayName, typeProxy, nameMetadataType.values());
    }

    private static MetadataTypeItemDTO merge(MetadataTypeItemDTO dto1, MetadataTypeItemDTO dto2) {
        return Objects.equals(dto1.value, dto2.value) ?
                dto1 : new MetadataTypeItemDTO(dto1.name, dto1.value + ", " + dto2.value);
    }
}
