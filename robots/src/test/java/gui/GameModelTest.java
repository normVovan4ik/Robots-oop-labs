package gui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class GameModelTest {

    private static final int FIELD_SIZE = 200;

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
            assertTrue(predator.distanceTo(model.getRobot()) > 100);
        }
    }

    @Test
    void predatorGetsCloserToRobot() {
        GameModel model = new GameModel(1);
        RobotModel predator = model.getPredators().get(0);
        double before = predator.distanceTo(model.getRobot());

        for (int i = 0; i < 200; i++) {
            model.update(10, controls);
        }

        assertTrue(predator.distanceTo(model.getRobot()) < before);
    }

    @Test
    void robotTargetIsSetThroughTheModel() {
        GameModel model = new GameModel(0);

        model.setRobotTarget(250, 180);

        assertEquals(250, model.getRobot().getTargetX());
        assertEquals(180, model.getRobot().getTargetY());
    }

    @Test
    void robotAndPredatorsStayInsideField() {
        GameModel model = new GameModel(2);
        model.setFieldSize(FIELD_SIZE, FIELD_SIZE);

        for (int i = 0; i < 3000; i++) {
            model.update(10, controls);
            assertBodyInsideField(model.getRobot());
            model.getPredators().forEach(GameModelTest::assertBodyInsideField);
        }
    }

    private static void assertBodyInsideField(RobotModel robot) {
        double x = robot.getPositionX();
        double y = robot.getPositionY();
        assertTrue(x - robot.getHalfExtentX() >= -1e-9 && x + robot.getHalfExtentX() <= FIELD_SIZE + 1e-9
                        && y - robot.getHalfExtentY() >= -1e-9 && y + robot.getHalfExtentY() <= FIELD_SIZE + 1e-9,
                "left the field: " + x + ", " + y);
    }
}