package ro.hibyte.horus.eye.translator

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import one.space.spo.app.service.KpiService
import ucar.ma2.Array
import ucar.nc2.NetcdfFiles
import java.time.format.DateTimeFormatter
import java.util.Calendar.*
import javax.enterprise.context.ApplicationScoped
import javax.enterprise.context.Dependent
import javax.inject.Inject

@Dependent
class NCToKpiTranslator {

    private val axisDimensions = setOf(
            "longitude",
            "latitude",
            "time"
    )

    private val formatter = DateTimeFormatter.ofPattern("YYYY-MM-ddTHH:mm")

    private val objectMapper: ObjectMapper = ObjectMapper()

    @Inject
    private lateinit var kpiService: KpiService

    fun translateFile(path: String, kpiName: String, targetId: Long) {
        val ncFile = NetcdfFiles.open(path)
        val dimensions = ncFile.rootGroup.dimensions
        dimensions.map { it.name }
                .filter { it !in axisDimensions }
                .forEach { storeValues(ncFile.findVariable(it)!!.read(), kpiName, targetId) }
    }

    private fun storeValues(values: Array, kpiName: String, targetId: Long) {
        val reducedValues = values.reduce()
        while (reducedValues.hasNext()) {
            storeKpi(
                objectMapper.readValue(reducedValues.next().toString(), object: TypeReference<Map<String, Any>>(){}),
                kpiName,
                targetId
            )
        }
    }

    private fun storeKpi(obj: Map<String, Any>, kpiName: String, targetId: Long) {
        kpiService.storeKPIValue(
                "point",
                kpiName,
                convertToKpi(obj, targetId),
                obj["result"]
        )
    }

    private fun convertToKpi(obj: Map<String, Any>, targetId: Long) =
            mapOf(
                "timestamp" to obtainDate(obj["time"] as String),
                "targetId" to targetId
            )

    private fun obtainDate(hours: String): String {
        val calendar = getInstance()
        calendar.set(YEAR, 1970)
        calendar.set(MONTH, 1)
        calendar.set(DAY_OF_YEAR, 1)
        calendar.add(HOUR, hours.toInt())
        return formatter.format(calendar.toInstant())
    }

}