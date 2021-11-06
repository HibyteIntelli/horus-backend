package ro.hibyte.horus.service

import com.github.kittinunf.fuel.Fuel.get
import ro.hibyte.horus.api.AuthResponse
import ro.hibyte.horus.utils.password
import ro.hibyte.horus.utils.username
import ro.hibyte.horus.utils.wekeoLink
import java.util.*

class AuthService {

    private val encodedUsernameAndPassword = Base64.getEncoder().encode("$username:$password".toByteArray())

    private val basicAuthHeader = "Authorization" to "Basic $encodedUsernameAndPassword"

    private val jsonHeader = "Accept" to "application/json"

    fun generateAccessToken() =
        get(wekeoLink + "gettoken")
                .header(
                        mapOf(
                                basicAuthHeader,
                                jsonHeader
                        )
                ).responseObject(AuthResponse.Deserializer())
                .third.get().access_token

}