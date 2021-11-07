package ro.hibyte.horus.service

import com.github.kittinunf.fuel.core.ResponseResultOf
import com.github.kittinunf.fuel.core.requests.DownloadRequest
import com.github.kittinunf.fuel.core.requests.download
import ro.hibyte.horus.api.JobModelEntity
import java.util.concurrent.TimeoutException
import javax.enterprise.context.Dependent
import javax.inject.Inject
import kotlin.jvm.Throws

@Dependent
class DataFetchService {

    @Inject
    private lateinit var reqestService: RequestService

    fun startJob(jsonBody: String): String {
        return reqestService.postt("datarequest", jsonBody)
                .responseObject(JobModelEntity.JobCreationResponse.Deserializer())
                .third.get().jobId
    }

    @Throws(TimeoutException::class)
    fun getJobStatus(jobId: String): String {
        var tries= 0
        while (tries<1000){
            tries++
            val status = reqestService.gett("datarequest/status/$jobId")
                    .responseObject(JobModelEntity.JobStatusResponse.Deserializer())
                    .third.get().status
            when(status){
                "completed", "failed" -> return status
            }
            Thread.sleep(1000)
        }
        throw TimeoutException()
    }

    fun getJobResult(jobId: String): List<JobModelEntity.JobResultResponsContent> {
        return reqestService.gett("datarequest/jobs/$jobId/result")
                .responseObject(JobModelEntity.JobResultRespons.Deserializer())
                .third.get().content
    }

    fun createOrder(jobId: String, uri: String): ResponseResultOf<JobModelEntity.OrderCreateResponse> {
        val jsonOrder: String = "{\n" +
                "  \"jobId\": \"$jobId\",\n" +
                "  \"uri\": \"$uri\"\n" +
                "}"
        return reqestService.postt("dataorder", jsonOrder)
                .responseObject(JobModelEntity.OrderCreateResponse.Deserializer())
    }

    fun downloadResult(orderId: String): DownloadRequest {
        return reqestService.gett("dataorder/download/$orderId").download()
    }


}