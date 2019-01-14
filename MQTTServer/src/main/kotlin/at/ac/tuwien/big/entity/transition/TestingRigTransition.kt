package at.ac.tuwien.big.entity.transition

import at.ac.tuwien.big.entity.state.TestingRigState

/**
 * State change of the testingRig
 */
data class TestingRigTransition(
        override val startState: TestingRigState,
        override val targetState: TestingRigState
) : Transition
