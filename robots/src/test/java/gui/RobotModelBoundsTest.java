package gui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class RobotModelBoundsTest {

    private static final int FIELD_SIZE = 200;

    @Test
    void robotCirclingAroundTargetNeverLeavesField() {
        RobotModel robot = new RobotModel();
        robot.setFieldSize(FIELD_SIZE, FIELD_SIZE);
        robot.setTarget(100, 190);

        for (int i = 0; i < 200_000; i++) {
            robot.update(10);
            assertBodyInsideField(robot);
        }
    }

    @Test
    void robotHeadingStraightAtWallStopsAtBorder() {
        RobotModel robot = new RobotModel();
        robot.setFieldSize(FIELD_SIZE, FIELD_SIZE);
        robot.setTarget(1000, 100);

        for (int i = 0; i < 1000; i++) {
            robot.update(10);
        }

        assertEquals(FIELD_SIZE - RobotModel.ROBOT_HALF_SIZE, robot.getPositionX(), 1e-9);
    }

    @Test
    void robotIsReturnedInsideWhenFieldShrinks() {
        RobotModel robot = new RobotModel(); // в (100, 100), смотрит вдоль оси X

        robot.setFieldSize(50, 50);

        assertEquals(50 - RobotModel.ROBOT_HALF_SIZE, robot.getPositionX(), 1e-9);
        assertEquals(50 - RobotModel.ROBOT_HALF_WIDTH, robot.getPositionY(), 1e-9);
    }

    @Test
    void robotIsKeptInsideEvenInTinyField() {
        RobotModel robot = new RobotModel();

        robot.setFieldSize(20, 20);

        // По горизонтали робот длиннее поля и встаёт в центр, по вертикали упирается в нижнюю стенку
        assertEquals(10, robot.getPositionX(), 1e-9);
        assertEquals(20 - RobotModel.ROBOT_HALF_WIDTH, robot.getPositionY(), 1e-9);
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

    private static void assertBodyInsideField(RobotModel robot) {
        double x = robot.getPositionX();
        double y = robot.getPositionY();
        assertTrue(x - robot.getHalfExtentX() >= -1e-9 && x + robot.getHalfExtentX() <= FIELD_SIZE + 1e-9
                        && y - robot.getHalfExtentY() >= -1e-9 && y + robot.getHalfExtentY() <= FIELD_SIZE + 1e-9,
                "robot body left the field: " + x + ", " + y);
    }
}