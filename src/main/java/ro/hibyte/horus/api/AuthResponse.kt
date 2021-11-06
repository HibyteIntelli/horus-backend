package ro.hibyte.horus.api

import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.google.gson.Gson

data class AuthResponse(
        val access_token: String
){

    class Deserializer : ResponseDeserializable<AuthResponse> {
        override fun deserialize(content: String): AuthResponse =
                Gson().fromJson(content, AuthResponse::class.java)
    }

}