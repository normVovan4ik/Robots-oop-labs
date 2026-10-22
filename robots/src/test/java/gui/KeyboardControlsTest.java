package gui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.event.KeyEvent;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class KeyboardControlsTest {

    private final KeyboardControls controls = new KeyboardControls();

    @Test
    void noKeysPressedMeansNoCommands() {
        assertEquals(0, controls.getTurn());
        assertFalse(controls.isForward());
        assertFalse(controls.isAnyKeyPressed());
    }

    @ParameterizedTest
    @ValueSource(ints = {KeyEvent.VK_LEFT, KeyEvent.VK_A})
    void leftKeysTurnLeft(int key) {
        controls.keyPressed(key);

        assertEquals(-1, controls.getTurn());
    }

    @ParameterizedTest
    @ValueSource(ints = {KeyEvent.VK_RIGHT, KeyEvent.VK_D})
    void rightKeysTurnRight(int key) {
        controls.keyPressed(key);

        assertEquals(1, controls.getTurn());
    }

    @ParameterizedTest
    @ValueSource(ints = {KeyEvent.VK_UP, KeyEvent.VK_W})
    void forwardKeysMoveForward(int key) {
        controls.keyPressed(key);

        assertTrue(controls.isForward());
    }

    @Test
    void oppositeTurnKeysCancelEachOther() {
        controls.keyPressed(KeyEvent.VK_LEFT);
        controls.keyPressed(KeyEvent.VK_D);

        assertEquals(0, controls.getTurn());
    }

    @Test
    void unrelatedKeyIsIgnored() {
        controls.keyPressed(KeyEvent.VK_Q);

        assertFalse(controls.isAnyKeyPressed());
    }

    @Test
    void releasedKeyStopsCommand() {
        controls.keyPressed(KeyEvent.VK_UP);
        controls.keyReleased(KeyEvent.VK_UP);

        assertFalse(controls.isForward());
        assertFalse(controls.isAnyKeyPressed());
    }

    @Test
    void releaseAllStopsEverything() {
        controls.keyPressed(KeyEvent.VK_UP);
        controls.keyPressed(KeyEvent.VK_LEFT);

        controls.releaseAll();

        assertFalse(controls.isAnyKeyPressed());
        assertEquals(0, controls.getTurn());
    }

    @Test
    void steerDrivesManuallyWhileKeyIsPressed() {
        RobotModel robot = new RobotModel();
        robot.setTarget(100, 300); // автопилот повернул бы робота вниз
        controls.keyPressed(KeyEvent.VK_UP);

        controls.steer(robot, 10);

        assertTrue(robot.isManual());
        assertEquals(101, robot.getPositionX(), 1e-9);
        assertEquals(0, robot.getDirection(), 1e-12);
    }

    @Test
    void robotStandsStillAfterKeyIsReleased() {
        RobotModel robot = new RobotModel();
        controls.keyPressed(KeyEvent.VK_UP);
        controls.steer(robot, 10);
        controls.keyReleased(KeyEvent.VK_UP);
        double x = robot.getPositionX();

        for (int i = 0; i < 5; i++) {
            controls.steer(robot, 10);
        }

        assertEquals(x, robot.getPositionX(), 1e-9);
    }

    @Test
    void steerFollowsMouseTargetWhenNoKeyIsPressed() {
        RobotModel robot = new RobotModel();
        robot.setTarget(100, 200);

        controls.steer(robot, 10);

        assertFalse(robot.isManual());
        assertEquals(RobotModel.MAX_ANGULAR_VELOCITY * 10, robot.getDirection(), 1e-12);
    }
}