package at.ac.tuwien.big.entity.transition

import at.ac.tuwien.big.entity.state.RoboticArmState

/**
 * State change of the roboticArm
 */
data class RoboticArmTransition(
        override val startState: RoboticArmState,
        override val targetState: RoboticArmState
) : Transition
