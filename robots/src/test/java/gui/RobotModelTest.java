package gui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class RobotModelTest {

    @Test
    void robotMovesTowardTarget() {
        RobotModel robot = new RobotModel(); // робот в (100, 100), цель в (150, 100)

        robot.update(10);

        assertEquals(101, robot.getPositionX(), 1e-9);
        assertEquals(100, robot.getPositionY(), 1e-9);
    }

    @Test
    void robotStaysInPlaceWhenAlreadyAtTarget() {
        RobotModel robot = new RobotModel();
        robot.setTarget(100, 100);

        robot.update(10);

        assertEquals(100, robot.getPositionX(), 1e-9);
        assertEquals(100, robot.getPositionY(), 1e-9);
        assertEquals(0, robot.getDirection(), 1e-9);
    }

    @Test
    void robotTurnsTowardTarget() {
        RobotModel robot = new RobotModel();
        robot.setTarget(100, 200); // цель строго «вниз», угол pi/2

        robot.update(10);

        assertEquals(RobotModel.MAX_ANGULAR_VELOCITY * 10, robot.getDirection(), 1e-12);
    }

    @Test
    void directionStaysWithinFullCircle() {
        RobotModel robot = new RobotModel();
        robot.setTarget(100, 300);

        for (int i = 0; i < 100_000; i++) {
            robot.update(10);
            assertTrue(robot.getDirection() >= 0 && robot.getDirection() < 2 * Math.PI);
        }
    }
}