package ro.hibyte.horus.api

import one.space.spi.ScopeResource
import one.space.spi.ScopedResource
import one.space.spo.app.domain.AppScope
import one.space.spo.app.service.ContentItemService
import one.space.spo.app.service.KpiService
import one.space.spo.web.cdi.CurrentApplicationScope
import org.joda.time.LocalDateTime
import ro.hibyte.horus.service.images.ImageFetchService
import javax.enterprise.context.RequestScoped
import javax.inject.Inject

import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.QueryParam
import javax.ws.rs.core.Response
import kotlin.random.Random

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

    @Inject
    private lateinit var kpiService: KpiService

    @GET
    @Path("startImages")
    fun startImages(): Response{
        imageFetchService.fetchDataForLocation(1)
        return Response.ok().build()
    }

    @GET
    @Path("putKpis/{kpiName}")
    fun startImages(@PathParam("kpiName") kpiName:String, @QueryParam("s") s:Int, @QueryParam("min") min:Int, @QueryParam("max") max:Int, @QueryParam("random") random:Boolean, @QueryParam("target") target:Int, @QueryParam("interval") interval:Int): Response{
        var result: Double = 0.0
        for(i in 0..s) {
            val values = mapOf(
                    "targetId" to target,
                    "timeStamp" to LocalDateTime.now().minusHours(i * interval)
            )

            if(random){
               result = Random.nextDouble(min.toDouble(), max.toDouble())
            } else {
                result = (max - (max-min)/(i+1)).toDouble()
            }
           kpiService.storeKPIValue(scope.scopeKey, kpiName, values, result)
        }
        return Response.ok().build()
    }


}