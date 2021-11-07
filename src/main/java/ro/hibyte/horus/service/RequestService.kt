package ro.hibyte.horus.service

import com.github.kittinunf.fuel.core.FuelManager
import com.github.kittinunf.fuel.core.Parameters
import com.github.kittinunf.fuel.core.Request
import ro.hibyte.horus.utils.wekeoLink
import javax.annotation.PostConstruct
import javax.enterprise.context.ApplicationScoped
import javax.enterprise.context.Dependent
import javax.inject.Inject
import com.github.kittinunf.fuel.Fuel.get
import com.github.kittinunf.fuel.Fuel.post
import com.github.kittinunf.fuel.Fuel.put

@Dependent
class RequestService {

    @Inject
    private lateinit var authService: AuthService

    @PostConstruct
    fun onInit() {
        FuelManager.instance.addRequestInterceptor(tokenInterceptor())
    }

    private fun tokenInterceptor() = {
        next: (Request) -> Request ->
            {
                req: Request -> attachHeader(req)
                next(req)
            }
    }

    private fun attachHeader(req: Request) {
        if (!req.url.path.contains("gettoken")) {
            req.header(mapOf("Authorization" to authService.generateAccessToken()))
        }
    }

    fun gett(path: String) = get("$wekeoLink$path")

    fun putt(path: String, params: Parameters) = put("$wekeoLink$path", params)

    fun postt(path: String, body: String) = post("$wekeoLink$path").body(body).header("content-type", "application/json")

}