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
    )
}