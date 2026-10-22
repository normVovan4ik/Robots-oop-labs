package gui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class RobotModelSettingsTest {

    @Test
    void robotStartsAtGivenPosition() {
        RobotModel robot = new RobotModel(30, 40, 0.1, 0.001);

        assertEquals(30, robot.getPositionX(), 1e-9);
        assertEquals(40, robot.getPositionY(), 1e-9);
    }

    @Test
    void customMaxVelocityIsUsed() {
        RobotModel robot = new RobotModel(100, 100, 0.05, 0.001);
        robot.setTarget(150, 100);

        robot.update(10);

        assertEquals(100.5, robot.getPositionX(), 1e-9);
    }

    @Test
    void customMaxAngularVelocityIsUsed() {
        RobotModel robot = new RobotModel(100, 100, 0.1, 0.002);
        robot.setTarget(100, 200); // цель строго «вниз»

        robot.update(10);

        assertEquals(0.02, robot.getDirection(), 1e-12);
    }
}