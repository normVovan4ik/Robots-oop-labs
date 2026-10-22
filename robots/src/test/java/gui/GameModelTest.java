package gui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class GameModelTest {

    private final KeyboardControls controls = new KeyboardControls();

    @Test
    void startsWithRequestedNumberOfPredators() {
        assertEquals(2, new GameModel(2).getPredators().size());
        assertEquals(0, new GameModel(0).getPredators().size());
    }

    @Test
    void predatorsStartFarFromRobot() {
        GameModel model = new GameModel(2);

        for (RobotModel predator : model.getPredators()) {
            assertTrue(distance(model.getRobot(), predator) > 100);
        }
    }

    @Test
    void predatorGetsCloserToRobot() {
        GameModel model = new GameModel(1);
        RobotModel predator = model.getPredators().get(0);
        double before = distance(model.getRobot(), predator);

        for (int i = 0; i < 200; i++) {
            model.update(10, controls);
        }

        assertTrue(distance(model.getRobot(), predator) < before);
    }

    @Test
    void robotAndPredatorsStayInsideField() {
        GameModel model = new GameModel(2);
        model.setFieldSize(200, 200);

        for (int i = 0; i < 3000; i++) {
            model.update(10, controls);
            assertInside(model.getRobot());
            model.getPredators().forEach(GameModelTest::assertInside);
        }
    }

    private static void assertInside(RobotModel model) {
        double min = RobotModel.ROBOT_HALF_SIZE;
        double max = 200 - RobotModel.ROBOT_HALF_SIZE;
        assertTrue(model.getPositionX() >= min && model.getPositionX() <= max
                        && model.getPositionY() >= min && model.getPositionY() <= max,
                "left the field: " + model.getPositionX() + ", " + model.getPositionY());
    }

    private static double distance(RobotModel a, RobotModel b) {
        return Math.hypot(a.getPositionX() - b.getPositionX(), a.getPositionY() - b.getPositionY());
    }
}