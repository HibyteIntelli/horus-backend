package ro.hibyte.horus.api

import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.google.gson.Gson

class JobModelEntity {
    data class JobModel(
            val datasetId: String,
            val dateRangeSelectValues: List<DateRangeSelectValues>,
            val stringChoiceValues: List<StringChoiceValues>,
            val boundingBoxValues: List<BoundingBoxValues>
    )

    data class DateRangeSelectValues(
            val name: String = "dtrange",
            val start: String,
            val end: String
    )

    data class StringChoiceValues(
            val name: String,
            val value: String
    )

    data class BoundingBoxValues(
            val name: String = "bbox",
            val bbox: List<Double>
    )

    data class JobCreationResponse(
            val jobId: String,
            val status: String,
            val results: List<String>,
            val message: String
    ) {
        class Deserializer : ResponseDeserializable<JobCreationResponse> {
            override fun deserialize(content: String): JobCreationResponse =
                    Gson().fromJson(content, JobCreationResponse::class.java)
        }
    }

    data class JobStatusResponse(
            val status: String,
            val message: String
    ) {
        class Deserializer : ResponseDeserializable<JobStatusResponse> {
            override fun deserialize(content: String): JobStatusResponse =
                    Gson().fromJson(content, JobStatusResponse::class.java)
        }
    }

    data class JobResultRespons(
            val content: List<JobResultResponsContent>

    ){
        class Deserializer : ResponseDeserializable<JobResultRespons> {
            override fun deserialize(content: String): JobResultRespons =
                    Gson().fromJson(content, JobResultRespons::class.java)
        }
    }

    data class JobResultResponsContent(
            val downloadUri: String,
            val filename: String,
            val order: OrderCreateResponse,
            val productInfo: ProductInfo,
            val size: Long,
            val url: String
    )

    data class ProductInfo(
            val datasetId: String,
            val product: String,
            val productEndDate: String,
            val productStartDate: String
    )

    data class OrderCreateResponse(
            val orderId: String,
            val status: String,
            val message: String
    ) {
        class Deserializer : ResponseDeserializable<OrderCreateResponse> {
            override fun deserialize(content: String): OrderCreateResponse =
                    Gson().fromJson(content, OrderCreateResponse::class.java)
        }
    }
}