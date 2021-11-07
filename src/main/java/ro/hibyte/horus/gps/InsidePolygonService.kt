package ro.hibyte.horus.gps

import ro.hibyte.horus.dataModel.LocationPoint
import kotlin.math.atan2

const val pi = 3.14159265
const val twopi = 2 * pi

object InsidePolygonService{

    fun coordinate_is_inside_polygon(
        latitude: Double, longitude: Double,
        lat_array: ArrayList<Double>, long_array: ArrayList<Double>
    ): Boolean {
        var i: Int
        var angle = 0.0
        var point1_lat: Double
        var point1_long: Double
        var point2_lat: Double
        var point2_long: Double
        val n = lat_array.size
        i = 0
        while (i < n) {
            point1_lat = lat_array[i] - latitude
            point1_long = long_array[i] - longitude
            point2_lat = lat_array[(i + 1) % n] - latitude
            //you should have paid more attention in high school geometry.
            point2_long = long_array[(i + 1) % n] - longitude
            angle += Angle2D(point1_lat, point1_long, point2_lat, point2_long)
            i++
        }
        return Math.abs(angle) >= pi
    }

    private fun Angle2D(y1: Double, x1: Double, y2: Double, x2: Double): Double {
        var dtheta: Double
        val theta1: Double
        val theta2: Double
        theta1 = atan2(y1, x1)
        theta2 = atan2(y2, x2)
        dtheta = theta2 - theta1
        while (dtheta > pi) dtheta -= twopi
        while (dtheta < -pi) dtheta += twopi
        return dtheta
    }

    fun is_valid_gps_coordinate(
        latitude: Double,
        longitude: Double
    ): Boolean {
        //This is a bonus function, it's unused, to reject invalid lat/longs.
        return latitude > -90 && latitude < 90 && longitude > -180 && longitude < 180
    }

    private fun find_Centroid(v: ArrayList<LocationPoint>): LocationPoint {
        var ans = LocationPoint(0.0,0.0)
        val n = v.size
        var signedArea = 0.0

        // For all vertices
        for (i in 0 until n) {   // 0 -> x 1 -> y
            val x0 = v[i].latitude
            val y0 = v[i].longitude
            val x1 = v[(i + 1) % n].latitude
            val y1 = v[(i + 1) % n].longitude

            // Calculate value of A
            // using shoelace formula
            val A = x0 * y1 - x1 * y0
            signedArea += A

            // Calculating coordinates of
            // centroid of polygon
            ans.latitude += (x0 + x1) * A
            ans.longitude += (y0 + y1) * A
        }
        signedArea *= 0.5
        ans.latitude = ans.latitude / (6 * signedArea)
        ans.longitude = ans.longitude / (6 * signedArea)
        return ans
    }

    // find the farthest points

    private fun farthersPoint(list: ArrayList<LocationPoint>, centroid: LocationPoint): ArrayList<LocationPoint> {
        var longX = 0.0
        var longY = 0.0

        list.forEach { point ->
            run {
                if(Math.abs(centroid.latitude - point.latitude) > longX) {
                    longX = Math.abs(centroid.latitude - point.latitude)
                }
                if(Math.abs(centroid.longitude - point.longitude) > longY) {
                    longY = Math.abs(centroid.longitude - point.longitude)
                }
            }

        }
        var locationPoint1 = LocationPoint(centroid.latitude + longX, centroid.longitude + longY)
        var locationPoint2 = LocationPoint(centroid.latitude - longX, centroid.longitude - longY)
        var data = ArrayList<LocationPoint>()
        data.add(locationPoint1)
        data.add(locationPoint2)
        return data
    }


    fun generate2Points(list: List<LocationPoint>): ArrayList<LocationPoint> {
        return farthersPoint(list as ArrayList<LocationPoint>, find_Centroid(list))
    }

    fun getPoints(): List<LocationPoint> {
        var list = ArrayList<LocationPoint>()
        list.add(LocationPoint(0.0, 1.1))
        list.add(LocationPoint(0.0, 1.1))
        list.add(LocationPoint(0.0, 1.1))
        list.add(LocationPoint(0.0, 1.1))
        return list
    }

}