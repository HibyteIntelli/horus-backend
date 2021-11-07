package ro.hibyte.horus.dataModel

data class LocationPoint(
    var latitude: Double,
    var longitude: Double
) {

    constructor() : this(.0, .0)
}
