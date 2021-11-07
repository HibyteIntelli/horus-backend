package ro.hibyte.horus.service.images

import ro.hibyte.horus.service.DataFetchService
import ro.hibyte.horus.service.RequestService
import java.time.LocalDateTime
import javax.enterprise.context.Dependent
import javax.inject.Inject

const val DATASETID = "EO:ESA:DAT:SENTINEL-1:SAR"

@Dependent
class ImageFetchService {

    @Inject
    private lateinit var reqestService: RequestService

    @Inject
    private lateinit var dataFetchService: DataFetchService

    fun fetchDataForLocation(targetId: Long) {
        //get AOI
        val bbox = listOf(-15.709664306450623,
                82.38501393101397,
                -15.707977462128701,
                82.38540510423468)
        //get range
        val start = "2021-07-25T00:00:00.000Z"
        val end = "2021-07-28T00:00:00.000Z"

        val jobParams = listOf(
            "datasetId" to DATASETID,
            "boundingBoxValues" to listOf(
                    "name" to "bbox",
                    "bbox" to bbox
                    ),
            "dateRangeSelectValues" to listOf(
                    "name" to "position",
                    "start" to start,
                    "end" to end
                    ),
            "stringChoiceValues" to listOf(
                    "name" to "processingLevel",
                    "value" to "LEVEL1C"
            )
        )

        val jobParamsString = "{\n" +
                "    \"datasetId\": \"EO:ESA:DAT:SENTINEL-2:MSI\",\n" +
                "    \"boundingBoxValues\": [\n" +
                "        {\n" +
                "            \"name\": \"bbox\",\n" +
                "            \"bbox\": [\n" +
                "                -15.709664306450623,\n" +
                "                82.38501393101397,\n" +
                "                -15.707977462128701,\n" +
                "                82.38540510423468\n" +
                "            ]\n" +
                "        }\n" +
                "    ],\n" +
                "    \"dateRangeSelectValues\": [\n" +
                "        {\n" +
                "            \"name\": \"position\",\n" +
                "            \"start\": \"2021-07-25T00:00:00.00Z\",\n" +
                "            \"end\": \"2021-07-28T00:00:00.000Z\"\n" +
                "        }\n" +
                "    ],\n" +
                "    \"stringChoiceValues\": [\n" +
                "        {\n" +
                "            \"name\": \"processingLevel\",\n" +
                "            \"value\": \"LEVEL1C\"\n" +
                "        }\n" +
                "    ]\n" +
                "}"

        val jobId = dataFetchService.startJob(jobParamsString)
        if(dataFetchService.getJobStatus(jobId) != "completed"){
            //to fallback
        } else {
            val contents = dataFetchService.getJobResult(jobId)
            val orderSource = contents.sortedBy { LocalDateTime.parse(it.productInfo.productStartDate) }.firstOrNull()

            if(!orderSource?.order?.status.equals("completed")){
                val orderObj = orderSource?.url?.let { dataFetchService.createOrder(jobId, it) }
                Thread.sleep(5000)
                orderObj?.third?.get()?.orderId?.let { dataFetchService.downloadResult(it) }
            }


        }
    }


}