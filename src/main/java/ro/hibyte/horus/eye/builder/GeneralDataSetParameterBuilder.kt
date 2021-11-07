package ro.hibyte.horus.eye.builder

import com.github.kittinunf.fuel.core.Parameters
import one.space.spo.app.domain.ContentItem
import one.space.spo.app.service.SpoqlService
import kotlin.collections.ArrayList

object GeneralDataSetParameterBuilder {

    private const val datasetId = "EO:ECMWF:DAT:REANALYSIS_ERA5_SINGLE_LEVELS_MONTHLY_MEANS"

    private val timePerDay = DateComputer.computeTimePerDay()

    fun build(target: ContentItem, spoqlService: SpoqlService): Parameters {
        val params = ArrayList<Pair<String, Any>>()
        addBasicParameters(params)
        addBoundingBoxValues(params, target)
        addMultiStringValues(params, target, spoqlService)
        return params
    }

    private fun addMultiStringValues(params: ArrayList<Pair<String, Any>>, target: ContentItem, spoqlService: SpoqlService) {
        val startDate = DateComputer.computeStartDate(target, spoqlService)
        params.add(
                "multiStringSelectValues" to
                listOf(
                        "variable" with setOf("2m_temperature", "snowmelt", "10m_v_component_of_wind"),
                        "product_type" with listOf("monthly_averaged_reanalysis_by_hour_of_day"),
                        "year" with DateComputer.computeYearsUntilNow(startDate).toList(),
                        "month" with DateComputer.computeMonthsUntilNow(startDate).toList(),
                        "day" with DateComputer.computeDaysUntilNow(startDate).toList(),
                        "time" with timePerDay
                )
        )
    }

    private fun addBoundingBoxValues(params: ArrayList<Pair<String, Any>>, target: ContentItem) {
        val location = target.getSingleProperty<ContentItem>("location")
        val point = location.getListProperty<ContentItem>("points")[0]
        val latitude = point.getSingleProperty<String>("latitude")
        val longitude = point.getSingleProperty<String>("longitude")
        params.add(
                "boundingBoxValues" to
                listOf(
                        mapOf(
                                "name" to "area",
                                "bbox" to listOf(
                                        latitude,
                                        longitude,
                                        latitude.toDouble() + .1,
                                        longitude.toDouble() + .1
                                )
                        )
                )
        )
    }

    private fun addBasicParameters(params: ArrayList<Pair<String, Any>>) {
        params.add(
                "datasetId" to datasetId
        )
        params.add(
                "stringChoiceValues" to
                listOf(
                        mapOf(
                                "name" to "format",
                                "value" to "netcdf"
                        )
                )
        )
    }

    private infix fun String.with(any: Any): Map<String, Any> =
            mapOf(
                "name" to this,
                "value" to any
            )

}