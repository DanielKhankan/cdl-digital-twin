package at.ac.tuwien.big

import at.ac.tuwien.big.entity.state.ConveyorState
import org.junit.Assert
import org.junit.Test

class ConveyorStateTest {

    @Test
    fun matchNotEqualBooleanProps() {

        // Arrange
        val state1 = ConveyorState(detected = false, inPickupWindow = false)
        val state2 = ConveyorState(detected = true, inPickupWindow = true)

        // Act
        val match = state1.match(state2, singleAccuracy)

        // Assert
        Assert.assertFalse(match)
    }

    @Test
    fun matchNotEqualPosition() {

        // Arrange
        val state1 = ConveyorState(adjusterPosition = 0.5)
        val state2 = ConveyorState(adjusterPosition = 1.6)

        // Act
        val match = state1.match(state2, singleAccuracy)

        // Assert
        Assert.assertFalse(match)
    }

    @Test
    fun matchNotEqualThisNull() {

        // Arrange
        val state1 = ConveyorState(detected = null, inPickupWindow = null)
        val state2 = ConveyorState(detected = true, inPickupWindow = true)

        // Act
        val match = state1.match(state2, singleAccuracy)

        // Assert
        Assert.assertFalse(match)
    }

    @Test
    fun matchEqual() {

        // Arrange
        val state1 = ConveyorState(detected = true, inPickupWindow = true, adjusterPosition = 0.5)
        val state2 = ConveyorState(detected = true, inPickupWindow = true, adjusterPosition = 0.5)

        // Act
        val match = state1.match(state2, singleAccuracy)

        // Assert
        Assert.assertTrue(match)
    }

    @Test
    fun matchEqualOtherNull() {

        // Arrange
        val state1 = ConveyorState(detected = true, inPickupWindow = true, adjusterPosition = 0.5)
        val state2 = ConveyorState(detected = null, inPickupWindow = null, adjusterPosition = 0.5)

        // Act
        val match = state1.match(state2, singleAccuracy)

        // Assert
        Assert.assertTrue(match)
    }

    @Test
    fun matchEqualOtherPositionNull() {

        // Arrange
        val state1 = ConveyorState(detected = true, inPickupWindow = true, adjusterPosition = 0.5)
        val state2 = ConveyorState(detected = true, inPickupWindow = true, adjusterPosition = null)

        // Act
        val match = state1.match(state2, singleAccuracy)

        // Assert
        Assert.assertTrue(match)
    }
}