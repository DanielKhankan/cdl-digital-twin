package at.ac.tuwien.big

import at.ac.tuwien.big.entity.state.StateEvent
import org.influxdb.InfluxDB
import org.influxdb.InfluxDB.ConsistencyLevel
import org.influxdb.InfluxDBFactory
import org.influxdb.dto.BatchPoints
import org.influxdb.dto.Point
import java.util.concurrent.TimeUnit
import kotlin.reflect.full.memberProperties

/**
 * Store data points in a time series database
 */
class TimeSeriesDatabase(host: String) {

    private val dbName = "pick-and-place"
    private var influxDB: InfluxDB = InfluxDBFactory.connect("http://$host:8086", "root", "root")

    init {
        influxDB.createDatabase(dbName)
        influxDB.enableBatch(100, 1000, TimeUnit.MILLISECONDS)
    }

    /**
     * Reset the database
     */
    fun resetDatabase() {
        influxDB.deleteDatabase(dbName)
        influxDB.createDatabase(dbName)
    }

    /**
     * Save a robotic arm measurement point together with a reference point.
     */
    fun savePoint(state: StateEvent, label: String? = null) {
        val batchPoints = BatchPoints
                .database(dbName)
                .consistency(ConsistencyLevel.ALL)
                .build()
        val pointBuilder = Point.measurement(state.name)
                .time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)

        val kotlinClass = state.javaClass.kotlin

        val iterator = kotlinClass.memberProperties
        iterator.forEach {
            pointBuilder.addField(it.name, it.get(state).toString())
            println("The element is $it")
        }

        val point = if (label != null) {
            pointBuilder.addField("label", label).build()
        } else {
            pointBuilder.build()
        }
        batchPoints.point(point)
        influxDB.write(batchPoints)
    }
}
