package gui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class RobotModelPositionTest {

    @Test
    void distanceToOtherRobotIsMeasuredBetweenCenters() {
        RobotModel first = new RobotModel(0, 0, 0.1, 0.001);
        RobotModel second = new RobotModel(3, 4, 0.1, 0.001);

        assertEquals(5, first.distanceTo(second), 1e-9);
        assertEquals(5, second.distanceTo(first), 1e-9);
    }

    @Test
    void distanceToItselfIsZero() {
        RobotModel robot = new RobotModel();

        assertEquals(0, robot.distanceTo(robot), 1e-9);
    }

    @Test
    void chaseMakesPositionOfOtherRobotTheTarget() {
        RobotModel predator = new RobotModel();
        RobotModel prey = new RobotModel(10.4, 20.6, 0.1, 0.001);

        predator.chase(prey);

        assertEquals(10, predator.getTargetX());
        assertEquals(21, predator.getTargetY());
    }
}