import GameLogic

def main():
    base_joint = GameLogic.getCurrentScene().objects["base_joint"]
    main_arm = GameLogic.getCurrentScene().objects["main_arm"]
    second_arm = GameLogic.getCurrentScene().objects["second_arm"]
    wrist = GameLogic.getCurrentScene().objects["wrist"]
    hand = GameLogic.getCurrentScene().objects["BarrettHand"]
    gripper = GameLogic.getCurrentScene().objects["Finger1.1"]
    slider = GameLogic.getCurrentScene().objects["SliderPanel"]
    adjuster = GameLogic.getCurrentScene().objects["Adjuster1"]

    base_position = base_joint.localOrientation.to_euler().z
    main_arm_position = main_arm.localOrientation.to_euler().y
    second_arm_position = second_arm.localOrientation.to_euler().y
    wrist_position = wrist.localOrientation.to_euler().x
    hand_position = hand.worldPosition
    gripper_position = gripper.localOrientation.to_euler().y
    slider_position = slider.worldPosition.x
    adjuster_position = adjuster.worldPosition.x

    GameLogic.server.client.publish("Sensor", payload="base " + str(base_position), qos=0, retain=False)
    GameLogic.server.client.publish("Sensor", payload="main_arm " + str(main_arm_position), qos=0, retain=False)
    GameLogic.server.client.publish("Sensor", payload="second_arm " + str(second_arm_position), qos=0, retain=False)
    GameLogic.server.client.publish("Sensor", payload="wrist " + str(wrist_position), qos=0, retain=False)
    GameLogic.server.client.publish("Sensor", payload="hand " + str(hand_position), qos=0, retain=False)
    GameLogic.server.client.publish("Sensor", payload="slider " + str(slider_position), qos=0, retain=False)
    GameLogic.server.client.publish("Sensor", payload="gripper " + str(gripper_position), qos=0, retain=False)
    GameLogic.server.client.publish("Sensor", payload="adjuster " + str(adjuster_position), qos=0, retain=False)