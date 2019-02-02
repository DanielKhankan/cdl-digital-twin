package at.ac.tuwien.big.entity.state

typealias DoubleCompareFunction = (Double?, Double?) -> Boolean

/**
 * Encapsulates sensor signals of one entity at one point in time
 */
interface StateEvent {
    var name: String
    var entity: String

    fun match(other: StateEvent, similar: DoubleCompareFunction): Boolean
}
