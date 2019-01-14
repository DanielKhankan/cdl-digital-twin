package at.ac.tuwien.big.sm

/**
 * State that offers two alternative sequences of [BasicState]. Hence, nested choices are not supported.
 */
data class ChoiceState(
        override var name: String = "Snapshot",
        override var type: String = "ChoiceState",
        var choices: Choices = Choices()
) : StateBase() {
    data class Choices(
            var first: List<BasicState> = emptyList(),
            var second: List<BasicState> = emptyList()
    )
}