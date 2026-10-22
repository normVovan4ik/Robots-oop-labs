package gui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class RobotModelTurningTest {

    @Test
    void robotTurnsTheShorterWayAroundZero() {
        RobotModel robot = new RobotModel(); // смотрит вдоль оси X (направление 0)
        robot.setTarget(200, 90);            // цель чуть выше, то есть левее по ходу

        robot.update(10);

        // Короткий путь это поворот влево: направление чуть меньше полного круга
        assertEquals(2 * Math.PI - RobotModel.MAX_ANGULAR_VELOCITY * 10, robot.getDirection(), 1e-9);
    }

    @Test
    void robotReachesTargetOutsideItsTurningCircle() {
        RobotModel robot = new RobotModel();
        robot.setTarget(300, 150);

        for (int i = 0; i < 1000; i++) {
            robot.update(10);
        }

        double distance = Math.hypot(300 - robot.getPositionX(), 150 - robot.getPositionY());
        assertTrue(distance < 1, "robot stopped too far from target: " + distance);
    }
}