package gui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class RobotModelWallTest {

    private static final int FIELD_SIZE = 200;

    @Test
    void bodyExtentDependsOnDirection() {
        RobotModel robot = new RobotModel(); // смотрит вдоль оси X

        assertEquals(RobotModel.ROBOT_HALF_SIZE, robot.getHalfExtentX(), 1e-9);
        assertEquals(RobotModel.ROBOT_HALF_WIDTH, robot.getHalfExtentY(), 1e-9);

        turnToFaceDown(robot);

        assertEquals(RobotModel.ROBOT_HALF_WIDTH, robot.getHalfExtentX(), 0.05);
        assertEquals(RobotModel.ROBOT_HALF_SIZE, robot.getHalfExtentY(), 0.05);
    }

    @Test
    void robotFacingSidewaysComesCloserToWallThanRobotFacingIt() {
        RobotModel robot = new RobotModel(199, 100, RobotModel.MAX_VELOCITY, RobotModel.MAX_ANGULAR_VELOCITY);
        turnToFaceDown(robot);

        robot.setFieldSize(FIELD_SIZE, FIELD_SIZE);

        // Центр стоит от правой стенки на половину ширины робота (около 5 px), а не на полдлины (15 px)
        assertEquals(FIELD_SIZE - robot.getHalfExtentX(), robot.getPositionX(), 1e-9);
        assertTrue(FIELD_SIZE - robot.getPositionX() < RobotModel.ROBOT_HALF_SIZE / 2);
    }

    @Test
    void robotStopsAtWallWhenTargetIsBehindIt() {
        RobotModel robot = new RobotModel();
        robot.setFieldSize(FIELD_SIZE, FIELD_SIZE);
        robot.setTarget(199, 100); // цель у самой стенки: центр туда не дотянется

        run(robot, 500);
        double x = robot.getPositionX();
        double direction = robot.getDirection();
        run(robot, 100);

        assertEquals(FIELD_SIZE - RobotModel.ROBOT_HALF_SIZE, x, 1e-9);
        assertEquals(x, robot.getPositionX(), 1e-9);
        assertEquals(direction, robot.getDirection(), 1e-12);
    }

    @Test
    void keyboardDrivenRobotReachesWallWithItsNose() {
        RobotModel robot = new RobotModel();
        robot.setFieldSize(FIELD_SIZE, FIELD_SIZE);

        for (int i = 0; i < 300; i++) {
            robot.drive(0, true, 10);
        }

        assertEquals(FIELD_SIZE - RobotModel.ROBOT_HALF_SIZE, robot.getPositionX(), 1e-9);
    }

    private static void turnToFaceDown(RobotModel robot) {
        for (int i = 0; i < 79; i++) {
            robot.drive(1, false, 10);
        }
    }

    private static void run(RobotModel robot, int steps) {
        for (int i = 0; i < steps; i++) {
            robot.update(10);
        }
    }
}