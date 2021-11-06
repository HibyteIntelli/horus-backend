package ro.hibyte.horus.utils;

import one.space.spo.app.service.contentitem.EntityItemPropertyFilter;

public class FilterUtils {

    public static EntityItemPropertyFilter filterForEquals(String typeKey, String value) {
        return EntityItemPropertyFilter.builder()
                .property(typeKey)
                .filterType(EntityItemPropertyFilter.Type.EQUALS)
                .value(value)
                .build();
    }

}
