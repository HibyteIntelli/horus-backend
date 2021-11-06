package ro.hibyte.horus.service

import ro.hibyte.horus.api.JobModelEntity

class DataFetchService {

    private lateinit var reqestService: RequestService

    private fun startJob(params: List<Pair<String, Any>>) {
        //TODO: get location AOI
        reqestService.post("datarequest", params).
    }
}