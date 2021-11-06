package ro.hibyte.horus.gps

import kotlin.math.atan2

class InsidePolygon {

    private var pi = 3.14159265
    private var twopi = 2 * pi

    fun main(args: Array<String>) {
        val lat_array = ArrayList<Double>()
        val long_array = ArrayList<Double>()

        //This is the polygon bounding box, if you plot it,
        //you'll notice it is a rough tracing of the parameter of
        //the state of Florida starting at the upper left, moving
        //clockwise, and finishing at the upper left corner of florida.
        val polygon_lat_long_pairs = ArrayList<String>()
        polygon_lat_long_pairs.add("31.000213,-87.584839")
        //lat/long of upper left tip of florida.
        polygon_lat_long_pairs.add("31.009629,-85.003052")
        polygon_lat_long_pairs.add("30.726726,-84.838257")
        polygon_lat_long_pairs.add("30.584962,-82.168579")
        polygon_lat_long_pairs.add("30.73617,-81.476441")
        //lat/long of upper right tip of florida.
        polygon_lat_long_pairs.add("29.002375,-80.795288")
        polygon_lat_long_pairs.add("26.896598,-79.938355")
        polygon_lat_long_pairs.add("25.813738,-80.059204")
        polygon_lat_long_pairs.add("24.93028,-80.454712")
        polygon_lat_long_pairs.add("24.401135,-81.817017")
        polygon_lat_long_pairs.add("24.700927,-81.959839")
        polygon_lat_long_pairs.add("24.950203,-81.124878")
        polygon_lat_long_pairs.add("26.0015,-82.014771")
        polygon_lat_long_pairs.add("27.833247,-83.014527")
        polygon_lat_long_pairs.add("28.8389,-82.871704")
        polygon_lat_long_pairs.add("29.987293,-84.091187")
        polygon_lat_long_pairs.add("29.539053,-85.134888")
        polygon_lat_long_pairs.add("30.272352,-86.47522")
        polygon_lat_long_pairs.add("30.281839,-87.628784")

        //Convert the strings to doubles.
        for (s in polygon_lat_long_pairs) {
            lat_array.add(s.split(",").toTypedArray()[0].toDouble())
            long_array.add(s.split(",").toTypedArray()[1].toDouble())
        }

        //prints TRUE true because the lat/long passed in is
        //inside the bounding box.
        println(
            coordinate_is_inside_polygon(
                25.7814014, -80.186969,
                lat_array, long_array
            )
        )

        //prints FALSE because the lat/long passed in
        //is Not inside the bounding box.
        println(
            coordinate_is_inside_polygon(
                25.831538, -1.069338,
                lat_array, long_array
            )
        )
    }

    public fun coordinate_is_inside_polygon(
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



}