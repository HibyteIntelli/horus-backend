package ro.hibyte.horus.service.images

import ro.hibyte.horus.service.RequestService
import javax.enterprise.context.ApplicationScoped

const val DATASETID = "EO:ESA:DAT:SENTINEL-1:SAR"

@ApplicationScoped
class ImageFetchService {

    private lateinit var reqestService: RequestService

    fun fetchDataForLocation() {}


}