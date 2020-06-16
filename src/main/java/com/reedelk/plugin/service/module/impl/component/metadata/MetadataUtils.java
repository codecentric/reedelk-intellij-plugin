package com.reedelk.plugin.service.module.impl.component.metadata;

import com.reedelk.plugin.service.module.impl.component.completion.TypeAndTries;
import com.reedelk.plugin.service.module.impl.component.completion.TypeBuiltIn;
import com.reedelk.plugin.service.module.impl.component.completion.TypeProxy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MetadataUtils {

    public static MetadataTypeDTO mergeAttributesMetadata(List<MetadataTypeDTO> metadataTypes, TypeAndTries typeAndTries) {
        if (metadataTypes.size() == 1) {
            // No need to merge.
            return metadataTypes.iterator().next();
        }
        Map<String, MetadataTypeItemDTO> nameMetadataType = new HashMap<>();
        metadataTypes.forEach(metadataTypeDTO ->
                metadataTypeDTO.getProperties().forEach(metadataTypeItemDTO -> {
                    MetadataTypeItemDTO item = nameMetadataType.get(metadataTypeItemDTO.name);
                    if (item != null) {
                        MetadataTypeItemDTO merged = mergeAttributesMetadata(metadataTypeItemDTO, item);
                        nameMetadataType.put(metadataTypeItemDTO.name, merged);
                    } else {
                        nameMetadataType.put(metadataTypeItemDTO.name, metadataTypeItemDTO);
                    }
                }));
        TypeProxy typeProxy = TypeProxy.create(TypeBuiltIn.DEFAULT_ATTRIBUTES);
        String displayName = typeProxy.toSimpleName(typeAndTries);
        return new MetadataTypeDTO(displayName, typeProxy, nameMetadataType.values());
    }

    private static MetadataTypeItemDTO mergeAttributesMetadata(MetadataTypeItemDTO dto1, MetadataTypeItemDTO dto2) {
        return Objects.equals(dto1.displayType, dto2.displayType) ?
                dto1 : new MetadataTypeItemDTO(dto1.name, dto1.displayType + "," + dto2.displayType);
    }
}
