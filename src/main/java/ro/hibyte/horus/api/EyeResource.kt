package ro.hibyte.horus.api

import one.space.spi.ScopeResource
import one.space.spi.ScopedResource
import one.space.spo.app.domain.AppScope
import one.space.spo.app.service.ContentItemService
import one.space.spo.web.cdi.CurrentApplicationScope
import javax.enterprise.context.RequestScoped
import javax.inject.Inject

@RequestScoped
@ScopedResource("eye")
class EyeResource : ScopeResource {

    @Inject
    @CurrentApplicationScope
    private lateinit var scope: AppScope

    @Inject
    private lateinit var itemService: ContentItemService

}