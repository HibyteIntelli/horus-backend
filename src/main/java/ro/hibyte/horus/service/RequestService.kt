package ro.hibyte.horus.service

import com.github.kittinunf.fuel.core.FuelManager
import com.github.kittinunf.fuel.core.Parameters
import com.github.kittinunf.fuel.core.Request
import ro.hibyte.horus.utils.wekeoLink
import javax.annotation.PostConstruct
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject
import com.github.kittinunf.fuel.Fuel.get as fuelGet
import com.github.kittinunf.fuel.Fuel.post as fuelPost
import com.github.kittinunf.fuel.Fuel.put as fuelPut

@ApplicationScoped
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

    fun get(path: String) = fuelGet("$wekeoLink$path")

    fun put(path: String, params: Parameters) = fuelPut("$wekeoLink$path", params)

    fun post(path: String, params: Parameters) = fuelPost("$wekeoLink$path", params)

}