package ro.hibyte.horus.resource

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import net.wissenswerft.commons.jersey.security.Authenticated
import one.space.spi.ScopeResource
import one.space.spi.ScopedResource
import ro.hibyte.horus.dataModel.LocationPoint
import ro.hibyte.horus.gps.InsidePolygonService
import javax.enterprise.context.RequestScoped
import javax.ws.rs.GET
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.Response;

@RequestScoped
@ScopedResource("horus")
@Tag(name = "Horus")
class HorusResource: ScopeResource {

    @POST
    @Path("sendListPoints")
    @Produces("application/json")
    @Operation(
        summary = "Calculate points",
        tags = ["Hibyte"],
        security = [SecurityRequirement(name = "basic"), SecurityRequirement(name = "apikey")],
        responses = [ApiResponse(responseCode = "200", description = "Data processed"), ApiResponse(
            responseCode = "403",
            description = "Please authorize!"
        )]
    )
    @Authenticated
    fun solvePoint(list: List<LocationPoint>): Response? {
        return Response.ok(InsidePolygonService.generate2Points(list)).build()
    }
    @GET
    @Path("getListPoints")
    @Produces("application/json")
    @Operation(
        summary = "Calculate points",
        tags = ["Hibyte"],
        security = [SecurityRequirement(name = "basic"), SecurityRequirement(name = "apikey")],
        responses = [ApiResponse(responseCode = "200", description = "Data processed"), ApiResponse(
            responseCode = "403",
            description = "Please authorize!"
        )]
    )
    @Authenticated
    fun getPoints() :Response? {
        return Response.ok(InsidePolygonService.getPoints()).build()
    }

}