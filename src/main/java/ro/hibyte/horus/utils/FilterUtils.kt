package ro.hibyte.horus.utils

import one.space.spo.app.service.contentitem.EntityItemPropertyFilter

fun filterForEquals(typeKey: String?, value: String?): EntityItemPropertyFilter =
        EntityItemPropertyFilter.builder()
                                .property(typeKey)
                                .filterType(EntityItemPropertyFilter.Type.EQUALS)
                                .value(value)
                                .build()
