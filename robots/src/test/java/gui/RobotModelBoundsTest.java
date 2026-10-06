package gui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class RobotModelBoundsTest {

    private static final int FIELD_SIZE = 200;
    private static final double MIN = RobotModel.ROBOT_HALF_SIZE;
    private static final double MAX = FIELD_SIZE - RobotModel.ROBOT_HALF_SIZE;

    @Test
    void robotCirclingAroundTargetNeverLeavesField() {
        RobotModel robot = new RobotModel();
        robot.setFieldSize(FIELD_SIZE, FIELD_SIZE);
        // цель у самого края: робот поворачивает по дуге и раньше «выносило» за границу
        robot.setTarget(100, 190);

        for (int i = 0; i < 200_000; i++) {
            robot.update(10);
            assertInsideField(robot);
        }
    }

    @Test
    void robotHeadingStraightAtWallStopsAtBorder() {
        RobotModel robot = new RobotModel();
        robot.setFieldSize(FIELD_SIZE, FIELD_SIZE);
        robot.setTarget(1000, 100); // цель за правой границей

        for (int i = 0; i < 1000; i++) {
            robot.update(10);
        }

        assertEquals(MAX, robot.getPositionX(), 1e-9);
    }

    @Test
    void robotIsReturnedInsideWhenFieldShrinks() {
        RobotModel robot = new RobotModel(); // робот в (100, 100)

        robot.setFieldSize(50, 50);

        assertEquals(35, robot.getPositionX(), 1e-9);
        assertEquals(35, robot.getPositionY(), 1e-9);
    }

    @Test
    void tinyFieldKeepsRobotInCenter() {
        RobotModel robot = new RobotModel();

        robot.setFieldSize(20, 20);

        assertEquals(10, robot.getPositionX(), 1e-9);
        assertEquals(10, robot.getPositionY(), 1e-9);
    }

    @Test
    void robotHasNoLimitsWhileFieldSizeIsUnknown() {
        RobotModel robot = new RobotModel();
        robot.setTarget(1000, 100);

        for (int i = 0; i < 600; i++) {
            robot.update(10);
        }

        assertTrue(robot.getPositionX() > 500);
    }

    private static void assertInsideField(RobotModel robot) {
        double x = robot.getPositionX();
        double y = robot.getPositionY();
        assertTrue(x >= MIN && x <= MAX && y >= MIN && y <= MAX,
                "robot left the field: " + x + ", " + y);
    }
}