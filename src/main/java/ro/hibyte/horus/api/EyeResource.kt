package ro.hibyte.horus.api

import one.space.spi.ScopeResource
import one.space.spi.ScopedResource
import one.space.spo.app.domain.AppScope
import one.space.spo.app.service.ContentItemService
import one.space.spo.web.cdi.CurrentApplicationScope
import ro.hibyte.horus.service.images.ImageFetchService
import javax.enterprise.context.RequestScoped
import javax.inject.Inject
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.core.Response

@RequestScoped
@ScopedResource("eye")
class EyeResource : ScopeResource {

    @Inject
    @CurrentApplicationScope
    private lateinit var scope: AppScope

    @Inject
    private lateinit var itemService: ContentItemService

    @Inject
    private lateinit var imageFetchService: ImageFetchService

    @GET
    @Path("startImages")
    fun test(): Response{
        imageFetchService.fetchDataForLocation(1)
        return Response.ok().build()
    }


}