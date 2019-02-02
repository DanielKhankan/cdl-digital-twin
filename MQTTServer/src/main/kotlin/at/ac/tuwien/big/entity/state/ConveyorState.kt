package at.ac.tuwien.big.entity.state

/**
 * State of the conveyor
 */
data class ConveyorState(
        override var name: String = "Snapshot",
        override var entity: String = "Conveyor",
        val inPickupWindow: Boolean? = null,
        val detected: Boolean? = null,
        val adjusterPosition: Double? = null
) : StateEvent {
    override fun match(other: StateEvent, similar: DoubleCompareFunction): Boolean {
        return if (other is ConveyorState) {
            return similar(this.adjusterPosition, other.adjusterPosition)
                    && (other.detected == null || this.detected == other.detected)
                    && (other.inPickupWindow == null || this.inPickupWindow == other.inPickupWindow)
        } else {
            false
        }
    }
}
