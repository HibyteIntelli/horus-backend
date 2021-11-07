package ro.hibyte.horus.service

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
    fun getJobResult(jobId: String): String {
        var tries= 0
        while (tries<1000){
            tries++
            val status = reqestService.gett("status/"+jobId)
                    .responseObject(JobModelEntity.JobStatusResponse.Deserializer())
                    .third.get().status
            when(status){
                "completed", "failed" -> return status
            }
            Thread.sleep(1000)
        }
        throw TimeoutException()
    }


}