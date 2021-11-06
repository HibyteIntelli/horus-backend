package ro.hibyte.horus.api;

import one.space.spi.ScopeResource;
import one.space.spi.ScopedResource;
import one.space.spo.app.domain.AppScope;
import one.space.spo.app.service.ContentItemService;
import one.space.spo.web.cdi.CurrentApplicationScope;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

@RequestScoped
@ScopedResource("eye")
public class EyeResource implements ScopeResource {

    @Inject
    @CurrentApplicationScope
    private AppScope scope;

    @Context
    private UriInfo uriInfo;

    @Inject
    private ContentItemService itemService;

}
