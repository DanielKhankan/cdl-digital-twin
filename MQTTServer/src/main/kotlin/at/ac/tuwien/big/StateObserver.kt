package at.ac.tuwien.big

import at.ac.tuwien.big.entity.state.*
import at.ac.tuwien.big.entity.transition.*
import at.ac.tuwien.big.sm.BasicState

/**
 * This object maintains the current state of the environment and tries to match the current state to states defined
 * in the job. By finding successor states to currently matched states, the system follows the control procedure.
 * Internally, the object uses [StateMachine] to find the correct successor states.
 */
object StateObserver : Observable<BasicState>() {

    /**
     * State machine used to find correct successor states
     */
    var stateMachine: StateMachine? = null

    /**
     * Most recently observed state
     */
    private var snapshot: Environment = Environment(RoboticArmState(), ConveyorState(), TestingRigState(), SliderState())

    /**
     * Latest matching state, given the current job
     */
    var latestMatch: Pair<BasicState, Boolean> = Pair(BasicState(), true)
        private set

    /**
     * Successor state, given the latest matching state
     */
    var targetState: BasicState = BasicState()
        private set

    /**
     * Update the unit with new sensor information. This includes matching the updated state to the set of defined states.
     */
    fun update(e: StateEvent) {
        when (e) {
            is RoboticArmState -> {
                val currentState = snapshot.roboticArmState ?: RoboticArmState()
                val newState = currentState.copy(
                        handPosition = e.handPosition ?: currentState.handPosition)
                snapshot = snapshot.copy(roboticArmState = newState)
            }
            is ConveyorState -> {
                val currentState = snapshot.conveyorState ?: ConveyorState()
                val newState = currentState.copy(
                    inPickupWindow = e.inPickupWindow ?: currentState.inPickupWindow,
                    detected = e.detected ?: currentState.detected,
                    adjusterPosition = e.adjusterPosition ?: currentState.adjusterPosition)
                snapshot = snapshot.copy(conveyorState = newState)
            }
            is TestingRigState -> {
                val currentState = snapshot.testingRigState ?: TestingRigState()
                val newState = currentState.copy(
                    objectCategory = e.objectCategory ?: currentState.objectCategory,
                    platformPosition = e.platformPosition ?: currentState.platformPosition,
                    heatplateTemperature = e.heatplateTemperature ?: currentState.heatplateTemperature)
                snapshot = snapshot.copy(testingRigState = newState)
            }
            is SliderState -> {
                snapshot = snapshot.copy(sliderState = e)
            }
        }
        val match = matchState(snapshot)
        if (match != null && latestMatch != match) {
            latestMatch = match
            notify(latestMatch.first)
        }
    }

    /**
     * Return the defined successor state of the latest matching state, according to the state machine
     */
    fun next(): List<Transition> {
        val successor = stateMachine?.successor(latestMatch.first, latestMatch.second)
        return if (successor != null) {
            targetState = successor
            val env = if (latestMatch.second) {
                latestMatch.first.environment
            } else {
                latestMatch.first.altEnvironment!!
            }
            val succ = successor.environment
            val result = mutableListOf<Transition>()
            if (succ.roboticArmState != null) {
                result.add(RoboticArmTransition(env.roboticArmState ?: RoboticArmState(), succ.roboticArmState))
            }
            if (succ.conveyorState != null) {
                result.add(ConveyorTransition(env.conveyorState ?: ConveyorState(), succ.conveyorState))
            }
            if (succ.testingRigState != null) {
                result.add(TestingRigTransition(env.testingRigState ?: TestingRigState(), succ.testingRigState))
            }
            if (succ.sliderState != null) {
                result.add(SliderTransition(env.sliderState ?: SliderState(), succ.sliderState))
            }
            return result
        } else {
            emptyList()
        }
    }

    /**
     * Return the defined successor state of the latest matching state, according to the state machine
     */
    fun reset(): List<Transition> {
        val successor = stateMachine?.states?.first() as BasicState
        val env = (stateMachine?.states?.first() as BasicState?)?.environment
        targetState = successor
        return if (env != null) {
            listOf(
                    RoboticArmTransition(RoboticArmState(), env.roboticArmState ?: RoboticArmState()),
                    ConveyorTransition(ConveyorState(), env.conveyorState ?: ConveyorState()),
                    SliderTransition(SliderState(), env.sliderState ?: SliderState()),
                    TestingRigTransition(TestingRigState(), env.testingRigState ?: TestingRigState())
            )
        } else {
            emptyList()
        }
    }

    fun atEndState() = stateMachine?.isEndState(latestMatch.first) ?: true

    /**
     * Transform successor into 'goto' commands for the MQTT API
     */
    fun transform(transition: Transition): List<String> {
        return when (transition) {
            is RoboticArmTransition -> {
                val target = transition.targetState
                listOf(
                        "base-goto ${target.basePosition} ${transition.baseSpeed}",
                        "main-arm-goto ${target.mainArmPosition} ${transition.mainArmSpeed}",
                        "second-arm-goto ${target.secondArmPosition} ${transition.secondArmSpeed}",
                        "head-goto ${target.headPosition} 1.0",
                        "head-mount-goto ${target.headMountPosition} 1.0",
                        "gripper-goto ${target.gripperPosition} 1.0"
                )
            }
            is SliderTransition -> {
                listOf("slider-goto ${transition.targetState.sliderPosition}")
            }
            is ConveyorTransition -> {
                val t = transition.targetState
                listOf(
                        if (t.adjusterPosition != null) {
                            "adjuster-goto ${transition.targetState.adjusterPosition}"
                        } else {
                            ""
                        }).filter { !it.isEmpty() }
            }
            is TestingRigTransition -> {
                val t = transition.targetState
                listOf(
                        if (t.platformPosition != null) {
                            "platform-goto ${t.platformPosition}"
                        } else {
                            ""
                        },
                        if (t.heatplateTemperature != null) {
                            "platform-heatup ${t.heatplateTemperature}"
                        } else {
                            ""
                        }
                ).filter { !it.isEmpty() }
            }
            else -> emptyList()
        }
    }

    /**
     * Match the given state against all states defined in this class and returns a match
     * @return the matching state or null, if no match was found
     */
    private fun matchState(env: Environment): Pair<BasicState, Boolean>? {

        val all = stateMachine?.all() ?: emptyList()

        val matches = all.filter {
            env.matches(it.environment) || (it.altEnvironment != null && env.matches(it.altEnvironment!!))
        }

        return if (matches.isNotEmpty()) {
            val match = matches.first()
            Pair(match, env.matches(match.environment))
        } else {
            null
        }
    }
}
