package ro.hibyte.horus.eye

import com.fasterxml.jackson.databind.ObjectMapper
import one.space.spo.app.domain.ContentItem
import one.space.spo.app.service.SpoqlService
import ro.hibyte.horus.eye.builder.GeneralDataSetParameterBuilder
import ro.hibyte.horus.eye.translator.NCToKpiTranslator
import ro.hibyte.horus.service.DataFetchService
import java.io.File
import javax.enterprise.context.Dependent
import javax.inject.Inject

@Dependent
class DataLoader {

    @Inject
    private lateinit var dataFetchService: DataFetchService

    @Inject
    private lateinit var spoqlService: SpoqlService

    @Inject
    private lateinit var ncToKpiTranslator: NCToKpiTranslator

    private val objectMapper: ObjectMapper = ObjectMapper()

    fun loadData(target: ContentItem, kpiName: String) {
        val jobId = dataFetchService.startJob(
                objectMapper.writeValueAsString(
                        GeneralDataSetParameterBuilder.build(target, spoqlService)
                )
        )
        if (dataFetchService.getJobStatus(jobId) == "COMPLETED") {
            val uri = dataFetchService.getJobResult(jobId)[0].downloadUri
            val orderCreationResult = dataFetchService.createOrder(jobId, uri)
            val filePath = "var/jobs/$jobId"
            dataFetchService.downloadResult(orderCreationResult.third.get().message).fileDestination { _, _ -> File(filePath) }
            ncToKpiTranslator.translateFile(filePath, kpiName, target.id)
        }
    }

}