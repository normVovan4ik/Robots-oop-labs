package gui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class RobotModelManualTest {

    @Test
    void driveForwardMovesAlongDirection() {
        RobotModel robot = new RobotModel(); // робот в (100, 100), смотрит вдоль оси X

        robot.drive(0, true, 10);

        assertEquals(100 + RobotModel.MAX_VELOCITY * 10, robot.getPositionX(), 1e-9);
        assertEquals(100, robot.getPositionY(), 1e-9);
        assertEquals(0, robot.getDirection(), 1e-12);
    }

    @Test
    void driveRightTurnsRobotOnTheSpot() {
        RobotModel robot = new RobotModel();

        robot.drive(1, false, 10);

        assertEquals(RobotModel.MAX_ANGULAR_VELOCITY * 10, robot.getDirection(), 1e-12);
        assertEquals(100, robot.getPositionX(), 1e-9);
        assertEquals(100, robot.getPositionY(), 1e-9);
    }

    @Test
    void driveLeftTurnsRobotTheOtherWay() {
        RobotModel robot = new RobotModel();

        robot.drive(-1, false, 10);

        assertEquals(2 * Math.PI - RobotModel.MAX_ANGULAR_VELOCITY * 10, robot.getDirection(), 1e-9);
    }

    @Test
    void driveForwardStopsAtFieldBorder() {
        RobotModel robot = new RobotModel();
        robot.setFieldSize(200, 200);

        for (int i = 0; i < 200; i++) {
            robot.drive(0, true, 10);
        }

        assertEquals(200 - RobotModel.ROBOT_HALF_SIZE, robot.getPositionX(), 1e-9);
    }

    @Test
    void updateDoesNothingInManualMode() {
        RobotModel robot = new RobotModel(); // цель по умолчанию в (150, 100)
        robot.drive(0, false, 10);
        assertTrue(robot.isManual());

        for (int i = 0; i < 100; i++) {
            robot.update(10);
        }

        assertEquals(100, robot.getPositionX(), 1e-9);
        assertEquals(100, robot.getPositionY(), 1e-9);
    }

    @Test
    void settingTargetReturnsRobotToAutopilot() {
        RobotModel robot = new RobotModel();
        robot.drive(0, false, 10);

        robot.setTarget(150, 100);
        assertFalse(robot.isManual());
        robot.update(10);

        assertEquals(100 + RobotModel.MAX_VELOCITY * 10, robot.getPositionX(), 1e-9);
    }
}